docker build --build-arg JAR_FILE=build/libs/\*.jar -t data-service .
docker run --name data-service -p 8081:8081 data-service