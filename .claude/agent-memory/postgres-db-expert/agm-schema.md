# AGM Entity Schema Deep Dive

## Inheritance Chain (JOINED)
biologicalentity (id PK, curie, obsolete, internal, taxon_id, dataprovider_id, ...)
  -> genomicentity (id PK/FK)
    -> affectedgenomicmodel (id PK/FK, subtype_id)

## Collections on AffectedGenomicModel Entity
1. **agmDiseaseAnnotations** - @OneToMany(mappedBy="diseaseAnnotationSubject") -> AGMDiseaseAnnotation
   - Table: agmdiseaseannotation (32K rows)
   - Index: agmdiseaseannotation_diseaseannotationsubject_index
   - Inheritance: diseaseannotation -> agmdiseaseannotation (JOINED)

2. **agmPhenotypeAnnotations** - @OneToMany(mappedBy="phenotypeAnnotationSubject") -> AGMPhenotypeAnnotation
   - Table: agmphenotypeannotation (624K rows)
   - Index: agmphenotypeannotation_phenotypeannotationsubject_index
   - Inheritance: phenotypeannotation -> agmphenotypeannotation (JOINED)

3. **agmSecondaryIds** - @OneToMany(mappedBy="singleAgm") -> AgmSecondaryIdSlotAnnotation
   - Table: slotannotation (singleagm_id column, 972K AGM refs)
   - Index: slotannotation_singleagm_index

4. **agmFullName** - @OneToOne(mappedBy="singleAgm") -> AgmFullNameSlotAnnotation
   - Table: slotannotation (singleagm_id column)
   - Index: slotannotation_singleagm_index

5. **agmSynonyms** - @OneToMany(mappedBy="singleAgm") -> AgmSynonymSlotAnnotation
   - Table: slotannotation (singleagm_id column)
   - Index: slotannotation_singleagm_index

6. **agmSequenceTargetingReagentAssociations** - @OneToMany(mappedBy="agmAssociationSubject")
   - Table: agmsequencetargetingreagentassociation (24K rows)
   - Index: agmstrassociation_agmassociationsubject_index

7. **components** (AgmAlleleAssociation) - @OneToMany(mappedBy="agmAssociationSubject")
   - Table: agmalleleassociation (2.04M rows)
   - Index: agmalleleassociation_agmassociationsubject_index

8. **parentalPopulations** (AgmAgmAssociation) - @OneToMany(mappedBy="agmAssociationSubject")
   - Table: agmagmassociation (0 rows currently)

9. **constructGenomicEntityAssociations** - @OneToMany(mappedBy="constructGenomicEntityAssociationObject")
   - Table: constructgenomicentityassociation (245K rows)

## Deep Navigation Chains (triggered by ModelDocumentBuilder)
- AGM -> components (AgmAlleleAssociation) -> allele -> alleleGeneAssociations -> gene
- AGM -> agmSTRAssociations -> STR -> sqtrGeneAssociations -> gene
- AGM -> agmDiseaseAnnotations -> conditionRelations, inferredGene, assertedGenes
- AGM -> agmPhenotypeAnnotations -> conditionRelations, inferredGene, assertedGenes, phenotypeAnnotationObject

## Indexer Pattern (after changes)
1. getAllIds: native SQL, returns 557K IDs ordered
2. findByIds: JPQL batch of 1500 IDs, returns AffectedGenomicModel entities
3. Lazy collections loaded on-demand with batch-fetch-size=100
