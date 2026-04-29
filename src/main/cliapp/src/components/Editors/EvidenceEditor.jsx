import { AutocompleteMultiEditor } from './autocomplete/base/AutocompleteMultiEditor';
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { LiteratureAutocompleteTemplate } from './autocomplete/base/templates/LiteratureAutocompleteTemplate';
import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { Endpoints } from '../../constants/Endpoints';

const evidenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ['curie', 'cross_references.curie'];
	const endpoint = Endpoints.Document.LITERATURE_REFERENCE;
	const filterName = 'evidenceFilter';
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};
export const EvidenceEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={evidenceSearch}
				initialValue={props?.rowData?.evidence}
				editorOptions={props}
				fieldName="evidence"
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
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'evidence'} />
		</>
	);
};
