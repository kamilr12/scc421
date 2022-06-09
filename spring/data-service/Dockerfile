FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
WORKDIR /opt
RUN mkdir logs
ENV PORT 8081
EXPOSE 8081
COPY build/libs/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar