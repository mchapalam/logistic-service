FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/order-service-0.0.1-SNAPSHOT.jar order-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "order-service-0.0.1-SNAPSHOT.jar"]