import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { Endpoints } from '../../../../constants/Endpoints';

// Biological entities (Gene/Allele/AGM) are searched via a row-type-aware
// endpoint + autocompleteFields pair. Reused across multi-subject annotation
// tables (e.g. disease annotations), hence the generic "biologicalEntity" name.

export const getBiologicalEntityEndpoint = (rowData) => {
	if (rowData?.type === 'GeneDiseaseAnnotation') return Endpoints.Entity.GENE;
	if (rowData?.type === 'AlleleDiseaseAnnotation') return Endpoints.Entity.ALLELE;
	if (rowData?.type === 'AGMDiseaseAnnotation') return Endpoints.Entity.AGM;
	return Endpoints.Entity.BIOLOGICAL_ENTITY;
};

export const getBiologicalEntityAutocompleteFields = (rowData) => {
	const fields = ['curie', 'primaryExternalId', 'modInternalId', 'crossReferences.referencedCurie'];
	if (rowData?.type === 'AGMDiseaseAnnotation') {
		fields.push(
			'agmFullName.formatText',
			'agmFullName.displayText',
			'agmSynonyms.formatText',
			'agmSynonyms.displayText',
			'agmSecondaryIds.secondaryId'
		);
	} else if (rowData?.type === 'AlleleDiseaseAnnotation') {
		fields.push(
			'alleleFullName.formatText',
			'alleleFullName.displayText',
			'alleleSymbol.formatText',
			'alleleSymbol.displayText',
			'alleleSynonyms.formatText',
			'alleleSynonyms.displayText',
			'alleleSecondaryIds.secondaryId'
		);
	} else if (rowData?.type === 'GeneDiseaseAnnotation') {
		fields.push(
			'geneFullName.formatText',
			'geneFullName.displayText',
			'geneSymbol.formatText',
			'geneSymbol.displayText',
			'geneSynonyms.formatText',
			'geneSynonyms.displayText',
			'geneSystematicName.formatText',
			'geneSystematicName.displayText',
			'geneSecondaryIds.secondaryId'
		);
	}
	return fields;
};

export const biologicalEntityValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);
