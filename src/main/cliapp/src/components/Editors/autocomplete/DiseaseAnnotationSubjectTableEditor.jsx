import { AutocompleteSingleTableEditor } from './AutocompleteSingleTableEditor';
import { SubjectAutocompleteTemplate } from '../../Autocomplete/SubjectAutocompleteTemplate';
import { Endpoints } from '../../../constants/Endpoints';
import { getIdentifier } from '../../../utils/utils';
//This editor is isolated because it has to be functional for AGMs, Genes, and Alleles
//Could potentially be reused in future multi-subject annotation tables
const getEndpoint = (rowData) => {
	if (rowData?.type === 'GeneDiseaseAnnotation') return Endpoints.Entity.GENE;
	if (rowData?.type === 'AlleleDiseaseAnnotation') return Endpoints.Entity.ALLELE;
	if (rowData?.type === 'AGMDiseaseAnnotation') return Endpoints.Entity.AGM;
	return Endpoints.Entity.BIOLOGICAL_ENTITY;
};

const getAutocompleteFields = (rowData) => {
	const fields = ['curie', 'primaryExternalId', 'modInternalId', 'crossReferences.referencedCurie'];
	if (rowData.type === 'AGMDiseaseAnnotation') {
		fields.push(
			'agmFullName.formatText',
			'agmFullName.displayText',
			'agmSynonyms.formatText',
			'agmSynonyms.displayText',
			'agmSecondaryIds.secondaryId'
		);
	} else if (rowData.type === 'AlleleDiseaseAnnotation') {
		fields.push(
			'alleleFullName.formatText',
			'alleleFullName.displayText',
			'alleleSymbol.formatText',
			'alleleSymbol.displayText',
			'alleleSynonyms.formatText',
			'alleleSynonyms.displayText',
			'alleleSecondaryIds.secondaryId'
		);
	} else if (rowData.type === 'GeneDiseaseAnnotation') {
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

const subjectValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const DiseaseAnnotationSubjectTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => {
	const endpoint = getEndpoint(editorOptions.rowData);
	const autocompleteFields = getAutocompleteFields(editorOptions.rowData);
	const initialValue = getIdentifier(editorOptions.rowData.diseaseAnnotationSubject);

	return (
		<AutocompleteSingleTableEditor
			editorOptions={editorOptions}
			field="diseaseAnnotationSubject"
			subField="primaryExternalId"
			endpoint={endpoint}
			autocompleteFields={autocompleteFields}
			filterName="diseaseAnnotationSubjectFilter"
			initialValue={initialValue}
			valueDisplay={subjectValueDisplay}
			errorMessagesRef={errorMessagesRef}
			uiErrorMessagesRef={uiErrorMessagesRef}
		/>
	);
};
