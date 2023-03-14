
export const FIELD_SETS = Object.freeze({
  abbreviationFieldSet: {
    filterName: "abbreviationFilter",
    fields: ["abbreviation"],
  },
  abstractFieldSet: {
    filterName: "abstractFilter",
    fields: ["abstract"],
  },
  alleleNameFieldSet: {
    filterName: "alleleNameFilter",
    fields: ["alleleFullName.displayText", "alleleFullName.formatText"],
  },
  alleleSymbolFieldSet: {
    filterName: "alleleSymbolFilter",
    fields: ["alleleSymbol.displayText", "alleleSymbol.formatText"],
  },
  alleleInheritanceModesFieldSet: {
    filterName: "alleleInheritanceModesFilter",
    fields: ["alleleInheritanceModes.inheritanceMode.name", "alleleInheritanceModes.phenotypeTerm.curie", "alleleInheritanceModes.phenotypeTerm.name", "alleleInheritanceModes.phenotypeStatement", "alleleInheritanceModes.evidence.curie"],
  },
  alleleMutationFieldSet: {
    filterName: "alleleMutationFilter",
    fields: ["alleleMutationTypes.mutationTypes.curie", "alleleMutationTypes.mutationTypes.name", "alleleMutationTypes.evidence.curie"],
  },
  annotationTypeFieldSet: {
    filterName: "annotationTypeFilter",
    fields: ["annotationType.name"],
  },
  assertedAlleleFieldSet: {
    filterName: "assertedAlleleFilter",
    fields: ["assertedAllele.alleleSymbol.displayText", "assertedAllele.alleleFullName.displayText", "assertedAllele.curie"],
  },
  assertedGenesFieldSet: {
    filterName: "assertedGenesFilter",
    fields: ["assertedGenes.geneSymbol.displayText", "assertedGenes.curie"],
  },
  citationFieldSet: {
    filterName: "citationFilter",
    fields: ["citation"],
  },
  conditionAnatomyFieldSet: {
    filterName: "conditionAnatomyFilter",
    fields: ["conditionAnatomy.curie", "conditionAnatomy.name"],
  }, 
  conditionChemicalFieldSet: {
    filterName: "conditionChemicalFilter",
    fields: ["conditionChemical.curie", "conditionChemical.name"],
  }, 
  conditionClassFieldSet: {
    filterName: "conditionClassFilter",
    fields: ["conditionClass.name", "conditionClass.curie"],
  },
  conditionFreeTextFieldSet: {
    filterName: "conditionFreeTextFilter",
    fields: ["conditionFreeText"],
  },
  conditionGeneOntologyFieldSet: {
    filterName: "conditionGeneOntologyFilter",
    fields: ["conditionGeneOntology.curie", "conditionGeneOntology.name"],
  }, 
  conditionIdFieldSet: {
    filterName: "conditionIdFieldSet",
    fields: ["conditionId.curie", "conditionId.name"],
  },
  conditionQuantityFieldSet: {
    filterName: "conditionQuantityFilter",
    fields: ["conditionQuantity"],
  },
  conditionRelationTypeFieldSet: {
    filterName: "conditionRelationFilter",
    fields: ["conditionRelationType.name"],
  },
  conditionRelationSummaryFieldSet: {
    filterName: "conditionSummaryFilter",
    fields: ["conditionSummary"],
  }, 
  conditionRelationsHandleFieldSet: {
    filterName: "conditionRelationHandleFilter",
    fields: ["conditionRelations.handle"],
  },
  daConditionRelationsHandleFieldSet: {
    filterName: "daConditionRelationHandleFilter",
    fields: ["conditionRelations.handle", "conditionRelations.conditions.conditionSummary"],
  },
  daConditionRelationsSummaryFieldSet: {
    filterName: "conditionRelationsFilter",
    fields: ["conditionRelations.conditions.conditionSummary"],
  },
  conditionTaxonFieldSet: {
    filterName: "conditionTaxonFilter",
    fields: ["conditionTaxon.curie", "conditionTaxon.name"],
  }, 
  createdByFieldSet: {
    filterName: "createdByFilter",
    fields: ["createdBy.uniqueId"],
  },
  literatureCrossReferenceFieldSet: {
    filterName: "literatureCrossReferenceFilter",
    fields: ["cross_references.curie", "cross_references.name"],
  },
  curieFieldSet: {
		filterName: "curieFilter",
		fields: ["curie"],
  },
  dataProviderFieldSet: {
    filterName: "dataProviderFilter",
    fields: [ "dataProvider.sourceOrganization.abbreviation", "dataProvider.sourceOrganization.fullName", "dataProvider.sourceOrganization.shortName" ],
  },
  dataCreatedFieldSet: {
    filterName: "dateCreatedFilter",
    fields: ["dateCreated"],
  },
  dataUpdatedFieldSet: {
    filterName: "dateUpdatedFilter",
    fields: ["dateUpdated"],
  },
  daAggregationFieldSet: {
    filterName: "daAggregationFilter",
    fields: ['diseaseRelation.name', 'geneticSex.name', 'annotationType.name', 'diseaseGeneticModifierRelation.name', 'diseaseQualifiers.name', 'dataProvider.sourceOrganization.abbreviation', 'secondaryDataProvider.sourceOrganization.abbreviation'],
  },
  defaultUrlTemplateFieldSet: {
    filterName: "defaultUrlTemplateFilter",
    fields: ["defaultUrlTemplate"],
  },
  definitionFieldSet: {
    filterName: "definitionFilter",
    fields: ["definition"],
  },
  vocabularyDescriptionFieldSet: {
    filterName: "vocabularyDescriptionFilter",
    fields: ["vocabularyDescription"],
  },
  diseaseQualifiersFieldSet: {
    filterName: "diseaseQualifiersFilter",
    fields: ["diseaseQualifiers.name"],
  },
  diseaseRelationFieldSet: {
    filterName: "diseaseRelationFilter",
    fields: ["diseaseRelation.name"],
  },
  evidenceCodesFieldSet: {
    filterName: "evidenceCodesFilter",
    fields: ["evidenceCodes.curie", "evidenceCodes.name", "evidenceCodes.abbreviation"],
  },
  experimentalConditionFieldSet: {
    filterName: "experimentalConditionFilter",
    fields: ["conditions.conditionSummary"],
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
  geneticSexFieldSet: {
    filterName: "geneticSexFilter",
    fields: ["geneticSex.name"],
  },
  handleFieldSet: {
    filterName: "handleFilter",
    fields: ["handle"],
  },
  idExampleFieldSet: {
    filterName: "idExampleFilter",
    fields: ["idExample"],
  },
  idPatternFieldSet: {
    filterName: "idPatternFilter",
    fields: ["idPattern"],
  },
  inchiFieldSet: {
    filterName: "inchiFilter",
    fields: ["inchi"],
  },
  inchiKeyFieldSet: {
    filterName: "inchiKeyFilter",
    fields: ["inchiKey"],
  },
  inCollectionFieldSet: {
    filterName: "inCollectionFilter",
    fields: ["inCollection.name"],
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
    fields: ["internal"],
  },
  isExtinctFieldSet: {
    filterName: "isExtinctFilter",
    fields: ["isExtinct"],
  },
  iupacFieldSet: {
    filterName: "iupacFilter",
    fields: ["iupac"],
  }, 
  memberTermsFieldSet: {
    filterName: "memberTermsFilter",
    fields: ["memberTerms.name"],
  },
  modentityidFieldSet: {
    filterName: "modentityidFilter",
    fields: ["modEntityId"],
  },
  nameFieldSet: {
    filterName: "nameFilter",
    fields: ["name"],
  },
  negatedFieldSet: {
    filterName: "negatedFilter",
    fields: ["negated"],
  },
  objectFieldSet: {
    filterName: "objectFilter",
    fields: ["object.curie", "object.name"],
  },
  obsoleteFieldSet: {
    filterName: "obsoleteFilter",
    fields: ["obsolete"],
  },
  pageDescriptionFieldSet: {
    filterName: "pageDescriptionFilter",
    fields: ["pageDescription"],
  },
  prefixFieldSet: {
    filterName: "prefixFilter",
    fields: ["prefix"], 
  },
  referencesFieldSet: {
    filterName: "referencesFilter",
    fields: ["references.curie", "references.crossReferences.referencedCurie"],
  },
  relatedNotesFieldSet: {
    filterName: "relatedNotesFilter",
    fields: ["relatedNotes.freeText"],
  },
  resourceDescriptorFieldSet: {
    filterName: "resourceDescriptorFilter",
    fields: ["resourceDescriptor.prefix", "resourceDescriptor.name"],
  },
  secondaryDataProviderFieldSet: {
    filterName: "secondaryDataProviderFilter",
    fields: [ "secondaryDataProvider.sourceOrganization.abbreviation", "secondaryDataProvider.sourceOrganization.fullName", "secondaryDataProvider.sourceOrganization.shortName" ],
  },
  secondaryIdsFieldSet: {
    filterName: "secondaryIdsFilter",
    fields: ["alleleSecondaryIds.secondaryId", "alleleSecondaryIds.evidence.curie"],
  },
  sgdStrainBackgroundFieldSet: {
    filterName: "sgdStrainBackgroundFilter",
    fields: ["sgdStrainBackground.name", "sgdStrainBackground.curie"],
  },
  singleReferenceFieldSet: {
    filterName: "singleReferenceFilter",
    fields: ["singleReference.curie", "singleReference.crossReferences.referencedCurie"],
  },
  smilesFieldSet: {
    filterName: "smilesFilter",
    fields: ["smiles"],
  }, 
  subjectFieldSet: {
    filterName: "subjectFilter",
    fields: ["subject.symbol", "subject.name", "subject.curie"],
  },
  subtypeFieldSet: {
    filterName: "subtypeFilter",
    fields: ["subtype.name"],
  },
  synonymsFieldSet: {
    filterName: "synonymsFilter",
    fields: ["synonyms"],
  },
  taxonFieldSet: {
    filterName: "taxonFilter",
    fields: ["taxon.curie", "taxon.name"],
  },
  titleFieldSet: {
    filterName: "titleFilter",
    fields: ["title"],
  },
  uniqueidFieldSet: {
    filterName: "uniqueidFilter",
    fields: ["uniqueId"],
  },
  updatedByFieldSet: {
    filterName: "updatedByFilter",
    fields: ["updatedBy.uniqueId"],
  },
  urlTemplateFieldSet: {
    filterName: "urlTemplateFilter",
    fields: ["urlTemplate"],
  },
  vocabularyFieldSet: {
    filterName: "vocabularyFilter",
    fields: ["vocabularyTermSetVocabulary.name"],
  },
  vocabularyNameFieldSet: {
    filterName: "vocabularyNameFilter",
    fields: ["vocabulary.name"],
  },
  vocabularyTermSetDescriptionFieldSet: {
    filterName: "vocabularyTermSetDescriptionFilter",
    fields: ["vocabularyTermSetDescription"],
  },
  withFieldSet: {
    filterName: "withFilter",
    fields: ["with.geneSymbol.displayText", "with.geneFullName.displayText", "with.curie"],
  }
});

export const FILTER_CONFIGS = Object.freeze({
  abbreviationFilterConfig:                 { filterComponentType: 'input', fieldSets: [FIELD_SETS.abbreviationFieldSet] },
  abstractFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.abstractFieldSet]},
  alleleInheritanceModesFilterConfig:       { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleInheritanceModesFieldSet] },
  alleleMutationFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleMutationFieldSet] },
  alleleNameFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleNameFieldSet] },
  alleleSymbolFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.alleleSymbolFieldSet] },
  assertedAlleleFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.assertedAlleleFieldSet] },
  assertedGenesFilterConfig:                { filterComponentType: "input", fieldSets: [FIELD_SETS.assertedGenesFieldSet] },
  citationFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.citationFieldSet]},
  conditionAnatomyFilterConfig:             { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionAnatomyFieldSet] },
  conditionChemicalFilterConfig:            { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionChemicalFieldSet] },
  conditionClassFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionClassFieldSet] },
  conditionFreeTextFilterConfig:            { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionFreeTextFieldSet] },
  conditionGeneOntologyFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionGeneOntologyFieldSet] },
  conditionIdFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionIdFieldSet] },
  conditionRelationHandleFilterConfig:      { filterComponentType: "input", fieldSets: [FIELD_SETS.handleFieldSet] },
  conditionRelationSummaryFilterConfig:     { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionRelationSummaryFieldSet] },
  conditionQuantityFilterConfig:            { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionQuantityFieldSet] },
  conditionTaxonFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.conditionTaxonFieldSet] },
  createdByFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.createdByFieldSet] },
  curieFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.curieFieldSet] },
  
  daConditionRelationsHandleFilterConfig:   { filterComponentType: "input", fieldSets: [FIELD_SETS.daConditionRelationsHandleFieldSet], nonNullFields: FIELD_SETS.conditionRelationsHandleFieldSet },
  daConditionRelationsSummaryFilterConfig:  { filterComponentType: "input", fieldSets: [FIELD_SETS.daConditionRelationsSummaryFieldSet], nullFields: FIELD_SETS.conditionRelationsHandleFieldSet },
  
  dataCreatedFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.dataCreatedFieldSet] },
  dateUpdatedFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.dataUpdatedFieldSet] },
  defaultUrlTemplateFilterConfig:           { filterComponentType: "input", fieldSets: [FIELD_SETS.defaultUrlTemplateFieldSet] },
  definitionFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.definitionFieldSet] },
  experimentalConditionFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.experimentalConditionFieldSet] },
  evidenceCodesFilterConfig:                { filterComponentType: "input", fieldSets: [FIELD_SETS.evidenceCodesFieldSet] },
  formulaFilterConfig:                      { filterComponentType: "input", fieldSets: [FIELD_SETS.formulaFieldSet] },
  geneNameFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.geneNameFieldSet] },
  geneSymbolFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.geneSymbolFieldSet] },
  geneticModifierFilterConfig:              { filterComponentType: "input", fieldSets: [FIELD_SETS.geneticModifierFieldSet] },
  idExampleFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.idExampleFieldSet] },
  idPatternFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.idPatternFieldSet] },
  inchiFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.inchiFieldSet] },
  inchiKeyFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.inchiKeyFieldSet] },
  inCollectionFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.inCollectionFieldSet], useKeywordFields: true },
  inferredAlleleFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.inferredAlleleFieldSet] },
  inferredGeneFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.inferredGeneFieldSet] },
  iupacFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.iupacFieldSet] },
  literatureCrossReferenceFilterConfig:     { filterComponentType: "input", fieldSets: [FIELD_SETS.literatureCrossReferenceFieldSet] },
  modentityidFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.modentityidFieldSet] },
  nameFilterConfig:                         { filterComponentType: "input", fieldSets: [FIELD_SETS.nameFieldSet] },
  objectFilterConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.objectFieldSet] },
  pageDescriptionFilterConfig:              { filterComponentType: "input", fieldSets: [FIELD_SETS.pageDescriptionFieldSet] },
  prefixFilterConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.prefixFieldSet]},
  referencesFilterConfig:                   { filterComponentType: "input", fieldSets: [FIELD_SETS.referencesFieldSet] },
  relatedNotesFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.relatedNotesFieldSet] },
  resourceDescriptorFilterConfig:           { filterComponentType: "input", fieldSets: [FIELD_SETS.resourceDescriptorFieldSet] },
  secondaryIdsFilterConfig:                 { filterComponentType: "input", fieldSets: [FIELD_SETS.secondaryIdsFieldSet] },
  sgdStrainBackgroundFilterConfig:          { filterComponentType: "input", fieldSets: [FIELD_SETS.sgdStrainBackgroundFieldSet], },
  singleReferenceFilterConfig:              { filterComponentType: "input", fieldSets: [FIELD_SETS.singleReferenceFieldSet] },
  smilesFilterConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.smilesFieldSet] },
  subjectFieldConfig:                       { filterComponentType: "input", fieldSets: [FIELD_SETS.subjectFieldSet] },
  subtypeFilterConfig:                      { filterComponentType: "input", fieldSets: [FIELD_SETS.subtypeFieldSet] }, 
  synonymsFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.synonymsFieldSet] },
  taxonFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.taxonFieldSet] },
  titleFilterConfig:                        { filterComponentType: "input", fieldSets: [FIELD_SETS.titleFieldSet] },
  uniqueidFilterConfig:                     { filterComponentType: "input", fieldSets: [FIELD_SETS.uniqueidFieldSet] },
  updatedByFilterConfig:                    { filterComponentType: "input", fieldSets: [FIELD_SETS.updatedByFieldSet] },
  urlTemplateFilterConfig:                  { filterComponentType: "input", fieldSets: [FIELD_SETS.urlTemplateFieldSet] },
  vocabularyDescriptionFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyDescriptionFieldSet] },
  vocabularyFieldSetFilterConfig:           { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyFieldSet] },
  vocabularyMemberTermsFilterConfig:        { filterComponentType: "input", fieldSets: [FIELD_SETS.memberTermsFieldSet] },
  vocabularyNameFilterConfig:               { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyNameFieldSet] },
  vocabularyTermSetDescriptionFilterConfig: { filterComponentType: "input", fieldSets: [FIELD_SETS.vocabularyTermSetDescriptionFieldSet] },
  withFilterConfig:                         { filterComponentType: "input", fieldSets: [FIELD_SETS.withFieldSet] },
  
  isExtinctFilterConfig:                    { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.isExtinctFieldSet] },
	obsoleteFilterConfig:                     { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.obsoleteFieldSet] },
  internalFilterConfig:                     { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.internalFieldSet] },
  negatedFilterConfig:                      { filterComponentType: "dropdown", fieldSets: [FIELD_SETS.negatedFieldSet] },

  annotationTypeFilterConfig:               { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.annotationTypeFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true },
  dataProviderFilterConfig:                 { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.dataProviderFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true  },
  diseaseQualifiersFilterConfig:            { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.diseaseQualifiersFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true },
  diseaseRelationFilterConfig:              { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.diseaseRelationFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true },
  geneticModifierRelationFilterConfig:      { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.geneticModifierRelationFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true },
  geneticSexFilterConfig:                   { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.geneticSexFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true },
  secondaryDataProviderFilterConfig:        { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.secondaryDataProviderFieldSet], aggregationFieldSet: FIELD_SETS.daAggregationFieldSet, useKeywordFields: true },
  
  conditionRelationTypeFilterConfig:        { filterComponentType: "multiselect", fieldSets: [FIELD_SETS.conditionRelationTypeFieldSet], aggregationFieldSet: FIELD_SETS.conditionRelationTypeFieldSet },

  // ALL Auto Complete Filters need to have useKeywordFields: true in order that exact matches come to the top of the list
  acLiteratureCrossReferenceFilterConfig: { filterComponentType: "input", fieldSets: [FIELD_SETS.curieFieldSet, FIELD_SETS.literatureCrossReferenceFieldSet], useKeywordFields: true },
	// nameAutoCompleteFilterConfig: { filterType: "autocomplete", fieldSets: [FIELD_SETS.curieFieldSet, FIELD_SETS.geneNameFieldSet, FIELD_SETS.alleleNameFieldSet] }
});
