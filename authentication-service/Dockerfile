FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/authentication-service-0.0.1-SNAPSHOT.jar authentication-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "authentication-service-0.0.1-SNAPSHOT.jar"]