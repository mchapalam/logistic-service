FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /opt

COPY build/libs/eureka-server-0.0.1-SNAPSHOT.jar eureka-server-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "eureka-server-0.0.1-SNAPSHOT.jar"]