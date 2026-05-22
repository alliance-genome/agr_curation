import { AutocompleteMultiEditor } from '../autocomplete/base/AutocompleteMultiEditor';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../../utils/utils';
import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';
import { LiteratureAutocompleteTemplate } from '../autocomplete/base/templates/LiteratureAutocompleteTemplate';
import { Endpoints } from '../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';

const referenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.referenceAutocompleteConfig);
	const endpoint = Endpoints.Document.LITERATURE_REFERENCE;
	const filterName = 'referencesFilter';
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};

export const ReferencesEditor = ({ editorOptions, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={referenceSearch}
				initialValue={editorOptions?.rowData?.references}
				editorOptions={editorOptions}
				fieldName="references"
				subField="curie"
				valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
					<LiteratureAutocompleteTemplate
						item={item}
						setAutocompleteHoverItem={setAutocompleteHoverItem}
						op={op}
						query={query}
					/>
				)}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'references'} />
		</>
	);
};
