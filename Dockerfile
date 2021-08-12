FROM openjdk:11

WORKDIR /agr_curation

ADD target/agr_curation_api-runner .

EXPOSE 8080

ENV QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_HOSTS localhost:9200
ENV QUARKUS_DATASOURCE_JDBC_URL jdbc:postgresql://localhost:5432/curation
ENV QUARKUS_DATASOURCE_USERNAME postgres
ENV QUARKUS_DATASOURCE_PASSWORD postgres

CMD ["./agr_curation_api-runner"]
