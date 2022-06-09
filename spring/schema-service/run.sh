docker build --build-arg JAR_FILE=build/libs/\*.jar -t schema-service .
docker run --name schema-service -p 8082:8082 schema-service