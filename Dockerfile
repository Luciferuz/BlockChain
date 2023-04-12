FROM maven:3.6.1-jdk-11 as build
WORKDIR /app
COPY . .
RUN mvn verify package
FROM openjdk:11
COPY --from=build /app/target/Blockchain-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/Blockchain.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/Blockchain.jar"]