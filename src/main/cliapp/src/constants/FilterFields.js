export const FILTER_FIELD_SETS = Object.freeze({
  abbreviationFilter: ["abbreviation"],
  abstractFilter: ["abstract"],


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
  citationFilter: ["citation"],
  conditionAnatomyFilter: ["conditionAnatomy.curie", "conditionAnatomy.name"],
  conditionChemicalFilter: ["conditionChemical.curie", "conditionChemical.name"],
  conditionClassFilter: ["conditionClass.name", "conditionClass.curie"],
  conditionFreeTextFilter: ["conditionFreeText"],
  conditionGeneOntologyFilter: ["conditionGeneOntology.curie", "conditionGeneOntology.name"],
  conditionIdFilter: ["conditionId.curie", "conditionId.name"],
  conditionQuantityFilter: ["conditionQuantity"],
  conditionRelationFilter: ["conditionRelationType.name"],
  conditionRelationHandleFilter: ["conditionRelations.handle", "conditionRelations.conditions.conditionSummary"],
  conditionRelationsFilter: ["conditionRelations.conditions.conditionSummary"],
  conditionSummaryFilter: ["conditionSummary"],
  conditionTaxonFilter: ["conditionTaxon.curie", "conditionTaxon.name"],
  createdByFilter: ["createdBy.uniqueId"],
  cross_referenceFilter: ["cross_references.curie"],


  curieFieldSet: {
		filterName: "curieFilter",
		fields: new Set(["curie"])
  },



  dataProviderFilter: [ "dataProvider.sourceOrganization.abbreviation", "dataProvider.sourceOrganization.fullName", "dataProvider.sourceOrganization.shortName" ],
  dateCreatedFilter: ["dateCreated"],
  dateUpdatedFilter: ["dateUpdated"],
  defaultUrlTemplateFilter: ["defaultUrlTemplate"],
  definitionFilter: ["definition"],
  descriptionFilter: ["vocabularyDescription"],
  diseaseQualifiersFilter: ["diseaseQualifiers.name"],
  diseaseRelationFilter: ["diseaseRelation.name"],
  evidenceCodesFilter: ["evidenceCodes.curie", "evidenceCodes.name", "evidenceCodes.abbreviation"],
  experimentalConditionFilter: ["conditions.conditionSummary"],
  formulaFilter: ["formula"],
  geneNameFilter: ["geneFullName.displayText", "geneFullName.formatText"],
  geneSymbolFilter: ["geneSymbol.displayText", "geneSymbol.formatText"],
  geneticModifierFilter: ["diseaseGeneticModifier.symbol", "diseaseGeneticModifier.name", "diseaseGeneticModifier.curie"],
  geneticModifierRelationFilter: ["diseaseGeneticModifierRelation.name"],
  geneticSexFilter: ["geneticSex.name"],
  handleFilter: ["handle"],
  idExampleFilter: ["idExample"],
  idPatternFilter: ["idPattern"],
  inchiFilter: ["inchi"],
  inchiKeyFilter: ["inchiKey"],


  inCollectionFieldSet: {
    filterName: "inCollectionFilter",
    fields: new Set(["inCollection.name"])
  },


  inferredAlleleFilter: ["inferredAllele.alleleSymbol.displayText", "inferredAllele.alleleFullName.displayText", "inferredAllele.curie"],
  inferredGeneFilter: ["inferredGene.geneSymbol.displayText", "inferredGene.curie"],

  internalFilter: ["internal"],
  isExtinctFilter: ["isExtinct"],
  iupacFilter: ["iupac"],
  memberTermsFilter: ["memberTerms.name"],
  modentityidFilter: ["modEntityId"],


  nameFieldSet: {
    filterName: "nameFilter",
    fields: new Set(["name"])
  },
  
  negatedFilter: ["negated"],
  objectFilter: ["object.curie", "object.name"],
  obsoleteFilter: ["obsolete"],
  pageDescriptionFilter: ["pageDescription"],
  parental_populationFilter: ["parental_population"],
  prefixFilter: ["prefix"], 

  referencesFieldSet: {
    filterName: "referencesFilter",
    fields: new Set(["references.curie", "references.crossReferences.referencedCurie"])
  },


  relatedNotesFilter: ["relatedNotes.freeText"],
  resourceDescriptorFilter: ["resourceDescriptor.prefix", "resourceDescriptor.name"],
  secondaryDataProviderFilter: [ "secondaryDataProvider.sourceOrganization.abbreviation", "secondaryDataProvider.sourceOrganization.fullName", "secondaryDataProvider.sourceOrganization.shortName" ],

  secondaryIdsFieldSet: {
    filterName: "secondaryIdsFilter",
    fields: new Set(["alleleSecondaryIds.secondaryId", "alleleSecondaryIds.evidence.curie"])
  },

  sgdStrainBackgroundFilter: ["sgdStrainBackground.name", "sgdStrainBackground.curie"],
  singleReferenceFilter: ["singleReference.curie", "singleReference.crossReferences.referencedCurie"],
  smilesFilter: ["smiles"],
  subjectFilter: ["subject.symbol", "subject.name", "subject.curie"],

  subtypeFieldSet: {
    filterName: "subtypeFilter",
    fields: new Set(["subtype.name"])
  },
  
  synonymsFilter: ["synonyms"],

  taxonFieldSet: {
    filterName: "taxonFilter",
    fields: new Set(["taxon.curie", "taxon.name"])
  },

  titleFilter: ["title"],
  uniqueidFilter: ["uniqueId"],
  updatedByFilter: ["updatedBy.uniqueId"],
  urlTemplateFilter: ["urlTemplate"],
  vocabularyFieldSet: {
    filterName: "vocabularyFilter",
    fields: new Set(["vocabularyTermSetVocabulary.name"]),
  },
  vocabularyNameFilter: ["vocabulary.name"],
  vocabularyTermSetDescriptionFilter: ["vocabularyTermSetDescription"],
  withFilter: ["with.geneSymbol.displayText", "with.geneFullName.displayText", "with.curie"],
});


