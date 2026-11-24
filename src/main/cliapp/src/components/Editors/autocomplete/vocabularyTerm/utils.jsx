import { Endpoints } from '../../../../constants/Endpoints';
import { VocabTermAutocompleteTemplate } from '../base/templates/VocabTermAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const memberTermsSearchConfig = {
	endpoint: Endpoints.Vocabulary.TERM,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.nameOnlyAutocompleteConfig),
	filterName: 'memberTermsFilter',
	valueDisplay: (item, setAutocompleteSelectedItem, op, query) => (
		<VocabTermAutocompleteTemplate
			item={item}
			setAutocompleteSelectedItem={setAutocompleteSelectedItem}
			op={op}
			query={query}
		/>
	),
};

/**
 * The filter that limits a term search to one vocabulary. Callers add it to the member terms config themselves,
 * because the vocabulary is the one chosen on screen: the term set row being edited, or the New Vocabulary Term Set
 * form's vocabulary.
 *
 * @param {string} vocabularyName - the vocabulary's name
 * @returns {object} `otherFilters` for the member terms search
 */
export const buildMemberTermsOtherFilters = (vocabularyName) => ({
	vocabularyFilter: {
		'vocabulary.name': { queryString: vocabularyName },
	},
});

export const functionalImpactsSearchConfig = {
	endpoint: Endpoints.Vocabulary.TERM,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.nameOnlyAutocompleteConfig),
	filterName: 'functionalImpactFilter',
	// The vocabulary is fixed rather than read off the row, so the scoping filter belongs in the config.
	otherFilters: {
		vocabularyFilter: {
			'vocabulary.vocabularyLabel': { queryString: 'allele_functional_impact' },
		},
	},
	valueDisplay: (item, setAutocompleteSelectedItem, op, query) => (
		<VocabTermAutocompleteTemplate
			item={item}
			setAutocompleteSelectedItem={setAutocompleteSelectedItem}
			op={op}
			query={query}
		/>
	),
};
