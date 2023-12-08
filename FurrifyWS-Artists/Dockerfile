FROM alpine:3.19

RUN apk update && apk add --no-cache openjdk17-jdk && apk add --no-cache gcompat

COPY artists-monolith/target/furrifyws-storage-artists.jar furrifyws-storage-artists.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-artists.jar"]
