FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/payment-service-0.0.1-SNAPSHOT.jar payment-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "payment-service-0.0.1-SNAPSHOT.jar"]