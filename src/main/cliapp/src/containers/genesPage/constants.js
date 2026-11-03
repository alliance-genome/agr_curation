import { slotAnnotationList, synonymFields, secondaryIdFields } from '../../utils/slotAnnotations';
import { haveSameCrossReferences, prepareCrossReferences, stripUiFields } from '../allelesPage/crossReferences/utils';
import { haveSameRelatedNotes, prepareRelatedNotes, stripRelatedNote } from '../allelesPage/relatedNotes/utils';

/**
 * The lists a gene holds through sub-resources of its own rather than in its payload, keyed by the name the detail
 * page provides each under, in the order the page saves them. Each entry configures `useEntitySubResource`.
 */
export const GENE_SUB_RESOURCES = {
	geneSynonyms: slotAnnotationList({
		path: 'synonyms',
		label: 'synonyms',
		errorField: 'geneSynonyms',
		fieldsOf: synonymFields,
		copy: false,
	}),
	geneSecondaryIds: slotAnnotationList({
		path: 'secondaryids',
		label: 'secondary IDs',
		errorField: 'geneSecondaryIds',
		fieldsOf: secondaryIdFields,
		copy: false,
	}),
	relatedNotes: {
		path: 'relatednotes',
		label: 'related notes',
		errorField: 'relatedNotes',
		prepare: prepareRelatedNotes,
		strip: stripRelatedNote,
		isSame: haveSameRelatedNotes,
	},
	crossReferences: {
		path: 'crossreferences',
		label: 'cross references',
		errorField: 'crossReferences',
		prepare: prepareCrossReferences,
		strip: stripUiFields,
		isSame: haveSameCrossReferences,
	},
};
