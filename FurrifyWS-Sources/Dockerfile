FROM alpine:3

RUN apk update && apk add --no-cache openjdk17-jdk && apk add --no-cache gcompat

COPY sources-monolith/target/furrifyws-storage-sources.jar furrifyws-storage-sources.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-sources.jar"]
