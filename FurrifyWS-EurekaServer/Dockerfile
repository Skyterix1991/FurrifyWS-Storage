FROM alpine:3.19

RUN apk update && apk add --no-cache openjdk17-jdk && apk add --no-cache gcompat

COPY target/furrifyws-storage-eureka-server.jar furrifyws-storage-eureka-server.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-eureka-server.jar"]
