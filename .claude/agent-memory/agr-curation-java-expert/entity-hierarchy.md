# Entity Hierarchy Notes

## BiologicalEntity JOINED Inheritance (3 tables)
```
biologicalentity (InheritanceType.JOINED)
  +-- genomicentity
      +-- affectedgenomicmodel
      +-- allele
      +-- gene
      +-- sequencetargetingreagent
      +-- variant
      +-- assemblycomponent / codingsequence / exon / transcript
```

## DiseaseAnnotation JOINED Inheritance
```
diseaseannotation (InheritanceType.JOINED, extends Annotation @MappedSuperclass)
  +-- agmdiseaseannotation
  +-- allelediseaseannotation
  +-- genediseaseannotation
```

## PhenotypeAnnotation JOINED Inheritance
```
phenotypeannotation (InheritanceType.JOINED, extends Annotation @MappedSuperclass)
  +-- agmphenotypeannotation
  +-- allelephenotypeannotation
  +-- genephenotypeannotation
```

## Organization JOINED Inheritance
```
agent
  +-- organization
      +-- alliancemember
```

## MappedSuperclass Chain (no separate tables)
AuditedObject -> CurieObject -> SubmittedObject -> BiologicalEntity(entity)
AuditedObject -> Association -> SingleReferenceAssociation -> Annotation -> DiseaseAnnotation(entity)
AuditedObject -> Association -> EvidenceAssociation (for SequenceTargetingReagentGeneAssociation etc.)

## Key ManyToOne relationships on AGM path
- AGM.dataProvider -> Organization (SubmittedObject level, @Fetch SELECT)
- AGM.dataProviderCrossReference -> CrossReference (SubmittedObject level, @Fetch SELECT)
- AGM.taxon -> NCBITaxonTerm (BiologicalEntity level)
- AGM.subtype -> VocabularyTerm
- AGMDiseaseAnnotation.inferredGene -> Gene
- AGMDiseaseAnnotation.diseaseAnnotationObject -> DOTerm
- AGMPhenotypeAnnotation.inferredGene -> Gene
- ConditionRelation.conditionRelationType -> VocabularyTerm

## Key Collections on AGM path
- AGM.agmDiseaseAnnotations (OneToMany AGMDiseaseAnnotation)
- AGM.agmPhenotypeAnnotations (OneToMany AGMPhenotypeAnnotation)
- AGM.agmSequenceTargetingReagentAssociations (OneToMany)
- AGM.components (OneToMany AgmAlleleAssociation)
- Annotation.conditionRelations (ManyToMany ConditionRelation)
- ConditionRelation.conditions (ManyToMany ExperimentalCondition)
- AGMDiseaseAnnotation.assertedGenes (ManyToMany Gene)
- AGMPhenotypeAnnotation.assertedGenes (ManyToMany Gene)
