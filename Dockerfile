FROM openjdk:17-jdk

WORKDIR /app

COPY target/charity-run-0.0.1-SNAPSHOT.jar /app/charityrun.jar

EXPOSE 443

CMD ["java","-jar","charityrun.jar"]