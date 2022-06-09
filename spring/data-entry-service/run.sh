docker build --build-arg JAR_FILE=build/libs/\*.jar -t data-entry-service .
docker run --name data-entry-service -p 8080:8080 data-entry-service