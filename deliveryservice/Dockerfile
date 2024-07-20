FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/deliveryservice-0.0.1-SNAPSHOT.jar deliveryservice-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "deliveryservice-0.0.1-SNAPSHOT.jar"]