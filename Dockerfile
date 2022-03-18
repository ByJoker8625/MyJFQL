FROM openjdk:8-alpine

ENV MYJFQL_USER_NAME="docker"
ENV MYJFQL_USER_PASSWORD="myjfql_docker_password"
ENV MYJFQL_DATABASE="docker"

EXPOSE 2291

CMD mkdir /var/lib/myjfql/
CMD mkdir /opt/myjfql/

WORKDIR /var/lib/myjfql
COPY target/MyJFQL-1.5.5-jar-with-dependencies.jar /opt/myjfql/MyJFQL.jar
COPY myjfql-docker-config.yml /var/lib/myjfql/config.yml
CMD ["java", "-jar", "/opt/myjfql/MyJFQL.jar"]
