# AGR Curation - Java Expert Memory

## Project Overview
- Quarkus-based curation API with JPA/Hibernate and PostgreSQL
- Paired with `agr_java_software` indexer that calls curation API via REST
- Indexers fetch IDs then fetch document batches, building ES documents

## Key Patterns
- **ID-batch indexer pattern**: fetch all IDs via `/ids`, partition, fetch docs via `/byids` POST
- **CurationView JSON views**: control serialization; `ModelDocument` does NOT extend `FieldsOnly`
- **Global batch-fetch-size**: `quarkus.hibernate-orm.fetch.batch-size=100` in application.properties (line 57)
- **Entity hierarchy**: AffectedGenomicModel -> GenomicEntity -> BiologicalEntity -> SubmittedObject -> CurieObject

## AGM Indexer Analysis (Feb 2026)
- See [agm-indexer-analysis.md](agm-indexer-analysis.md) for detailed performance findings
- IndexerConfig: 4 threads, bufferSize=1500, runInParallel=true
- Key concern: Deep entity graph traversal in ModelDocumentBuilder despite batch-fetch-size=100
- Controller does NOT use `@JsonView` -- but the interface `ModelDocumentInterface.findByIds` does annotate with `@JsonView(CurationView.ModelDocument.class)` which means Jackson on the API side filters by that view
- However, the builder accesses the entity graph BEFORE serialization, so all lazy collections get loaded regardless of view

## File Locations
- IndexerConfig: `agr_java_software/.../indexer/config/IndexerConfig.java`
- AGM Indexer: `agr_java_software/.../indexers/curation/AffectedGenomicModelCurationIndexer.java`
- Model Controller: `agr_curation/.../controllers/document/ModelDocumentController.java`
- Model Builder: `agr_curation/.../model/document/builders/ModelDocumentBuilder.java`
- AGM Entity: `agr_curation/.../model/entities/AffectedGenomicModel.java`
- AGM DAO: `agr_curation/.../dao/AffectedGenomicModelDAO.java`
