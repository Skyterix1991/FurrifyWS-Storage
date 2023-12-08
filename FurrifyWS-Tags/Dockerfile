FROM alpine:3.19

RUN apk update && apk add --no-cache openjdk17-jdk && apk add --no-cache gcompat

COPY tags-monolith/target/furrifyws-storage-tags.jar furrifyws-storage-tags.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-tags.jar"]
