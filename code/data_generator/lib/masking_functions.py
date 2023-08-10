import pandas as pd
import numpy as np
import re
import matplotlib.pyplot as plt
import math
import pandasql as ps
import time
import string
from typing import Any, Callable
import copy


def bucketize_age(age, b_size):
    l = age // b_size
    h = l+1
    return '['+str(l*b_size)+' - '+str(h*b_size-1)+']'

def blur_zip(zip_code, nFields):
    assert(nFields <= 6 and nFields > 0)
    return zip_code[:-nFields]+("X"*nFields)

def generalize_diagnosis(diagnosis, level):
    if level == 1:
        return diagnosis[:-1]+("X")
    elif level == 2:
        return diagnosis[:-4]+("XX.X")
    else:
        raise Exception("For the MF generalize_diagnosis level should be either 1 or 2, and was "+str(level))
    
def add_relative_noise(value, relNoise, cast_to_int=True):
    assert(relNoise > 0.0 and relNoise < 1.0)
    if cast_to_int:
        return int(np.random.uniform(value-(value*relNoise),value+(value*relNoise)))
    else:
        return np.random.uniform(value-(value*relNoise),value+(value*relNoise))
    
def blur_phone(phone, nFields):
    phone_str = str(phone)
    if nFields >= len(phone_str):
        return "X"*len(phone_str)
    else:
        return phone_str[:-nFields]+("X"*nFields) 
    
def mf_adapter(value, masking_function, mf_parameters):
    if value == "NaN":
        return np.nan
    
    if masking_function.__name__ == 'bucketize_age':
        if len(mf_parameters) != 1:
            raise Exception("For "+masking_function.__name__+" only the bucketsize is required.")
        b_size = float(mf_parameters[0])
        return bucketize_age(value, b_size)
    elif masking_function.__name__ == 'blur_zip':
        if len(mf_parameters) != 1:
            raise Exception("For "+masking_function.__name__+" only the number of blurred fields is required.")
        nFields = int(mf_parameters[0])
        return blur_zip(value, nFields)
    elif masking_function.__name__ == 'generalize_diagnosis':
        if len(mf_parameters) != 1:
            raise Exception("For "+masking_function.__name__+" only the generalization level is required.")
        level = int(mf_parameters[0])
        return generalize_diagnosis(value, level)
    elif masking_function.__name__ == 'add_relative_noise':
        if len(mf_parameters) == 1:
            relNoise = float(mf_parameters[0])
            return add_relative_noise(value, relNoise)
        elif len(mf_parameters) == 2:
            relNoise = float(mf_parameters[0])
            if type(mf_parameters[1]) == bool:
                cast_to_int = mf_parameters[1]
            else:
                cast_to_int = (mf_parameters[1] == 'True')
            return add_relative_noise(value, relNoise, cast_to_int)
        else:
            raise Exception("For "+masking_function.__name__+" only the relative noise is required and optionally a boolean if the result should be cast to int.")
    elif masking_function.__name__ == 'blur_phone':
        if len(mf_parameters) != 1:
            raise Exception("For "+masking_function.__name__+" only the number of blurred fields is required.")
        nFields = int(mf_parameters[0])
        return blur_phone(value, nFields)
    else:
        raise Exception("Masking function "+masking_function.__name__+" does not exist.")
    

def mask(original, masking_function, masked_attr, *mf_parameters):
    masked = original.copy()
    masked[masked_attr] = masked[masked_attr].apply(lambda x: mf_adapter(x, masking_function, mf_parameters))
    return masked

# Inverse Masking Functions
def inverse_bucketize_age(bucketized_age: str) -> list:
    values = []
    low, high = [int(float(s)) for s in re.findall(r'\d+\.\d+|\d+', bucketized_age)]
    for j in range(low, high+1):
        values.append(str(j))
    return values

def inverse_blur_zip(blured_zip: str) -> list:
    assert(len(blured_zip) == 6)
    if blured_zip.isnumeric():
        return [blured_zip]
    
    cut = blured_zip.find("X")
    base_zip = blured_zip[:cut]
    assert(base_zip == "" or base_zip.isnumeric())  
    assert(blured_zip[cut:] == "X"*(6-cut))
    values = []
    for i in range(10**(6-cut)):
        tail = str(i)
        values.append(base_zip+((6-cut-len(tail))*"0")+tail)
    return values

def inverse_generalize_diagnosis(generalized_diagnosis: str) -> list:
    head = generalized_diagnosis[0]
    possible_heads = string.ascii_uppercase.replace("U", "").replace("W", "").replace("X", "")
    assert head in possible_heads and len(generalized_diagnosis) == 5, f"Input with wrong formating. {generalized_diagnosis}"

    values = []
    if generalized_diagnosis[1:] == "XX.X":
        for i in np.arange(0.0, 100.0, 0.1):
            tail = str(i)
            if i < 10.0:
                values.append(head+"0"+tail[:3])
            else:
                values.append(head+tail[:4])
        return values
    elif generalized_diagnosis[3:] == ".X" and generalized_diagnosis[1:3].isnumeric():
        for i in range(10):
            values.append(generalized_diagnosis[:4]+str(i))
        return values
    else:
        raise Exception("Input with wrong formating.")

def inverse_blur_phone(blured_phone: str) -> list:
    if blured_phone.isnumeric():
        return [blured_phone]
    phone_len = len(blured_phone)
    cut = blured_phone.find("X")
    base_phone = blured_phone[:cut]
    assert(base_phone == "" or base_phone.isnumeric())  
    assert(blured_phone[cut:] == "X"*(phone_len-cut))
    values = []
    for i in range(10**(phone_len-cut)):
        tail = str(i)
        values.append(base_phone+((phone_len-cut-len(tail))*"0")+tail)
    return values

def plot_probabilities(dist):
    dist_copy = dist.copy().sort_index()
    # labels = dist.index.to_series().apply(lambda x: '{0}-{1}'.format(*x))
    # labels = dist.index

    fig = plt.figure()
    ax = fig.add_axes([0,0,1,1])
    ax.bar(range(len(dist_copy.values)),dist_copy.values)
    plt.show()


diagnosis_alphabet = []
letters = string.ascii_uppercase.replace("U", "").replace("W", "").replace("X", "")
for letter in letters:
    for number in np.arange(0.0, 100.0, 0.1):
        number = np.trunc(number*10)/(10)
        if number < 10.0:
            diagnosis_alphabet.append(letter+"0"+str(number)) 
        else:
            diagnosis_alphabet.append(letter+str(number))