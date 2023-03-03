
export const FILTER_CONFIGS = Object.freeze({
  abbreviationFilterConfig:                 { filterComponentType: 'input', fieldSets: [FIELD_SETS.abbreviationFieldSet] },
  alleleInheritanceModesFilterConfig:       { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleInheritanceModesFieldSet] },
  alleleMutationFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleMutationFieldSet] },
  alleleNameFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleNameFieldSet] },
  alleleSymbolFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleSymbolFieldSet] },
  assertedAlleleFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.assertedAlleleFieldSet] },
  assertedGenesFilterConfig:                { filterComponentType: "input", fieldSets: [FIELD_SETS.assertedGeneFieldSet] },
  conditionAnatomyFilterConfig:             { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionAnatomyFieldSet] },
  conditionClassFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionClassFieldSet] },
  conditionChemicalFilterConfig:            { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionChemicalFieldSet] },
  conditionGeneOntologyFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionGeneOntologyFieldSet] },
  conditionIdFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionIdFieldSet] },
  conditionRelationHandleFilterConfig:      { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionRelationHandleFieldSet], nonNullFields: ["conditionRelations.handle"] },
  conditionSummaryFilterConfig:             { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionSummaryFieldSet] },
  createdByFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.createdByFieldSet] },
  curieFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.curieFieldSet] },

  dataCreatedFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.dataCreatedFieldSet] },
  dataProviderFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.dataProviderFieldSet] },
  dateUpdatedFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.dataUpdatedFieldSet] },
  definitionFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.definitionFieldSet] },
  experimentalConditionFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.experimentalConditionFieldSet] },
  geneticModifierFilterConfig:              { filterComponentType: "input", fieldSets: [FIELD_SETS.geneticModifierFieldSet] },
  
  inCollectionFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.inCollectionFieldSet], useKeywordFields: true },
  inferredAlleleFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.inferredAlleleFieldSet] },
  inferredGeneFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.inferredGeneFieldSet] },

  modentityidFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.modentityidFieldSet] },
  nameFilterConfig:                         { filterComponentType: "input", fieldSets: [FIELD_SETS.nameFieldSet] },
  referencesFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.referencesFieldSet] },
  secondaryDataProviderFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.secondaryDataProviderFieldSet] },
  secondaryIdsFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.secondaryIdsFieldSet] },
  subjectFieldConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.subjectFieldSet] },
  subtypeFilterConfig:                      { filterComponentType: "input", fieldSets: [FIELD_SETS.subtypeFieldSet] }, 
  taxonFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.taxonFieldSet] },
  singleReferenceFilterConfig:              { filterComponentType: "input", fieldSets: [FIELD_SETS.singleReferenceFieldSet] },
  sgdStrainBackgroundFilterConfig:          { filterComponentType: "input", fieldSets: [FIELD_SETS.sgdStrainBackgroundFieldSet], },
  experimentalConditionFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.experimentalConditionFieldSet] },

  uniqueidFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.uniqueidFieldSet] },
  updatedByFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.updatedByFieldSet] },
  vocabularyFieldSetFilterConfig:           { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyFieldSet] },
  vocabularyMemberTermsFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.memberTermsFieldSet] },
  vocabularyDescriptionFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyDescriptionFieldSet] },
  vocabularyNameFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyNameFieldSet] },
  vocabularyTermSetDescriptionFilterConfig: { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyTermSetDescriptionFieldSet] },
  
  prefixFilterConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.prefixFieldSet]},
  synonymsFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.synonymsFieldSet]},
  idPatternFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.idPatternFieldSet]},
  idExampleFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.idExampleFieldSet]},
  defaultUrlTemplateFilterConfig:           { filterComponentType: "input", fieldSets: [FIELD_SETS.defaultUrlTemplateFieldSet]},
  resourceDescriptorFilterConfig:           { filterComponentType: "input", fieldSets: [FIELD_SETS.resourceDescriptorFieldSet]},
  urlTemplateFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.urlTemplateFieldSet]},
  pageDescriptionFilterConfig:              { filterComponentType: "input", fieldSets: [FIELD_SETS.pageDescriptionFieldSet]},
  inchiFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.inchiFieldSet]},
  inchiKeyFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.inchiKeyFieldSet]},
  iupacFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.iupacFieldSet]},
  formulaFilterConfig:                      { filterComponentType: "input", fieldSets: [FIELD_SETS.formulaFieldSet]},
  smilesFilterConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.smilesFieldSet]},
  literatureCrossReferenceFilterConfig:     { filterComponentType: "input", fieldSets: [FIELD_SETS.literatureCrossReferenceFieldSet]},
  titleFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.titleFieldSet]},
  abstractFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.abstractFieldSet]},
  citationFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.citationFieldSet]},
  geneNameFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.geneNameFieldSet]},
  geneSymbolFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.geneSymbolFieldSet]},
  
  isExtinctFilterConfig:                    { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.isExtinctFieldSet] },
	obsoleteFilterConfig:                     { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.obsoleteFieldSet] },
  internalFilterConfig:                     { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.internalFieldSet] },
  negatedFilterConfig:                      { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.negatedFieldSet] },

  annotationTypeFilterConfig:               { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.annotationTypeFieldSet], useKeywordFields: true},
  geneticModifierRelationFilterConfig:      { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.geneticModifierRelationFieldSet], useKeywordFields: true},
  conditionRelationsFilterConfig:           { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.conditionRelationsFieldSet] },


	// nameAutoCompleteFilterConfig: { filterType: "autocomplete", fieldSets: [FIELD_SETS.curieFieldSet, FIELD_SETS.geneNameFieldSet, FIELD_SETS.alleleNameFieldSet] }

});

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
  annotationTypeFieldSet: {
    filterName: "annotationTypeFilter",
    fields: new Set(["annotationType.name"]),
  },
  assertedAlleleFieldSet: {
    filterName: "assertedAlleleFilter",
    fields: new Set(["assertedAllele.alleleSymbol.displayText", "assertedAllele.alleleFullName.displayText", "assertedAllele.curie"]),
  },
  assertedGenesFieldSet: {
    filterName: "assertedGenesFilter",
    fields: new Set(["assertedGenes.geneSymbol.displayText", "assertedGenes.curie"]),
  },
  citationFieldSet: {
    filterName: "citationFilter",
    fields: new Set(["citation"]),
  },
  conditionAnatomyFieldSet: {
    filterName: "conditionAnatomyFilter",
    fields: new Set(["conditionAnatomy.curie", "conditionAnatomy.name"]),
  }, 
  conditionChemicalFieldSet: {
    filterName: "conditionChemicalFilter",
    fields: new Set(["conditionChemical.curie", "conditionChemical.name"]),
  }, 
  conditionClassFieldSet: {
    filterName: "conditionClassFilter",
    fields: new Set(["conditionClass.name", "conditionClass.curie"]),
  },
  conditionFreeTextFieldSet: {
    filterName: "conditionFreeTextFilter",
    fields: new Set(["conditionFreeText"])
  },
  conditionGeneOntologyFieldSet: {
    filterName: "conditionGeneOntologyFilter",
    fields: new Set(["conditionGeneOntology.curie", "conditionGeneOntology.name"]),
  }, 
  conditionIdFieldSet: {
    filterName: "conditionIdFieldSet",
    fields: new Set(["conditionId.curie", "conditionId.name"]),
  },
  conditionQuantityFieldSet: {
    filterName: "conditionQuantityFilter",
    fields: new Set(["conditionQuantity"]),
  },

  conditionRelationFilter: ["conditionRelationType.name"],

  conditionRelationHandleFieldSet: {
    filterName: "conditionRelationHandleFilter",
    fields: ["conditionRelations.handle", "conditionRelations.conditions.conditionSummary"],
  },
  conditionRelationsFieldSet: {
    filterName: "conditionRelationsFilter",
    fields: new Set(["conditionRelations.conditions.conditionSummary"])
  },
  conditionSummaryFieldSet: {
    filterName: "conditionSummaryFilter",
    fields: new Set(["conditionSummary"]),
  }, 
  conditionTaxonFieldSet: {
    filterName: "conditionTaxonFilter",
    fields: new Set(["conditionTaxon.curie", "conditionTaxon.name"]),
  }, 
  createdByFieldSet: {
    filterName: "createdByFilter",
    fields: new Set(["createdBy.uniqueId"])
  },
  literatureCrossReferenceFieldSet: {
    filterName: " literatureCrossReferenceFilter",
    fields: new Set(["cross_references.curie", "cross_references.name"]),
  },
  curieFieldSet: {
		filterName: "curieFilter",
		fields: new Set(["curie"])
  },
  dataProviderFieldSet: {
    filterName: "dataProviderFilter",
    fields: [ "dataProvider.sourceOrganization.abbreviation", "dataProvider.sourceOrganization.fullName", "dataProvider.sourceOrganization.shortName" ],
  },
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
  vocabularyDescriptionFieldSet: {
    filterName: "vocabularyDescriptionFilter",
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
  geneNameFieldSet: {
    filterName: "geneNameFilter",
    fields: ["geneFullName.displayText", "geneFullName.formatText"],
  },
  geneSymbolFieldSet: {
    filterName: "geneSymbolFilter",
    fields: ["geneSymbol.displayText", "geneSymbol.formatText"],
  },
  geneticModifierFieldSet: {
    filterName: "geneticModifierFilter",
    fields: ["diseaseGeneticModifier.symbol", "diseaseGeneticModifier.name", "diseaseGeneticModifier.curie"],
  },

  geneticModifierRelationFieldSet: {
    filterName: "geneticModifierRelationFilter",
    fields: ["diseaseGeneticModifierRelation.name"],
  },
  
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
  inferredAlleleFieldSet: {
    filterName: "inferredAlleleFilter",
    fields: ["inferredAllele.alleleSymbol.displayText", "inferredAllele.alleleFullName.displayText", "inferredAllele.curie"],
  },
  inferredGeneFieldSet: {
    filterName: "inferredGeneFilter",
    fields: ["inferredGene.geneSymbol.displayText", "inferredGene.curie"]
  },
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
    fields: new Set(["iupac"]),
  }, 
  memberTermsFieldSet: {
    filterName: "memberTermsFilter",
    fields: new Set(["memberTerms.name"]),
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
  secondaryDataProviderFieldSet: {
    filterName: "secondaryDataProviderFilter",
    fields: new Set([ "secondaryDataProvider.sourceOrganization.abbreviation", "secondaryDataProvider.sourceOrganization.fullName", "secondaryDataProvider.sourceOrganization.shortName" ]),
  },
  secondaryIdsFieldSet: {
    filterName: "secondaryIdsFilter",
    fields: new Set(["alleleSecondaryIds.secondaryId", "alleleSecondaryIds.evidence.curie"])
  },
  sgdStrainBackgroundFieldSet: {
    filterName: "sgdStrainBackgroundFilter",
    fields: ["sgdStrainBackground.name", "sgdStrainBackground.curie"],
  },
  singleReferenceFieldSet: {
    filterName: "singleReferenceFilter",
    fields: new Set(["singleReference.curie", "singleReference.crossReferences.referencedCurie"])
  },
  smilesFieldSet: {
    filterName: "smilesFilter",
    fields: new Set(["smiles"]),
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


