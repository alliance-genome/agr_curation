# AGR Curation Java Expert - Memory Index

## Architecture
- [Entity Hierarchy](entity-hierarchy.md) - JPA inheritance, key entities, mapping strategies
- [AGM Performance Analysis](agm-performance-analysis.md) - Feb 2026 analysis of batch fetch optimization

## Key Patterns
- **Inheritance**: BiologicalEntity uses JOINED inheritance (3 tables: biologicalentity, genomicentity, {gene|allele|agm|str|...})
- **Annotations**: DiseaseAnnotation and PhenotypeAnnotation also use JOINED inheritance
- **MappedSuperclass chain**: AuditedObject -> CurieObject -> SubmittedObject -> BiologicalEntity (entity) -> GenomicEntity (entity) -> AffectedGenomicModel (entity)
- **Annotation chain**: AuditedObject -> Association -> SingleReferenceAssociation -> Annotation (all MappedSuperclass) -> DiseaseAnnotation (entity, JOINED) -> AGMDiseaseAnnotation (entity)
- **Batch fetch**: `quarkus.hibernate-orm.fetch.batch-size=100` in application.properties (line 57)
- **Document endpoints**: Pattern is GET /{entity}/ids + POST /{entity}/byids with @JsonView

## CurationView
- `ModelDocument` is a standalone view class (not extending FieldsOnly)
- Controls serialization; does NOT prevent lazy loads triggered by Java code
- Key fields annotated with ModelDocument: curie, primaryExternalId, subtype, agmFullName, dataProvider, dataProviderCrossReference (on SubmittedObject), Gene.geneSymbol, Organization.abbreviation, DOTerm, ConditionRelation.conditionRelationType, ConditionRelation.conditions, ExperimentalCondition.conditionSummary

## Files
- application.properties: `/src/main/resources/application.properties`
- ModelDocumentController: `/src/main/java/.../controllers/document/ModelDocumentController.java`
- AffectedGenomicModelDAO: `/src/main/java/.../dao/AffectedGenomicModelDAO.java`
- ModelDocumentBuilder: `/src/main/java/.../model/document/builders/ModelDocumentBuilder.java`
- AffectedGenomicModel entity: `/src/main/java/.../model/entities/AffectedGenomicModel.java`
- ModelDocumentInterface: `/src/main/java/.../interfaces/document/ModelDocumentInterface.java`
