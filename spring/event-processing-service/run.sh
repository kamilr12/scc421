docker build --build-arg JAR_FILE=build/libs/\*.jar -t event-processing-service .
docker run --name event-processing-service -p 8083:8083 event-processing-service