FROM python:3.10-slim-bullseye

RUN pip install kafka-python
RUN pip install river
RUN pip install pymongo

WORKDIR /opt
RUN mkdir logs

COPY service.py /opt/service.py

CMD ["python", "service.py"]