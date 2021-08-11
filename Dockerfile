FROM openjdk:11

WORKDIR /agr_curation

ADD target/agr_curation_api-bootable.jar .

EXPOSE 8080

CMD ["java", "-jar", "agr_curation_api-bootable.jar"]
