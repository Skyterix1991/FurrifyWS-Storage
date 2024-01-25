FROM alpine:3

RUN apk update && apk add --no-cache openjdk17-jdk && apk add --no-cache gcompat

COPY posts-monolith/target/furrifyws-storage-posts.jar furrifyws-storage-posts.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/furrifyws-storage-posts.jar"]
