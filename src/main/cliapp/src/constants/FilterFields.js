export const FIELD_SETS = Object.freeze({
	abbreviationFieldSet: {
		filterName: 'abbreviationFilter',
		fields: ['abbreviation'],
	},
	abstractFieldSet: {
		filterName: 'abstractFilter',
		fields: ['abstract'],
	},
	aggregationDatabaseFieldSet: {
		filterName: 'aggregationDatabaseFilter',
		fields: ['aggregationDatabase.curie', 'aggregationDatabase.name'],
	},
	agmAggregationFieldSet: {
		filterName: 'alleleAggregationFilter',
		fields: ['dataProvider.abbreviation'],
	},
	agmNameFieldSet: {
		filterName: 'agmNameFilter',
		fields: ['agmFullName.displayText', 'agmFullName.formatText'],
	},
	agmSecondaryIdsFieldSet: {
		filterName: 'agmSecondaryIdsFilter',
		fields: ['agmSecondaryIds.secondaryId', 'agmSecondaryIds.evidence.curie'],
	},
	agmSynonymsFieldSet: {
		filterName: 'agmSynonymsFilter',
		fields: ['agmSynonyms.displayText', 'agmSynonyms.formatText'],
	},
	alleleAggregationFieldSet: {
		filterName: 'alleleAggregationFilter',
		fields: ['dataProvider.abbreviation'],
	},
	alleleFunctionalImpactsFieldSet: {
		filterName: 'alleleFunctionalImpactsFilter',
		fields: [
			'alleleFunctionalImpacts.functionalImpacts.name',
			'alleleFunctionalImpacts.phenotypeTerm.curie',
			'alleleFunctionalImpacts.phenotypeTerm.name',
			'alleleFunctionalImpacts.phenotypeStatement',
			'alleleFunctionalImpacts.evidence.curie',
		],
	},
	alleleNameFieldSet: {
		filterName: 'alleleNameFilter',
		fields: ['alleleFullName.displayText', 'alleleFullName.formatText'],
	},
	alleleSecondaryIdsFieldSet: {
		filterName: 'alleleSecondaryIdsFilter',
		fields: ['alleleSecondaryIds.secondaryId', 'alleleSecondaryIds.evidence.curie'],
	},
	alleleSymbolFieldSet: {
		filterName: 'alleleSymbolFilter',
		fields: ['alleleSymbol.displayText', 'alleleSymbol.formatText'],
	},
	agmAlleleAggregationFieldSet: {
		filterName: 'agmAlleleAggregationFilter',
		fields: ['agmAssociationSubject.dataProvider.abbreviation', 'relation.name', 'zygosity.name'],
	},
	agmAssociationSubjectFieldSet: {
		filterName: 'agmAssociationSubjectFilter',
		fields: [
			'agmAssociationSubject.agmFullName.displayText',
			'agmAssociationSubject.agmFullName.formatText',
			'agmAssociationSubject.name',
			'agmAssociationSubject.curie',
			'agmAssociationSubject.primaryExternalId',
			'agmAssociationSubject.modInternalId',
		],
	},
	agmAssociationSubjectTaxonFieldSet: {
		filterName: 'agmAssociationSubjectTaxonFilter',
		fields: ['agmAssociationSubject.taxon.curie', 'agmAssociationSubject.taxon.name'],
	},
	agmAlleleAssociationObjectFieldSet: {
		filterName: 'agmAlleleAssociationObjectFilter',
		fields: [
			'agmAlleleAssociationObject.alleleSymbol.displayText',
			'agmAlleleAssociationObject.alleleSymbol.formatText',
			'agmAlleleAssociationObject.curie',
			'agmAlleleAssociationObject.primaryExternalId',
			'agmAlleleAssociationObject.modInternalId',
		],
	},
	agmAlleleRelationFieldSet: {
		filterName: 'agmAlleleRelationFilter',
		fields: ['relation.name'],
	},
	agmAlleleDataProviderFieldSet: {
		filterName: 'agmAlleleDataProviderFilter',
		fields: [
			'agmAssociationSubject.dataProvider.abbreviation',
			'agmAssociationSubject.dataProvider.fullName',
			'agmAssociationSubject.dataProvider.shortName',
		],
	},
	zygosityFieldSet: {
		filterName: 'zygosityFilter',
		fields: ['zygosity.name'],
	},
	alleleAssociationSubjectFieldSet: {
		filterName: 'alleleAssociationSubjectFilter',
		fields: [
			'alleleAssociationSubject.alleleSymbol.displayText',
			'alleleAssociationSubject.alleleSymbol.formatText',
			'alleleAssociationSubject.curie',
			'alleleAssociationSubject.primaryExternalId',
			'alleleAssociationSubject.modInternalId',
		],
	},
	alleleAssociationSubjectTaxonFieldSet: {
		filterName: 'alleleAssociationSubjectTaxonFilter',
		fields: ['alleleAssociationSubject.taxon.curie', 'alleleAssociationSubject.taxon.name'],
	},
	alleleGeneAssociationObjectFieldSet: {
		filterName: 'alleleGeneAssociationObjectFilter',
		fields: [
			'alleleGeneAssociationObject.geneSymbol.displayText',
			'alleleGeneAssociationObject.geneSymbol.formatText',
			'alleleGeneAssociationObject.curie',
			'alleleGeneAssociationObject.primaryExternalId',
			'alleleGeneAssociationObject.modInternalId',
		],
	},
	alleleGeneRelationFieldSet: {
		filterName: 'alleleGeneRelationFilter',
		fields: ['relation.name'],
	},
	alleleGeneAggregationFieldSet: {
		filterName: 'alleleGeneAggregationFilter',
		fields: ['alleleAssociationSubject.dataProvider.abbreviation', 'relation.name'],
	},
	alleleGeneDataProviderFieldSet: {
		filterName: 'alleleGeneDataProviderFilter',
		fields: [
			'alleleAssociationSubject.dataProvider.abbreviation',
			'alleleAssociationSubject.dataProvider.fullName',
			'alleleAssociationSubject.dataProvider.shortName',
		],
	},
	alleleSynonymsFieldSet: {
		filterName: 'alleleSynonymsFilter',
		fields: ['alleleSynonyms.displayText', 'alleleSynonyms.formatText'],
	},
	alleleGermlineTransmissionStatusFieldSet: {
		filterName: 'alleleGermlineTransmissionStatusFilter',
		fields: [
			'alleleGermlineTransmissionStatus.germlineTransmissionStatus.name',
			'alleleGermlineTransmissionStatus.evidence.curie',
		],
	},
	alleleDatabaseStatusFieldSet: {
		filterName: 'alleleDatabaseStatusFilter',
		fields: ['alleleDatabaseStatus.databaseStatus.name', 'alleleDatabaseStatus.evidence.curie'],
	},
	alleleInheritanceModesFieldSet: {
		filterName: 'alleleInheritanceModesFilter',
		fields: [
			'alleleInheritanceModes.inheritanceMode.name',
			'alleleInheritanceModes.phenotypeTerm.curie',
			'alleleInheritanceModes.phenotypeTerm.name',
			'alleleInheritanceModes.phenotypeStatement',
			'alleleInheritanceModes.evidence.curie',
		],
	},
	alleleMutationFieldSet: {
		filterName: 'alleleMutationFilter',
		fields: [
			'alleleMutationTypes.mutationTypes.curie',
			'alleleMutationTypes.mutationTypes.name',
			'alleleMutationTypes.evidence.curie',
		],
	},
	alleleNomenclatureEventsFieldSet: {
		filterName: 'alleleNomenclatureEventsFilter',
		fields: ['alleleNomenclatureEvents.nomenclatureEvent.name', 'alleleNomenclatureEvents.evidence.curie'],
	},
	annotationTypeFieldSet: {
		filterName: 'annotationTypeFilter',
		fields: ['annotationType.name'],
	},
	assertedAllelesFieldSet: {
		filterName: 'assertedAllelesFilter',
		fields: [
			'assertedAlleles.alleleSymbol.displayText',
			'assertedAlleles.alleleSymbol.formatText',
			'assertedAlleles.curie',
			'assertedAlleles.primaryExternalId',
			'assertedAlleles.modInternalId',
		],
	},
	assertedGenesFieldSet: {
		filterName: 'assertedGenesFilter',
		fields: [
			'assertedGenes.geneSymbol.displayText',
			'assertedGenes.geneSymbol.formatText',
			'assertedGenes.curie',
			'assertedGenes.primaryExternalId',
			'assertedGenes.modInternalId',
		],
	},
	antibodyAggregationFieldSet: {
		filterName: 'antibodyAggregationFilter',
		fields: ['dataProvider.abbreviation', 'clonality.name', 'heavyChainIsotype.name', 'lightChainIsotype.name'],
	},
	antibodyDataProviderFieldSet: {
		// Dedicated field set (rather than the shared dataProviderFieldSet) so only Antibody's
		// filter targets the keyword field directly -- see clonalityFieldSet for why.
		filterName: 'dataProviderFilter',
		fields: ['dataProvider.abbreviation_keyword'],
	},
	antibodyTargetGenesFieldSet: {
		filterName: 'antibodyTargetGenesFilter',
		fields: [
			'antibodyTargetGenes.geneSymbol.displayText',
			'antibodyTargetGenes.geneSymbol.formatText',
			'antibodyTargetGenes.curie',
			'antibodyTargetGenes.primaryExternalId',
			'antibodyTargetGenes.modInternalId',
		],
	},
	antigenTaxonFieldSet: {
		filterName: 'antigenTaxonFilter',
		fields: ['antigenTaxon.curie', 'antigenTaxon.name'],
	},
	clonalityFieldSet: {
		// Targets the keyword field directly (not useKeywordFields) so this multiselect only ever
		// matches a selected value exactly -- no analyzed-field fallback to bleed across e.g. IgG/IgG1.
		filterName: 'clonalityFilter',
		fields: ['clonality.name_keyword'],
	},
	heavyChainIsotypeFieldSet: {
		filterName: 'heavyChainIsotypeFilter',
		fields: ['heavyChainIsotype.name_keyword'],
	},
	lightChainIsotypeFieldSet: {
		filterName: 'lightChainIsotypeFilter',
		fields: ['lightChainIsotype.name_keyword'],
	},
	originalReferenceFieldSet: {
		filterName: 'originalReferenceFilter',
		fields: ['originalReference.curie', 'originalReference.primaryCrossReferenceCurie'],
	},
	citationFieldSet: {
		filterName: 'citationFilter',
		fields: ['citation'],
	},
	literatureShortCitationFieldSet: {
		filterName: 'literatureShortCitationFilter',
		fields: ['short_citation'],
	},
	conditionAnatomyFieldSet: {
		filterName: 'conditionAnatomyFilter',
		fields: ['conditionAnatomy.curie', 'conditionAnatomy.name'],
	},
	conditionChemicalFieldSet: {
		filterName: 'conditionChemicalFilter',
		fields: ['conditionChemical.curie', 'conditionChemical.name'],
	},
	conditionClassFieldSet: {
		filterName: 'conditionClassFilter',
		fields: ['conditionClass.name', 'conditionClass.curie'],
	},
	conditionFreeTextFieldSet: {
		filterName: 'conditionFreeTextFilter',
		fields: ['conditionFreeText'],
	},
	conditionGeneOntologyFieldSet: {
		filterName: 'conditionGeneOntologyFilter',
		fields: ['conditionGeneOntology.curie', 'conditionGeneOntology.name'],
	},
	conditionIdFieldSet: {
		filterName: 'conditionIdFilter',
		fields: ['conditionId.curie', 'conditionId.name'],
	},
	conditionQuantityFieldSet: {
		filterName: 'conditionQuantityFilter',
		fields: ['conditionQuantity'],
	},
	conditionRelationTypeFieldSet: {
		filterName: 'conditionRelationFilter',
		fields: ['conditionRelationType.name'],
	},
	conditionRelationSummaryFieldSet: {
		filterName: 'conditionSummaryFilter',
		fields: ['conditionSummary'],
	},
	conditionRelationsHandleFieldSet: {
		filterName: 'conditionRelationHandleFilter',
		fields: ['conditionRelations.handle'],
	},
	conditionTaxonFieldSet: {
		filterName: 'conditionTaxonFilter',
		fields: ['conditionTaxon.curie', 'conditionTaxon.name'],
	},
	constructNameFieldSet: {
		filterName: 'constructNameFilter',
		fields: ['constructFullName.displayText', 'constructFullName.formatText'],
	},
	constructSymbolFieldSet: {
		filterName: 'constructSymbolFilter',
		fields: ['constructSymbol.displayText', 'constructSymbol.formatText'],
	},
	constructSynonymsFieldSet: {
		filterName: 'constructSynonymsFilter',
		fields: ['constructSynonyms.displayText', 'constructSynonyms.formatText'],
	},
	constructAggregationFieldSet: {
		filterName: 'constructAggregationFilter',
		fields: ['dataProvider.abbreviation'],
	},
	constructComponentsFieldSet: {
		filterName: 'constructComponentsFilter',
		fields: ['constructComponents.componentSymbol', 'constructComponents.relation.name'],
	},
	constructGenomicComponentsFieldSet: {
		filterName: 'constructGenomicComponentsFilter',
		fields: [
			'constructGenomicEntityAssociations.constructGenomicEntityAssociationObject.symbol',
			'constructGenomicEntityAssociations.constructGenomicEntityAssociationObject.name',
			'constructGenomicEntityAssociations.constructGenomicEntityAssociationObject.curie',
			'constructGenomicEntityAssociations.constructGenomicEntityAssociationObject.primaryExternalId',
			'constructGenomicEntityAssociations.constructGenomicEntityAssociationObject.modInternalId',
			'constructGenomicEntityAssociations.relation.name',
		],
	},
	crossReferenceFieldSet: {
		filterName: 'crossReferenceFilter',
		fields: [
			'crossReference.displayName',
			'crossReferences.resourceDescriptorPage.name',
			'crossReferences.referencedCurie',
		],
	},
	crossReferencesFieldSet: {
		filterName: 'crossReferencesFilter',
		fields: [
			'crossReferences.displayName',
			'crossReferences.resourceDescriptorPage.name',
			'crossReferences.referencedCurie',
		],
	},
	// Ontology-term indexes embed crossReferences with @IndexedEmbedded(includeDepth=1),
	// so the depth-2 path crossReferences.resourceDescriptorPage.name is NOT indexed
	// (biological-entity indexes include it explicitly via includePaths). Searching it
	// throws a Hibernate Search "Unknown field" on /doterm and the other ontology
	// endpoints (SCRUM-6220), so ontology autocompletes use this depth-1-only variant.
	ontologyCrossReferencesFieldSet: {
		filterName: 'crossReferencesFilter',
		fields: ['crossReferences.displayName', 'crossReferences.referencedCurie'],
	},
	// Antibody.crossReferences is likewise @IndexedEmbedded without resourceDescriptorPage.name
	// (see Antibody.java) -- same depth-2-not-indexed issue as ontologyCrossReferencesFieldSet above,
	// so the Antibody table's Cross References filter needs the same depth-1-only variant.
	antibodyCrossReferencesFieldSet: {
		filterName: 'crossReferencesFilter',
		fields: ['crossReferences.displayName', 'crossReferences.referencedCurie'],
	},
	daConditionRelationsHandleFieldSet: {
		filterName: 'daConditionRelationHandleFilter',
		fields: ['conditionRelations.handle', 'conditionRelations.conditions.conditionSummary'],
	},
	daConditionRelationsSummaryFieldSet: {
		filterName: 'conditionRelationsFilter',
		fields: ['conditionRelations.conditions.conditionSummary', 'conditionRelations.conditionRelationType.name'],
	},
	createdByFieldSet: {
		filterName: 'createdByFilter',
		fields: ['createdBy.uniqueId'],
	},
	literatureCrossReferenceFieldSet: {
		filterName: 'literatureCrossReferenceFilter',
		fields: ['cross_references.curie', 'cross_references.name'],
	},
	curieFieldSet: {
		filterName: 'curieFilter',
		fields: ['curie'],
	},
	dataProviderFieldSet: {
		filterName: 'dataProviderFilter',
		fields: ['dataProvider.abbreviation', 'dataProvider.fullName', 'dataProvider.shortName'],
	},
	dateCreatedFieldSet: {
		filterName: 'dateCreatedFilter',
		fields: ['dateCreated'],
	},
	dateUpdatedFieldSet: {
		filterName: 'dateUpdatedFilter',
		fields: ['dateUpdated'],
	},
	daAggregationFieldSet: {
		filterName: 'daAggregationFilter',
		fields: [
			'relation.name',
			'geneticSex.name',
			'annotationType.name',
			'diseaseGeneticModifierRelation.name',
			'diseaseQualifiers.name',
			'dataProvider.abbreviation',
			'secondaryDataProvider.abbreviation',
			'evidenceCodes.abbreviation',
		],
	},
	defaultUrlTemplateFieldSet: {
		filterName: 'defaultUrlTemplateFilter',
		fields: ['defaultUrlTemplate'],
	},
	definitionFieldSet: {
		filterName: 'definitionFilter',
		fields: ['definition'],
	},
	detectionMethodFieldSet: {
		filterName: 'detectionMethodFilter',
		fields: ['detectionMethod.curie', 'detectionMethod.name'],
	},
	diseaseAnnotationSubjectFieldSet: {
		filterName: 'diseaseAnnotationSubjectFilter',
		fields: [
			'diseaseAnnotationSubject.primaryExternalId',
			'diseaseAnnotationSubject.symbol',
			'diseaseAnnotationSubject.name',
		],
	},
	diseaseQualifiersFieldSet: {
		filterName: 'diseaseQualifiersFilter',
		fields: ['diseaseQualifiers.name'],
	},
	evidenceFieldSet: {
		filterName: 'evidenceFilter',
		fields: ['evidence.curie', 'evidence.primaryCrossReferenceCurie', 'evidence.crossReferenceCuries'],
	},
	evidenceCodesFieldSet: {
		filterName: 'evidenceCodesFilter',
		fields: ['evidenceCodes.abbreviation', 'evidenceCodes.name', 'evidenceCodes.curie'],
	},
	evidenceCodeFieldSet: {
		filterName: 'evidenceCodeFilter',
		fields: ['evidenceCode.curie', 'evidenceCode.name', 'evidenceCode.abbreviation'],
	},
	experimentalConditionFieldSet: {
		filterName: 'experimentalConditionFilter',
		fields: ['conditions.conditionSummary'],
	},
	formulaFieldSet: {
		filterName: 'formulaFilter',
		fields: ['formula'],
	},
	gcrpCrossReferenceFieldSet: {
		filterName: 'gcrpCrossReferenceFilter',
		fields: [
			'gcrpCrossReference.displayName',
			'gcrpCrossReference.resourceDescriptorPage.name',
			'gcrpCrossReference.referencedCurie',
		],
	},
	geneAggregationFieldSet: {
		filterName: 'geneAggregationFilter',
		fields: ['dataProvider.abbreviation'],
	},
	geneAssociationSubjectFieldSet: {
		filterName: 'geneAssociationSubjectFilter',
		fields: [
			'geneAssociationSubject.geneSymbol.displayText',
			'geneAssociationSubject.geneSymbol.formatText',
			'geneAssociationSubject.curie',
			'geneAssociationSubject.primaryExternalId',
			'geneAssociationSubject.modInternalId',
		],
	},
	geneGeneAssociationObjectFieldSet: {
		filterName: 'geneGeneAssociationObjectFilter',
		fields: [
			'geneGeneAssociationObject.geneSymbol.displayText',
			'geneGeneAssociationObject.geneSymbol.formatText',
			'geneGeneAssociationObject.curie',
			'geneGeneAssociationObject.primaryExternalId',
			'geneGeneAssociationObject.modInternalId',
		],
	},
	geneNameFieldSet: {
		filterName: 'geneNameFilter',
		fields: ['geneFullName.displayText', 'geneFullName.formatText'],
	},
	geneSecondaryIdsFieldSet: {
		filterName: 'geneSecondaryIdsFilter',
		fields: ['geneSecondaryIds.secondaryId', 'geneSecondaryIds.evidence.curie'],
	},
	geneSymbolFieldSet: {
		filterName: 'geneSymbolFilter',
		fields: ['geneSymbol.displayText', 'geneSymbol.formatText'],
	},
	geneSynonymsFieldSet: {
		filterName: 'geneSynonymsFilter',
		fields: ['geneSynonyms.displayText', 'geneSynonyms.formatText'],
	},
	geneSystematicNameFieldSet: {
		filterName: 'geneSystematicNameFilter',
		fields: ['geneSystematicName.displayText', 'geneSystematicName.formatText'],
	},
	geneticModifierAgmsFieldSet: {
		filterName: 'geneticModifierAgmsFilter',
		fields: [
			'diseaseGeneticModifierAgms.name',
			'diseaseGeneticModifierAgms.curie',
			'diseaseGeneticModifierAgms.primaryExternalId',
			'diseaseGeneticModifierAgms.modInternalId',
		],
	},
	geneticModifierAllelesFieldSet: {
		filterName: 'geneticModifierAllelesFilter',
		fields: [
			'diseaseGeneticModifierAlleles.alleleSymbol.displayText',
			'diseaseGeneticModifierAlleles.alleleSymbol.formatText',
			'diseaseGeneticModifierAlleles.curie',
			'diseaseGeneticModifierAlleles.primaryExternalId',
			'diseaseGeneticModifierAlleles.modInternalId',
		],
	},
	geneticModifierGenesFieldSet: {
		filterName: 'geneticModifierGenesFilter',
		fields: [
			'diseaseGeneticModifierGenes.geneSymbol.displayText',
			'diseaseGeneticModifierGenes.geneSymbol.formatText',
			'diseaseGeneticModifierGenes.curie',
			'diseaseGeneticModifierGenes.primaryExternalId',
			'diseaseGeneticModifierGenes.modInternalId',
		],
	},
	geneticModifierRelationFieldSet: {
		filterName: 'geneticModifierRelationFilter',
		fields: ['diseaseGeneticModifierRelation.name'],
	},
	geneticSexFieldSet: {
		filterName: 'geneticSexFilter',
		fields: ['geneticSex.name'],
	},
	geneTypeFieldSet: {
		filterName: 'geneTypeFilter',
		fields: ['geneType.curie', 'geneType.name'],
	},
	gmiAggregationFieldSet: {
		filterName: 'gmiAggregationFilter',
		fields: ['relation.name'],
	},
	handleFieldSet: {
		filterName: 'handleFilter',
		fields: ['handle'],
	},
	idExampleFieldSet: {
		filterName: 'idExampleFilter',
		fields: ['idExample'],
	},
	idPatternFieldSet: {
		filterName: 'idPatternFilter',
		fields: ['idPattern'],
	},
	inchiFieldSet: {
		filterName: 'inchiFilter',
		fields: ['inchi'],
	},
	inchiKeyFieldSet: {
		filterName: 'inchiKeyFilter',
		fields: ['inchiKey'],
	},
	inCollectionFieldSet: {
		filterName: 'inCollectionFilter',
		fields: ['inCollection.name'],
	},
	inferredAlleleFieldSet: {
		filterName: 'inferredAlleleFilter',
		fields: [
			'inferredAllele.alleleSymbol.displayText',
			'inferredAllele.alleleSymbol.formatText',
			'inferredAllele.curie',
			'inferredAllele.primaryExternalId',
			'inferredAllele.modInternalId',
		],
	},
	inferredGeneFieldSet: {
		filterName: 'inferredGeneFilter',
		fields: [
			'inferredGene.geneSymbol.displayText',
			'inferredGene.geneSymbol.formatText',
			'inferredGene.curie',
			'inferredGene.primaryExternalId',
			'inferredGene.modInternalId',
		],
	},
	interactorAGeneticPerturbationFieldSet: {
		filterName: 'interactorAGeneticPerturbationFilter',
		fields: [
			'interactorAGeneticPerturbation.alleleSymbol.displayText',
			'interactorAGeneticPerturbation.alleleSymbol.formatText',
			'interactorAGeneticPerturbation.curie',
			'interactorAGeneticPerturbation.primaryExternalId',
			'interactorAGeneticPerturbation.modInternalId',
		],
	},
	interactorBGeneticPerturbationFieldSet: {
		filterName: 'interactorBGeneticPerturbationFilter',
		fields: [
			'interactorBGeneticPerturbation.alleleSymbol.displayText',
			'interactorBGeneticPerturbation.alleleSymbol.formatText',
			'interactorBGeneticPerturbation.curie',
			'interactorBGeneticPerturbation.primaryExternalId',
			'interactorBGeneticPerturbation.modInternalId',
		],
	},
	interactorARoleFieldSet: {
		filterName: 'interactorARoleFilter',
		fields: ['interactorARole.curie', 'interactorARole.name'],
	},
	interactorBRoleFieldSet: {
		filterName: 'interactorBRoleFilter',
		fields: ['interactorBRole.curie', 'interactorBRole.name'],
	},
	interactorATypeFieldSet: {
		filterName: 'interactorATypeFilter',
		fields: ['interactorAType.curie', 'interactorAType.name'],
	},
	interactorBTypeFieldSet: {
		filterName: 'interactorBTypeFilter',
		fields: ['interactorBType.curie', 'interactorBType.name'],
	},
	interactionIdFieldSet: {
		filterName: 'interactionIdFilter',
		fields: ['interactionId'],
	},
	interactionSourceFieldSet: {
		filterName: 'interactionSourceFilter',
		fields: ['interactionSource.curie', 'interactionSource.name'],
	},
	interactionTypeFieldSet: {
		filterName: 'interactionTypeFilter',
		fields: ['interactionType.curie', 'interactionType.name'],
	},
	internalFieldSet: {
		filterName: 'internalFilter',
		fields: ['internal'],
	},
	isExtinctFieldSet: {
		filterName: 'isExtinctFilter',
		fields: ['isExtinct'],
	},
	iupacFieldSet: {
		filterName: 'iupacFilter',
		fields: ['iupac'],
	},
	memberTermsFieldSet: {
		filterName: 'memberTermsFilter',
		fields: ['memberTerms.name'],
	},
	primaryExternalIdFieldSet: {
		filterName: 'primaryExternalIdFilter',
		fields: ['primaryExternalId'],
	},
	modInternalIdFieldSet: {
		filterName: 'modInternalIdFilter',
		fields: ['modInternalId'],
	},
	curieFieldSet: {
		filterName: 'curieFilter',
		fields: ['curie'],
	},
	nameFieldSet: {
		filterName: 'nameFilter',
		fields: ['name'],
	},
	namespaceFieldSet: {
		filterName: 'namespaceFilter',
		fields: ['namespace'],
	},
	negatedFieldSet: {
		filterName: 'negatedFilter',
		fields: ['negated'],
	},
	diseaseAnnotationObjectFieldSet: {
		filterName: 'objectFilter',
		fields: ['diseaseAnnotationObject.curie', 'diseaseAnnotationObject.name'],
	},
	obsoleteFieldSet: {
		filterName: 'obsoleteFilter',
		fields: ['obsolete'],
	},
	ontologySynonymsFieldSet: {
		filterName: 'ontologySynonymsFilter',
		fields: ['synonyms.name'],
	},
	pageDescriptionFieldSet: {
		filterName: 'pageDescriptionFilter',
		fields: ['pageDescription'],
	},
	paAggregationFieldSet: {
		filterName: 'paAggregationFilter',
		fields: ['relation.name', 'dataProvider.abbreviation'],
	},
	paConditionRelationsSummaryFieldSet: {
		filterName: 'paConditionRelationsFilter',
		fields: ['conditionRelations.conditions.conditionSummary', 'conditionRelations.conditionRelationType.name'],
	},
	phenotypeAnnotationObjectFieldSet: {
		filterName: 'phenotypeAnnotationObjectFilter',
		fields: ['phenotypeAnnotationObject'],
	},
	phenotypeAnnotationSubjectFieldSet: {
		filterName: 'phenotypeAnnotationSubjectFilter',
		fields: [
			'phenotypeAnnotationSubject.symbol',
			'phenotypeAnnotationSubject.name',
			'phenotypeAnnotationSubject.curie',
			'phenotypeAnnotationSubject.primaryExternalId',
			'phenotypeAnnotationSubject.modInternalId',
		],
	},
	phenotypesOrTraitsFieldSet: {
		filterName: 'phenotypesOrTraitsFilter',
		fields: ['phenotypesOrTraits'],
	},
	prefixFieldSet: {
		filterName: 'prefixFilter',
		fields: ['prefix'],
	},
	referencesFieldSet: {
		filterName: 'referencesFilter',
		fields: ['references.curie', 'references.crossReferences.referencedCurie'],
	},
	relatedNoteFieldSet: {
		filterName: 'relatedNoteFilter',
		fields: ['relatedNote.freeText'],
	},
	relatedNotesFieldSet: {
		filterName: 'relatedNotesFilter',
		fields: ['relatedNotes.freeText'],
	},
	relationFieldSet: {
		filterName: 'relationFilter',
		fields: ['relation.name'],
	},
	resourceDescriptorFieldSet: {
		filterName: 'resourceDescriptorFilter',
		fields: ['resourceDescriptor.prefix', 'resourceDescriptor.name'],
	},
	secondaryDataProviderFieldSet: {
		filterName: 'secondaryDataProviderFilter',
		fields: ['secondaryDataProvider.abbreviation', 'secondaryDataProvider.fullName', 'secondaryDataProvider.shortName'],
	},
	subsetsFieldSet: {
		filterName: 'subsetsFilter',
		fields: ['subsets'],
	},
	// OntologyTerm.secondaryIdentifiers — the native indexed field on ontology
	// term entities. Different document path than the bridged secondaryIds field
	// used by BiologicalEntity subclasses (see secondaryIdsBridgedFieldSet).
	secondaryIdentifiersFieldSet: {
		filterName: 'secondaryIdsFilter',
		fields: ['secondaryIdentifiers'],
	},
	// Bridged from BiologicalEntityTypeBridge — denormalized flat field across
	// Gene/Allele/AGM/etc. (the per-type secondaryIds slot annotations). Path
	// differs from secondaryIdentifiersFieldSet, which targets ontology terms.
	secondaryIdsBridgedFieldSet: {
		filterName: 'secondaryIdsBridgedFilter',
		fields: ['secondaryIds'],
	},
	// Bridged from BiologicalEntityTypeBridge — denormalized flat symbol field
	// for Gene/Allele (AGM has no symbol).
	symbolFieldSet: {
		filterName: 'symbolFilter',
		fields: ['symbol'],
	},
	sgdStrainBackgroundFieldSet: {
		filterName: 'sgdStrainBackgroundFilter',
		fields: [
			'sgdStrainBackground.name',
			'sgdStrainBackground.curie',
			'sgdStrainBackground.primaryExternalId',
			'sgdStrainBackground.modInternalId',
		],
	},
	singleReferenceFieldSet: {
		filterName: 'singleReferenceFilter',
		fields: ['singleReference.curie', 'singleReference.crossReferences.referencedCurie'],
	},
	evidenceItemFieldSet: {
		filterName: 'evidenceItemFilter',
		fields: ['evidenceItem.curie', 'evidenceItem.crossReferenceCuries'],
	},
	smilesFieldSet: {
		filterName: 'smilesFilter',
		fields: ['smiles'],
	},
	sourceGeneralConsequenceFieldSet: {
		filterName: 'sourceGeneralConsequenceFilter',
		fields: ['sourceGeneralConsequence.name', 'sourceGeneralConsequence.curie'],
	},
	speciesTaxonCurieFieldSet: {
		filterName: 'speciesTaxonCurieFilter',
		fields: ['taxon.curie'],
	},
	speciesFullNameFieldSet: {
		filterName: 'speciesFullNameFilter',
		fields: ['fullName'],
	},
	speciesDisplayNameFieldSet: {
		filterName: 'speciesDisplayNameFilter',
		fields: ['displayName'],
	},
	speciesAbbreviationFieldSet: {
		filterName: 'speciesAbbreviationFilter',
		fields: ['abbreviation'],
	},
	speciesCommonNameFieldSet: {
		filterName: 'speciesCommonNameFilter',
		fields: ['commonNames'],
	},
	speciesAggregationFieldSet: {
		filterName: 'speciesAggregationFilter',
		fields: ['dataProvider.abbreviation'],
	},
	speciesAssemblyFieldSet: {
		filterName: 'speciesAssemblyFilter',
		fields: ['genomeAssembly.curie', 'genomeAssembly.primaryExternalId'],
	},
	subtypeFieldSet: {
		filterName: 'subtypeFilter',
		fields: ['subtype.name'],
	},
	synonymsFieldSet: {
		filterName: 'synonymsFilter',
		fields: ['synonyms'],
	},
	taxonFieldSet: {
		filterName: 'taxonFilter',
		fields: ['taxon.curie', 'taxon.name'],
	},
	titleFieldSet: {
		filterName: 'titleFilter',
		fields: ['title'],
	},
	uniqueidFieldSet: {
		filterName: 'uniqueidFilter',
		fields: ['uniqueId'],
	},
	updatedByFieldSet: {
		filterName: 'updatedByFilter',
		fields: ['updatedBy.uniqueId'],
	},
	urlTemplateFieldSet: {
		filterName: 'urlTemplateFilter',
		fields: ['urlTemplate'],
	},
	variantAggregationFieldSet: {
		filterName: 'variantAggregationFilter',
		fields: ['variantStatus.name', 'dataProvider.abbreviation'],
	},
	variantStatusFieldSet: {
		filterName: 'variantStatusFilter',
		fields: ['variantStatus.name'],
	},
	variantTypeFieldSet: {
		filterName: 'variantTypeFilter',
		fields: ['variantType.name', 'variantType.curie'],
	},
	vocabularyDescriptionFieldSet: {
		filterName: 'vocabularyDescriptionFilter',
		fields: ['vocabularyDescription'],
	},
	vocabularyFieldSet: {
		filterName: 'vocabularyFilter',
		fields: ['vocabularyTermSetVocabulary.name'],
	},
	vocabularyLabelFieldSet: {
		filterName: 'vocabularyLabelFilter',
		fields: ['vocabularyLabel'],
	},
	vocabularyNameFieldSet: {
		filterName: 'vocabularyNameFilter',
		fields: ['vocabulary.name'],
	},
	vocabularyTermSetDescriptionFieldSet: {
		filterName: 'vocabularyTermSetDescriptionFilter',
		fields: ['vocabularyTermSetDescription'],
	},
	withFieldSet: {
		filterName: 'withFilter',
		fields: [
			'with.geneSymbol.displayText',
			'with.geneSymbol.formatText',
			'with.curie',
			'with.primaryExternalId',
			'with.modInternalId',
		],
	},
	geaExperimentUniqueidFieldSet: {
		filterName: 'geaExperimentUniqueidFilter',
		fields: ['expressionExperiment.uniqueId'],
	},
	geaExperimentPrimaryExternalIdFieldSet: {
		filterName: 'geaExperimentPrimaryExternalIdFilter',
		fields: ['expressionExperiment.primaryExternalId'],
	},
	geaExperimentCrossRefsFieldSet: {
		filterName: 'geaExperimentCrossRefsFilter',
		fields: [
			'expressionExperiment.crossReferences.referencedCurie',
			'expressionExperiment.crossReferences.displayName',
		],
	},
	geaAggregationFieldSet: {
		filterName: 'geaAggregationFilter',
		fields: ['dataProvider.abbreviation'],
	},
	geaExperimentSingleReferenceFieldSet: {
		filterName: 'geaExperimentSingleReferenceFilter',
		fields: [
			'expressionExperiment.singleReference.curie',
			'expressionExperiment.singleReference.primaryCrossReferenceCurie',
			'expressionExperiment.singleReference.crossReferences.referencedCurie',
		],
	},
	geaSubjectFieldSet: {
		filterName: 'geaSubjectFilter',
		fields: [
			'expressionAnnotationSubject.geneSymbol.displayText',
			'expressionAnnotationSubject.geneSymbol.formatText',
			'expressionAnnotationSubject.curie',
			'expressionAnnotationSubject.primaryExternalId',
			'expressionAnnotationSubject.modInternalId',
		],
	},
	geaAssayUsedFieldSet: {
		filterName: 'geaAssayUsedFilter',
		fields: ['expressionAssayUsed.name', 'expressionAssayUsed.curie'],
	},
	geaRelationFieldSet: {
		filterName: 'geaRelationFilter',
		fields: ['relation.name'],
	},
	geaWhereExpressedFieldSet: {
		filterName: 'geaWhereExpressedFilter',
		fields: ['whereExpressedStatement'],
	},
	geaWhenExpressedFieldSet: {
		filterName: 'geaWhenExpressedFilter',
		fields: ['whenExpressedStageName'],
	},
	geaExperimentInternalFieldSet: {
		filterName: 'geaExperimentInternalFilter',
		fields: ['expressionExperiment.internal'],
	},
	geaExperimentObsoleteFieldSet: {
		filterName: 'geaExperimentObsoleteFilter',
		fields: ['expressionExperiment.obsolete'],
	},
	geaExperimentCreatedByFieldSet: {
		filterName: 'geaExperimentCreatedByFilter',
		fields: ['expressionExperiment.createdBy.uniqueId'],
	},
	geaExperimentDateCreatedFieldSet: {
		filterName: 'geaExperimentDateCreatedFilter',
		fields: ['expressionExperiment.dateCreated'],
	},
	geaExperimentUpdatedByFieldSet: {
		filterName: 'geaExperimentUpdatedByFilter',
		fields: ['expressionExperiment.updatedBy.uniqueId'],
	},
	geaExperimentDateUpdatedFieldSet: {
		filterName: 'geaExperimentDateUpdatedFilter',
		fields: ['expressionExperiment.dateUpdated'],
	},
	geaExperimentCurieFieldSet: {
		filterName: 'geaExperimentCurieFilter',
		fields: ['expressionExperiment.curie'],
	},
});

export const FILTER_CONFIGS = Object.freeze({
	abbreviationFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.abbreviationFieldSet] },
	abstractFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.abstractFieldSet] },
	aggregationDatabaseFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.aggregationDatabaseFieldSet],
	},
	agmNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.agmNameFieldSet] },
	agmSecondaryIdsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.agmSecondaryIdsFieldSet] },
	agmSynonymsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.agmSynonymsFieldSet] },
	alleleFunctionalImpactsFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleFunctionalImpactsFieldSet],
	},
	alleleGermlineTransmissionStatusFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleGermlineTransmissionStatusFieldSet],
	},
	alleleDatabaseStatusFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleDatabaseStatusFieldSet],
	},
	alleleInheritanceModesFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleInheritanceModesFieldSet],
	},
	alleleMutationFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.alleleMutationFieldSet] },
	alleleNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.alleleNameFieldSet] },
	alleleNomenclatureEventsFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleNomenclatureEventsFieldSet],
	},
	alleleSecondaryIdsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.alleleSecondaryIdsFieldSet] },
	alleleSymbolFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.alleleSymbolFieldSet] },
	agmAssociationSubjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.agmAssociationSubjectFieldSet],
	},
	agmAssociationSubjectTaxonFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.agmAssociationSubjectTaxonFieldSet],
	},
	agmAlleleAssociationObjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.agmAlleleAssociationObjectFieldSet],
	},
	agmAlleleRelationFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.agmAlleleRelationFieldSet],
		aggregationFieldSet: FIELD_SETS.agmAlleleAggregationFieldSet,
		useKeywordFields: true,
	},
	agmAlleleDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.agmAlleleDataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.agmAlleleAggregationFieldSet,
		useKeywordFields: true,
	},
	zygosityFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.zygosityFieldSet],
		aggregationFieldSet: FIELD_SETS.agmAlleleAggregationFieldSet,
		useKeywordFields: true,
	},
	alleleAssociationSubjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleAssociationSubjectFieldSet],
	},
	alleleAssociationSubjectTaxonFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleAssociationSubjectTaxonFieldSet],
	},
	alleleGeneAssociationObjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.alleleGeneAssociationObjectFieldSet],
	},
	alleleGeneRelationFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.alleleGeneRelationFieldSet],
		aggregationFieldSet: FIELD_SETS.alleleGeneAggregationFieldSet,
		useKeywordFields: true,
	},
	alleleGeneDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.alleleGeneDataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.alleleGeneAggregationFieldSet,
		useKeywordFields: true,
	},
	alleleSynonymsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.alleleSynonymsFieldSet] },
	assertedAllelesFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.assertedAllelesFieldSet] },
	assertedGenesFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.assertedGenesFieldSet] },
	citationFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.citationFieldSet] },
	literatureShortCitationFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.literatureShortCitationFieldSet],
	},
	conditionAnatomyFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionAnatomyFieldSet] },
	conditionChemicalFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionChemicalFieldSet] },
	conditionClassFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionClassFieldSet] },
	conditionFreeTextFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionFreeTextFieldSet] },
	conditionGeneOntologyFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.conditionGeneOntologyFieldSet],
	},
	conditionIdFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionIdFieldSet] },
	conditionRelationHandleFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.handleFieldSet] },
	conditionRelationSummaryFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.conditionRelationSummaryFieldSet],
	},
	conditionQuantityFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionQuantityFieldSet] },
	conditionTaxonFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.conditionTaxonFieldSet] },
	constructNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.constructNameFieldSet] },
	constructSymbolFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.constructSymbolFieldSet] },
	constructSynonymsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.constructSynonymsFieldSet] },
	constructComponentsFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.constructComponentsFieldSet],
	},
	constructGenomicComponentsFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.constructGenomicComponentsFieldSet],
	},
	antibodyDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.antibodyDataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.antibodyAggregationFieldSet,
	},
	antibodyTargetGenesFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.antibodyTargetGenesFieldSet],
	},
	antigenTaxonFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.antigenTaxonFieldSet] },
	clonalityFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.clonalityFieldSet],
		aggregationFieldSet: FIELD_SETS.antibodyAggregationFieldSet,
	},
	heavyChainIsotypeFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.heavyChainIsotypeFieldSet],
		aggregationFieldSet: FIELD_SETS.antibodyAggregationFieldSet,
	},
	lightChainIsotypeFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.lightChainIsotypeFieldSet],
		aggregationFieldSet: FIELD_SETS.antibodyAggregationFieldSet,
	},
	originalReferenceFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.originalReferenceFieldSet],
	},
	createdByFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.createdByFieldSet] },
	crossReferenceFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.crossReferenceFieldSet] },
	crossReferencesFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.crossReferencesFieldSet] },
	antibodyCrossReferencesFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.antibodyCrossReferencesFieldSet],
	},
	curieFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.curieFieldSet] },

	daConditionRelationsHandleFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.daConditionRelationsHandleFieldSet],
		nonNullFields: FIELD_SETS.conditionRelationsHandleFieldSet,
	},
	daConditionRelationsSummaryFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.daConditionRelationsSummaryFieldSet],
		nullFields: FIELD_SETS.conditionRelationsHandleFieldSet,
	},

	dateCreatedFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.dateCreatedFieldSet] },
	dateUpdatedFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.dateUpdatedFieldSet] },
	defaultUrlTemplateFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.defaultUrlTemplateFieldSet] },
	definitionFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.definitionFieldSet] },
	detectionMethodFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.detectionMethodFieldSet] },
	experimentalConditionFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.experimentalConditionFieldSet],
	},
	evidenceFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.evidenceFieldSet] },
	formulaFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.formulaFieldSet] },
	gcrpCrossReferenceFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.gcrpCrossReferenceFieldSet] },
	geneAssociationSubjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geneAssociationSubjectFieldSet],
	},
	geneGeneAssociationObjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geneGeneAssociationObjectFieldSet],
	},
	geneNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geneNameFieldSet] },
	geneSecondaryIdsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geneSecondaryIdsFieldSet] },
	geneSymbolFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geneSymbolFieldSet] },
	geneSynonymsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geneSynonymsFieldSet] },
	geneSystematicNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geneSystematicNameFieldSet] },
	geneTypeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geneTypeFieldSet] },
	geneticModifierAgmsFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geneticModifierAgmsFieldSet],
	},
	geneticModifierAllelesFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geneticModifierAllelesFieldSet],
	},
	geneticModifierGenesFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geneticModifierGenesFieldSet],
	},
	idExampleFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.idExampleFieldSet] },
	idPatternFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.idPatternFieldSet] },
	inchiFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.inchiFieldSet] },
	inchiKeyFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.inchiKeyFieldSet] },
	inCollectionFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.inCollectionFieldSet],
		useKeywordFields: true,
	},
	inferredAlleleFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.inferredAlleleFieldSet] },
	inferredGeneFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.inferredGeneFieldSet] },
	interactorAGeneticPerturbationFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.interactorAGeneticPerturbationFieldSet],
	},
	interactorBGeneticPerturbationFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.interactorBGeneticPerturbationFieldSet],
	},
	interactorARoleFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactorARoleFieldSet] },
	interactorBRoleFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactorBRoleFieldSet] },
	interactorATypeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactorATypeFieldSet] },
	interactorBTypeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactorBTypeFieldSet] },
	interactionIdFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactionIdFieldSet] },
	interactionSourceFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactionSourceFieldSet] },
	interactionTypeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.interactionTypeFieldSet] },
	iupacFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.iupacFieldSet] },
	literatureCrossReferenceFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.literatureCrossReferenceFieldSet],
	},
	primaryExternalIdFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.primaryExternalIdFieldSet] },
	curieFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.curieFieldSet] },
	modInternalIdFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.modInternalIdFieldSet] },
	nameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.nameFieldSet] },
	namespaceFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.namespaceFieldSet] },
	phenotypeAnnotationSubjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.phenotypeAnnotationSubjectFieldSet],
	},
	phenotypeAnnotationObjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.phenotypeAnnotationObjectFieldSet],
	},
	phenotypesOrTraitsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.phenotypesOrTraitsFieldSet] },
	diseaseAnnotationSubjectFieldConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.diseaseAnnotationSubjectFieldSet],
	},
	diseaseAnnotationObjectFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.diseaseAnnotationObjectFieldSet],
	},
	ontologySynonymsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.ontologySynonymsFieldSet] },
	paConditionRelationsSummaryFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.paConditionRelationsSummaryFieldSet],
	},
	pageDescriptionFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.pageDescriptionFieldSet] },
	prefixFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.prefixFieldSet] },
	referencesFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.referencesFieldSet] },
	relatedNoteFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.relatedNoteFieldSet] },
	relatedNotesFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.relatedNotesFieldSet] },
	resourceDescriptorFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.resourceDescriptorFieldSet] },
	subsetsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.subsetsFieldSet] },
	secondaryIdentifiersFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.secondaryIdentifiersFieldSet],
	},
	sgdStrainBackgroundFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.sgdStrainBackgroundFieldSet],
	},
	singleReferenceFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.singleReferenceFieldSet] },
	evidenceItemFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.evidenceItemFieldSet] },
	smilesFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.smilesFieldSet] },
	sourceGeneralConsequenceFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.sourceGeneralConsequenceFieldSet],
	},
	speciesTaxonCurieFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.speciesTaxonCurieFieldSet] },
	speciesFullNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.speciesFullNameFieldSet] },
	speciesDisplayNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.speciesDisplayNameFieldSet] },
	speciesCommonNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.speciesCommonNameFieldSet] },
	speciesAbbreviationFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.speciesAbbreviationFieldSet],
	},
	speciesAssemblyFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.speciesAssemblyFieldSet] },
	subtypeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.subtypeFieldSet] },
	synonymsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.synonymsFieldSet] },
	taxonFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.taxonFieldSet] },
	titleFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.titleFieldSet] },
	uniqueidFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.uniqueidFieldSet] },
	updatedByFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.updatedByFieldSet] },
	urlTemplateFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.urlTemplateFieldSet] },
	variantTypeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.variantTypeFieldSet] },
	vocabularyDescriptionFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.vocabularyDescriptionFieldSet],
	},
	vocabularyFieldSetFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.vocabularyFieldSet] },
	vocabularyMemberTermsFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.memberTermsFieldSet] },
	vocabularyLabelFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.vocabularyLabelFieldSet] },
	vocabularyNameFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.vocabularyNameFieldSet] },
	vocabularyTermSetDescriptionFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.vocabularyTermSetDescriptionFieldSet],
	},
	withFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.withFieldSet] },
	geaExperimentUniqueidFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentUniqueidFieldSet],
	},
	geaExperimentPrimaryExternalIdFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentPrimaryExternalIdFieldSet],
	},
	geaExperimentCrossRefsFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentCrossRefsFieldSet],
	},
	geaExperimentSingleReferenceFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentSingleReferenceFieldSet],
	},
	geaSubjectFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geaSubjectFieldSet] },
	geaAssayUsedFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geaAssayUsedFieldSet] },
	geaDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.geaAggregationFieldSet,
		useKeywordFields: true,
	},
	geaRelationFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geaRelationFieldSet] },
	geaWhereExpressedFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geaWhereExpressedFieldSet] },
	geaWhenExpressedFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geaWhenExpressedFieldSet] },
	geaExperimentInternalFilterConfig: {
		filterComponentType: 'dropdown',
		fieldSets: [FIELD_SETS.geaExperimentInternalFieldSet],
	},
	geaExperimentObsoleteFilterConfig: {
		filterComponentType: 'dropdown',
		fieldSets: [FIELD_SETS.geaExperimentObsoleteFieldSet],
	},
	geaExperimentCreatedByFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentCreatedByFieldSet],
	},
	geaExperimentDateCreatedFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentDateCreatedFieldSet],
	},
	geaExperimentUpdatedByFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentUpdatedByFieldSet],
	},
	geaExperimentDateUpdatedFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.geaExperimentDateUpdatedFieldSet],
	},
	geaExperimentCurieFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.geaExperimentCurieFieldSet] },

	isExtinctFilterConfig: { filterComponentType: 'dropdown', fieldSets: [FIELD_SETS.isExtinctFieldSet] },
	obsoleteFilterConfig: { filterComponentType: 'dropdown', fieldSets: [FIELD_SETS.obsoleteFieldSet] },
	internalFilterConfig: { filterComponentType: 'dropdown', fieldSets: [FIELD_SETS.internalFieldSet] },
	negatedFilterConfig: {
		filterComponentType: 'dropdown',
		fieldSets: [FIELD_SETS.negatedFieldSet],
		options: [
			{ label: 'NOT', value: 'true' },
			{ label: 'null', value: 'false' },
		],
	},

	annotationTypeFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.annotationTypeFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	diseaseDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	phenotypeDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.paAggregationFieldSet,
		useKeywordFields: true,
	},
	alleleDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.alleleAggregationFieldSet,
		useKeywordFields: true,
	},
	constructDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.constructAggregationFieldSet,
		useKeywordFields: true,
	},
	geneDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.geneAggregationFieldSet,
		useKeywordFields: true,
	},
	agmDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.agmAggregationFieldSet,
		useKeywordFields: true,
	},
	diseaseQualifiersFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.diseaseQualifiersFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	relationFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.relationFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	paRelationFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.relationFieldSet],
		aggregationFieldSet: FIELD_SETS.paAggregationFieldSet,
		useKeywordFields: true,
	},
	gmiRelationFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.relationFieldSet],
		aggregationFieldSet: FIELD_SETS.gmiAggregationFieldSet,
		useKeywordFields: true,
	},
	geneticModifierRelationFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.geneticModifierRelationFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	geneticSexFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.geneticSexFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	secondaryDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.secondaryDataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	speciesDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.speciesAggregationFieldSet,
		useKeywordFields: true,
	},
	evidenceCodesFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.evidenceCodesFieldSet],
		aggregationFieldSet: FIELD_SETS.daAggregationFieldSet,
		useKeywordFields: true,
	},
	evidenceCodeFilterConfig: { filterComponentType: 'input', fieldSets: [FIELD_SETS.evidenceCodeFieldSet] },
	variantDataProviderFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.dataProviderFieldSet],
		aggregationFieldSet: FIELD_SETS.variantAggregationFieldSet,
		useKeywordFields: true,
	},
	variantStatusFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.variantStatusFieldSet],
		aggregationFieldSet: FIELD_SETS.variantAggregationFieldSet,
		useKeywordFields: true,
	},

	conditionRelationTypeFilterConfig: {
		filterComponentType: 'multiselect',
		fieldSets: [FIELD_SETS.conditionRelationTypeFieldSet],
		aggregationFieldSet: FIELD_SETS.conditionRelationTypeFieldSet,
	},

	// ALL Auto Complete Filters need to have useKeywordFields: true in order that exact matches come to the top of the list
	acLiteratureCrossReferenceFilterConfig: {
		filterComponentType: 'input',
		fieldSets: [FIELD_SETS.curieFieldSet, FIELD_SETS.literatureCrossReferenceFieldSet],
		useKeywordFields: true,
	},
});

// Autocomplete editors compose their searchable fields from the same FIELD_SETS
// used by column filters. Each entry lists fieldSets only — the runtime
// filterName, endpoint, valueDisplay, and otherFilters stay colocated with the
// editor's local search config.
//
// One config (biologicalEntityAutocompleteConfig) covers Gene/Allele/AGM and
// every other BiologicalEntity subtype because BiologicalEntityTypeBridge
// denormalizes type-specific symbol/name/synonyms/secondaryIds into flat
// `symbol`/`name`/`synonyms`/`secondaryIds` index fields on every entity. So a
// single set of bridge fields routes to gene fields on Gene docs, allele fields
// on Allele docs, etc.
export const AUTOCOMPLETE_CONFIGS = Object.freeze({
	// Bridged-only field set. Every path here exists on the index of every
	// BiologicalEntity subtype: curie/primaryExternalId/modInternalId are native,
	// crossReferences.* is native via GenomicEntity, and name/symbol/synonyms/
	// secondaryIds are declared for all subtypes by BiologicalEntityTypeBridge.
	// Therefore this config is safe to send to the per-type /gene, /allele, /agm
	// endpoints AND the combined /biologicalentity endpoint.
	// Do NOT add per-type slot-annotation paths (geneSymbol.*, geneSystematicName.*,
	// alleleSymbol.*, agm*.* etc.) here: a single-type index rejects fields it does
	// not have with a Hibernate Search "Unknown field" error
	biologicalEntityAutocompleteConfig: {
		fieldSets: [
			FIELD_SETS.curieFieldSet,
			FIELD_SETS.primaryExternalIdFieldSet,
			FIELD_SETS.modInternalIdFieldSet,
			FIELD_SETS.crossReferencesFieldSet,
			FIELD_SETS.nameFieldSet,
			FIELD_SETS.symbolFieldSet,
			FIELD_SETS.synonymsFieldSet,
			FIELD_SETS.secondaryIdsBridgedFieldSet,
		],
	},
	// Gene-only autocomplete: the bridged fields plus geneSystematicName, which is
	// indexed on Gene only (yeast curators identify genes by systematic name, e.g.
	// YGR240C). Use solely for searches targeting the /gene endpoint.
	geneAutocompleteConfig: {
		fieldSets: [
			FIELD_SETS.curieFieldSet,
			FIELD_SETS.primaryExternalIdFieldSet,
			FIELD_SETS.modInternalIdFieldSet,
			FIELD_SETS.crossReferencesFieldSet,
			FIELD_SETS.nameFieldSet,
			FIELD_SETS.symbolFieldSet,
			FIELD_SETS.synonymsFieldSet,
			FIELD_SETS.secondaryIdsBridgedFieldSet,
			FIELD_SETS.geneSystematicNameFieldSet,
		],
	},
	assertedGenesAutocompleteConfig: {
		fieldSets: [
			FIELD_SETS.curieFieldSet,
			FIELD_SETS.primaryExternalIdFieldSet,
			FIELD_SETS.modInternalIdFieldSet,
			FIELD_SETS.crossReferencesFieldSet,
			FIELD_SETS.nameFieldSet,
			FIELD_SETS.symbolFieldSet,
			FIELD_SETS.synonymsFieldSet,
		],
	},
	referenceAutocompleteConfig: {
		fieldSets: [FIELD_SETS.curieFieldSet, FIELD_SETS.literatureCrossReferenceFieldSet],
	},
	ontologyTermAutocompleteConfig: {
		fieldSets: [
			FIELD_SETS.curieFieldSet,
			FIELD_SETS.nameFieldSet,
			FIELD_SETS.ontologyCrossReferencesFieldSet,
			FIELD_SETS.secondaryIdentifiersFieldSet,
			FIELD_SETS.ontologySynonymsFieldSet,
		],
	},
	evidenceCodeAutocompleteConfig: {
		fieldSets: [FIELD_SETS.curieFieldSet, FIELD_SETS.nameFieldSet, FIELD_SETS.abbreviationFieldSet],
	},
	nameOnlyAutocompleteConfig: {
		fieldSets: [FIELD_SETS.nameFieldSet],
	},
	experimentalConditionAutocompleteConfig: {
		fieldSets: [FIELD_SETS.conditionRelationSummaryFieldSet],
	},
	experimentalConditionDetailedAutocompleteConfig: {
		fieldSets: [
			FIELD_SETS.conditionRelationSummaryFieldSet,
			FIELD_SETS.conditionIdFieldSet,
			FIELD_SETS.conditionClassFieldSet,
			FIELD_SETS.conditionTaxonFieldSet,
			FIELD_SETS.conditionGeneOntologyFieldSet,
			FIELD_SETS.conditionChemicalFieldSet,
			FIELD_SETS.conditionAnatomyFieldSet,
		],
	},
	resourceDescriptorAutocompleteConfig: {
		fieldSets: [FIELD_SETS.prefixFieldSet, FIELD_SETS.nameFieldSet],
	},
});

export const getAutocompleteFields = (config) => Array.from(new Set(config.fieldSets.flatMap((fs) => fs.fields)));
