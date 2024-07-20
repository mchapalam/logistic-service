FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/inventoryservice-0.0.1-SNAPSHOT.jar inventoryservice-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "inventoryservice-0.0.1-SNAPSHOT.jar"]