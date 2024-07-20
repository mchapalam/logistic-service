FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/gatewayapi-0.0.1-SNAPSHOT.jar gatewayapi-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "gatewayapi-0.0.1-SNAPSHOT.jar"]