export const FIELD_SETS = Object.freeze({
  abbreviationFilter: ["abbreviation"],

  abbreviationFieldSet: {
    filterName: "abbreviationFilter",
    fields: new Set(["abbreviation"])
  },
  
  abstractFieldSet: {
    filterName: "abstractFilter",
    fields: new Set(["abstract"]),
  },

  alleleNameFieldSet: {
    filterName: "alleleNameFilter",
    fields: new Set(["alleleFullName.displayText", "alleleFullName.formatText"])
  },
  alleleSymbolFieldSet: {
    filterName: "alleleSymbolFilter",
    fields: new Set(["alleleSymbol.displayText", "alleleSymbol.formatText"])
  },
  alleleInheritanceModesFieldSet: {
    filterName: "alleleInheritanceModesFilter",
    fields: new Set(["alleleInheritanceModes.inheritanceMode.name", "alleleInheritanceModes.phenotypeTerm.curie", "alleleInheritanceModes.phenotypeTerm.name", "alleleInheritanceModes.phenotypeStatement", "alleleInheritanceModes.evidence.curie"])
  },
  alleleMutationFieldSet: {
    filterName: "alleleMutationFilter",
    fields: new Set(["alleleMutationTypes.mutationTypes.curie", "alleleMutationTypes.mutationTypes.name", "alleleMutationTypes.evidence.curie"])
  },
  
  annotationTypeFilter: ["annotationType.name"],
  assertedAlleleFilter: ["assertedAllele.alleleSymbol.displayText", "assertedAllele.alleleFullName.displayText", "assertedAllele.curie"],
  assertedGenesFilter: ["assertedGenes.geneSymbol.displayText", "assertedGenes.curie"],
  
  citationFieldSet: {
    filterName: "citationFilter",
    fields: ["citation"],
  },

  conditionAnatomyFilter: ["conditionAnatomy.curie", "conditionAnatomy.name"],
  conditionChemicalFilter: ["conditionChemical.curie", "conditionChemical.name"],
  conditionClassFilter: ["conditionClass.name", "conditionClass.curie"],
  conditionFreeTextFilter: ["conditionFreeText"],
  conditionGeneOntologyFilter: ["conditionGeneOntology.curie", "conditionGeneOntology.name"],
  conditionIdFilter: ["conditionId.curie", "conditionId.name"],
  conditionQuantityFilter: ["conditionQuantity"],
  conditionRelationFilter: ["conditionRelationType.name"],
  conditionRelationHandleFilter: ["conditionRelations.handle", "conditionRelations.conditions.conditionSummary"],

  conditionRelationsFieldSet: {
    filterName: "conditionRelationsFilter",
    fields: new Set(["conditionRelations.conditions.conditionSummary"])
  },

  conditionSummaryFilter: ["conditionSummary"],
  conditionTaxonFilter: ["conditionTaxon.curie", "conditionTaxon.name"],

  createdByFieldSet: {
    filterName: "createdByFilter",
    fields: new Set(["createdBy.uniqueId"])
  },
  cross_referenceFieldSet: {
    filterName: "cross_referenceFilter",
    fields: new Set(["cross_references.curie"]),
  },
  curieFieldSet: {
		filterName: "curieFilter",
		fields: new Set(["curie"])
  },

  dataProviderFilter: [ "dataProvider.sourceOrganization.abbreviation", "dataProvider.sourceOrganization.fullName", "dataProvider.sourceOrganization.shortName" ],

  dataCreatedFieldSet: {
    filterName: "dateCreatedFilter",
    fields: new Set(["dateCreated"])
  },
  dataUpdatedFieldSet: {
    filterName: "dateUpdatedFilter",
    fields: new Set(["dateUpdated"])
  },
  defaultUrlTemplateFieldSet: {
    filterName: "defaultUrlTemplateFilter",
    fields: new Set(["defaultUrlTemplate"]),
  },
  definitionFieldSet: {
    filterName: "definitionFilter",
    fields: new Set(["definition"])
  },
  descriptionFieldSet: {
    filterName: "descriptionFilter",
    fields: new Set(["vocabularyDescription"]),
  },

  diseaseQualifiersFilter: ["diseaseQualifiers.name"],

  diseaseRelationFieldSet: {
    filterName: "diseaseRelationFilter",
    fields: new Set(["diseaseRelation.name"])
  },

  evidenceCodesFilter: ["evidenceCodes.curie", "evidenceCodes.name", "evidenceCodes.abbreviation"],

  experimentalConditionFieldSet: {
    filterName: "experimentalConditionFilter",
    fields: new Set(["conditions.conditionSummary"])
  },
  
  formulaFieldSet: {
    filterName: "formulaFilter",
    fields: ["formula"],
  }, 
  geneNameFilter: ["geneFullName.displayText", "geneFullName.formatText"],
  geneSymbolFilter: ["geneSymbol.displayText", "geneSymbol.formatText"],
  geneticModifierFilter: ["diseaseGeneticModifier.symbol", "diseaseGeneticModifier.name", "diseaseGeneticModifier.curie"],
  geneticModifierRelationFilter: ["diseaseGeneticModifierRelation.name"],
  geneticSexFilter: ["geneticSex.name"],

  handleFieldSet: {
    filterName: "handleFilter",
    fields: new Set(["handle"])
  },
  idExampleFieldSet: {
    filterName: "idExampleFilter",
    fields: new Set(["idExample"]),
  },
  idPatternFieldSet: {
    filterName: "idPatternFilter",
    fields: new Set(["idPattern"]),
  },
  
  inchiFieldSet: {
    filterName: "inchiFilter",
    fields: new Set(["inchi"]),
  },
  
  inchiKeyFieldSet: {
    filterName: "inchiKeyFilter",
    fields: new Set(["inchiKey"]),
  },

  inCollectionFieldSet: {
    filterName: "inCollectionFilter",
    fields: new Set(["inCollection.name"])
  },

  inferredAlleleFilter: ["inferredAllele.alleleSymbol.displayText", "inferredAllele.alleleFullName.displayText", "inferredAllele.curie"],
  inferredGeneFilter: ["inferredGene.geneSymbol.displayText", "inferredGene.curie"],

  internalFieldSet: {
    filterName: "internalFilter",
    fields: new Set(["internal"])
  },
  isExtinctFieldSet: {
    filterName: "isExtinctFilter",
    fields: new Set(["isExtinct"])
  },
  
  iupacFieldSet: {
    filterName: "iupacFilter",
    fields: ["iupac"],
  }, 

  memberTermsFieldSet: {
    filterName: "memberTermsFilter",
    fields: Set(["memberTerms.name"]),
  },
  modentityidFieldSet: {
    filterName: "modentityidFilter",
    fields: new Set(["modEntityId"])
  },
  nameFieldSet: {
    filterName: "nameFilter",
    fields: new Set(["name"])
  },
  negatedFieldSet: {
    filterName: "negatedFilter",
    fields: new Set(["negated"])
  },
  objectFieldSet: {
    filterName: "objectFilter",
    fields: new Set(["object.curie", "object.name"])
  },
  obsoleteFieldSet: {
    filterName: "obsoleteFilter",
    fields: new Set(["obsolete"])
  },
  pageDescriptionFieldSet: {
    filterName: "pageDescriptionFilter",
    fields: new Set(["pageDescription"]),
  },
  
  parental_populationFilter: ["parental_population"],
  
  prefixFieldSet: {
    filterName: "prefixFilter",
    fields: new Set(["prefix"]), 
  },
  referencesFieldSet: {
    filterName: "referencesFilter",
    fields: new Set(["references.curie", "references.crossReferences.referencedCurie"])
  },

  relatedNotesFilter: ["relatedNotes.freeText"],
  
  resourceDescriptorFieldSet: {
    filterName: "resourceDescriptorFilter",
    fields: new Set(["resourceDescriptor.prefix", "resourceDescriptor.name"]),
  },

  secondaryDataProviderFilter: [ "secondaryDataProvider.sourceOrganization.abbreviation", "secondaryDataProvider.sourceOrganization.fullName", "secondaryDataProvider.sourceOrganization.shortName" ],

  secondaryIdsFieldSet: {
    filterName: "secondaryIdsFilter",
    fields: new Set(["alleleSecondaryIds.secondaryId", "alleleSecondaryIds.evidence.curie"])
  },

  sgdStrainBackgroundFilter: ["sgdStrainBackground.name", "sgdStrainBackground.curie"],

  singleReferenceFieldSet: {
    filterName: "singleReferenceFilter",
    fields: new Set(["singleReference.curie", "singleReference.crossReferences.referencedCurie"])
  },
  
  smilesFieldSet: {
    filterName: "smilesFilter",
    fields: ["smiles"],
  }, 

  subjectFieldSet: {
    filterName: "subjectFilter",
    fields: new Set(["subject.symbol", "subject.name", "subject.curie"])
  },
  subtypeFieldSet: {
    filterName: "subtypeFilter",
    fields: new Set(["subtype.name"])
  },
  synonymsFieldSet: {
    filterName: "synonymsFilter",
    fields: new Set(["synonyms"]),
  },
  taxonFieldSet: {
    filterName: "taxonFilter",
    fields: new Set(["taxon.curie", "taxon.name"])
  },
  titleFieldSet: {
    filterName: "titleFilter",
    fields: new Set(["title"]),
  },
  uniqueidFieldSet: {
    filterName: "uniqueidFilter",
    fields: new Set(["uniqueId"])
  },
  updatedByFieldSet: {
    filterName: "updatedByFilter",
    fields: new Set(["updatedBy.uniqueId"])
  },
  urlTemplateFieldSet: {
    filterName: "urlTemplateFilter",
    fields: new Set(["urlTemplate"]),
  },
  vocabularyFieldSet: {
    filterName: "vocabularyFilter",
    fields: new Set(["vocabularyTermSetVocabulary.name"]),
  },
  vocabularyNameFieldSet: {
    filterName: "vocabularyNameFilter",
    fields: new Set(["vocabulary.name"])
  },
  vocabularyTermSetDescriptionFieldSet: {
    filterName: "vocabularyTermSetDescriptionFilter",
    fields: new Set(["vocabularyTermSetDescription"]),
  },
  withFieldSet: {
    filterName: "withFilter",
    fields: new Set(["with.geneSymbol.displayText", "with.geneFullName.displayText", "with.curie"])
  }
});


