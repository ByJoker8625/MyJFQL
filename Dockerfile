FROM openjdk:8-alpine

ENV MYJFQL_USER_NAME="test"
ENV MYJFQL_USER_PASSWORD="password.min.8.chars"
ENV MYJFQL_DATABASE="tet"

EXPOSE 2291

WORKDIR /var/lib/myjfql
COPY target/MyJFQL-1.5.5-jar-with-dependencies.jar /opt/myjfql/MyJFQL.jar
COPY myjfql-docker-config.yml /var/lib/myjfql/config.yml
CMD ["java", "-jar", "/opt/myjfql/MyJFQL.jar"]
