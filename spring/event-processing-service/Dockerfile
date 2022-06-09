FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
WORKDIR /opt
RUN mkdir logs
ENV PORT 8083
EXPOSE 8083
COPY build/libs/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar