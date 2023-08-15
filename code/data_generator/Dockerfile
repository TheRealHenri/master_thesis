FROM continuumio/miniconda3

WORKDIR /project

RUN conda install \
    pandas \
    pandasql \
    numpy \
    matplotlib \
    faker \
    nbconvert \
    jupyterlab

COPY ./dataGenerator.ipynb /project
COPY ./datasets /project/datasets
COPY ./entrypoint.sh /project
COPY ./lib/ /project/lib

ENTRYPOINT ["/project/entrypoint.sh"]

CMD ["jupyter", "nbconvert", "--execute", "--to", "notebook", "--inplace", "/project/dataGenerator.ipynb"]
# CMD ["jupyter-lab", "--ip=0.0.0.0","--no-browser","--allow-root"]