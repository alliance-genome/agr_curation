FROM openjdk:11

WORKDIR /agr_curation

ADD target/agr_curation_api-bootable.jar .

EXPOSE 8080

ENV DB_CONNECTION_URL jdbc:postgresql://localhost:5432/curation
ENV DB_USER postgres
ENV DB_PASS postgres

CMD ["/bin/bash", "-c", "java -DDB_CONNECTION_URL=$DB_CONNECTION_URL -DDB_USER=$DB_USER -DDB_PASS=$DB_PASS -jar agr_curation_api-bootable.jar -b=0.0.0.0"]
