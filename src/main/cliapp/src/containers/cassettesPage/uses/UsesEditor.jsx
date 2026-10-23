import { AutocompleteMultiEditor } from '../../../components/Editors/autocomplete/base/AutocompleteMultiEditor';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, getIdentifier } from '../../../utils/utils';
import { DialogErrorMessageComponent } from '../../../components/Error/DialogErrorMessageComponent';
import { Endpoints } from '../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';

// A use is an FBcv term from the 'experimental_tool_descriptor' namespace.
export const USE_NAMESPACE = 'experimental_tool_descriptor';

export const useNamespaceFilter = {
	namespaceFilter: {
		namespace: {
			queryString: USE_NAMESPACE,
			tokenOperator: 'AND',
			useKeywordFields: true,
		},
	},
};

const useSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig);
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, Endpoints.Ontology.FBCV, 'useFilter', filter, setFiltered, useNamespaceFilter);
};

// Matches the suggestion list, so a term reads the same before and after it is chosen.
const useLabel = (term) => (term?.name ? `${term.name} (${getIdentifier(term)})` : getIdentifier(term));

export const UsesEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={useSearch}
				initialValue={props?.rowData?.uses}
				editorOptions={props}
				fieldName="uses"
				subField="curie"
				selectedItemTemplate={useLabel}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'uses'} />
		</>
	);
};
