FROM python:2.7-alpine

RUN mkdir /app
WORKDIR /app

RUN pip install flask redis

COPY . /app

EXPOSE 80

CMD python app.py
