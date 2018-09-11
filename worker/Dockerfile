FROM openjdk:latest

RUN mkdir /app
COPY . /app

RUN ["echo","export CLASSPATH=$CLASSPATH:/app/commons-beanutils-1.9.3.jar:/app/commons-collections-3.2.2.jar:/app/commons-lang-2.6.jar:/app/commons-logging-1.2.jar:/app/ezmorph-1.0.6.jar:/app/jedis-2.9.0.jar:/app/json-lib-2.4-jdk15.jar:/app/postgresql-42.2.5.jre6.jar",">>","/etc/bash.bashrc"]

WORKDIR /app

RUN ["javac","-cp","$CLASSPATH:/app/commons-beanutils-1.9.3.jar:/app/commons-collections-3.2.2.jar:/app/commons-lang-2.6.jar:/app/commons-logging-1.2.jar:/app/ezmorph-1.0.6.jar:/app/jedis-2.9.0.jar:/app/json-lib-2.4-jdk15.jar:/app/postgresql-42.2.5.jre6.jar","src/server/Worker.java","-d","./classes"]

CMD ["java","-cp","$CLASSPATH:/app/commons-beanutils-1.9.3.jar:/app/commons-collections-3.2.2.jar:/app/commons-lang-2.6.jar:/app/commons-logging-1.2.jar:/app/ezmorph-1.0.6.jar:/app/jedis-2.9.0.jar:/app/json-lib-2.4-jdk15.jar:/app/postgresql-42.2.5.jre6.jar:./classes","server.Worker"]
