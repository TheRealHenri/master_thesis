package com.pipeline.kafka.dataMasking;

import com.pipeline.kafka.dataMasking.maskingFunctions.*;

import java.util.ArrayList;
import java.util.List;

public class MaskingFunctionsCatalog {

    public List<MaskingFunction> functions;

    public MaskingFunctionsCatalog() {
        this.functions = new ArrayList<>();
        this.functions.add(new AddRelativeNoise());
        this.functions.add(new BlurPhone());
        this.functions.add(new BlurZip());
        this.functions.add(new BucketizeAge());
        this.functions.add(new GeneralizeDiagnosis());
    }
//
//    public void addToSchema(SchemaPlus schema){
//        for (MaskingFunction function : functions) {
//            schema.add(function.name, ScalarFunctionImpl.create(function.getClass(), "eval"));
//        }
//    }

    public MaskingFunction getByName(String name){
        for (MaskingFunction function : functions) {
            if (function.name.equals(name)){
                return function;
            }
        }
        return null;
    }
}
