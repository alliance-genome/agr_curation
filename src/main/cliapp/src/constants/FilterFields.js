export const FILTER_FIELDS = Object.freeze({
  abbreviationFilter: ["abbreviation"],
  abstractFilter: ["abstract"],
  alleleNameFilter: ["alleleFullName.displayText", "alleleFullName.formatText"],
  alleleSymbolFilter: ["alleleSymbol.displayText", "alleleSymbol.formatText"],
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
  curieFilter: ["curie"],
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
  inCollectionFilter: ["inCollection.name"],
  inferredAlleleFilter: ["inferredAllele.alleleSymbol.displayText", "inferredAllele.alleleFullName.displayText", "inferredAllele.curie"],
  inferredGeneFilter: ["inferredGene.geneSymbol.displayText", "inferredGene.curie"],
  inheritanceModesFilter: ["alleleInheritanceModes.inheritanceMode.name", "alleleInheritanceModes.phenotypeTerm.curie", "alleleInheritanceModes.phenotypeTerm.name", "alleleInheritanceModes.phenotypeStatement", "alleleInheritanceModes.evidence.curie"],
  internalFilter: ["internal"],
  isExtinctFilter: ["isExtinct"],
  iupacFilter: ["iupac"],
  memberTermsFilter: ["memberTerms.name"],
  modentityidFilter: ["modEntityId"],
  mutationTypesFilter: ["alleleMutationTypes.mutationTypes.curie", "alleleMutationTypes.mutationTypes.name", "alleleMutationTypes.evidence.curie"],
  nameFilter: ["name"],
  negatedFilter: ["negated"],
  objectFilter: ["object.curie", "object.name"],
  obsoleteFilter: ["obsolete"],
  pageDescriptionFilter: ["pageDescription"],
  parental_populationFilter: ["parental_population"],
  prefixFilter: ["prefix"], 
  referencesFilter: ["references.curie", "references.crossReferences.referencedCurie"],
  relatedNotesFilter: ["relatedNotes.freeText"],
  resourceDescriptorFilter: ["resourceDescriptor.prefix", "resourceDescriptor.name"],
  secondaryDataProviderFilter: [ "secondaryDataProvider.sourceOrganization.abbreviation", "secondaryDataProvider.sourceOrganization.fullName", "secondaryDataProvider.sourceOrganization.shortName" ],
  secondaryIdsFilter: ["alleleSecondaryIds.secondaryId", "alleleSecondaryIds.evidence.curie"],
  sgdStrainBackgroundFilter: ["sgdStrainBackground.name", "sgdStrainBackground.curie"],
  singleReferenceFilter: ["singleReference.curie", "singleReference.crossReferences.referencedCurie"],
  smilesFilter: ["smiles"],
  subjectFilter: ["subject.symbol", "subject.name", "subject.curie"],
  subtypeFilter: ["subtype.name"],
  synonymsFilter: ["synonyms"],
  taxonFilter: ["taxon.curie", "taxon.name"],
  titleFilter: ["title"],
  uniqueidFilter: ["uniqueId"],
  updatedByFilter: ["updatedBy.uniqueId"],
  urlTemplateFilter: ["urlTemplate"],
  vocabularyFilter: ["vocabularyTermSetVocabulary.name"],
  vocabularyNameFilter: ["vocabulary.name"],
  vocabularyTermSetDescriptionFilter: ["vocabularyTermSetDescription"],
  withFilter: ["with.geneSymbol.displayText", "with.geneFullName.displayText", "with.curie"],
});

