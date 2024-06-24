FROM alpine:3.20

RUN apk update && apk add --no-cache openjdk17-jdk && apk add --no-cache gcompat

COPY target/furrifyws-storage-gateway.jar furrifyws-storage-gateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-gateway.jar"]
