FROM continuumio/miniconda3

WORKDIR /project

RUN conda install \
    pandas \
    pandasql \
    numpy \
    matplotlib \
    faker \
    jupyterlab

COPY ./dataGenerator.ipynb /project
COPY ./datasets /project/datasets
COPY ./lib /project/lib
COPY ./syntheticData.csv /project/syntheticData.csv

CMD ["jupyter-lab", "--ip=0.0.0.0","--no-browser","--allow-root"]