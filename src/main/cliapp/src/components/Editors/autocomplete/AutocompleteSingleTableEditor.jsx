import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { TableEditorErrors } from '../../Error/TableEditorErrors';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange } from '../../../utils/utils';

const searchService = new SearchService();

export const AutocompleteSingleTableEditor = ({
	editorOptions,
	errorMessagesRef,
	uiErrorMessagesRef,
	field,
	subField = 'curie',
	endpoint,
	autocompleteFields,
	filterName,
	otherFilters,
	valueDisplay,
	initialValue,
}) => {
	const search = (event, setFiltered, setQuery) => {
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const onValueChange = (event, setFieldValue, editorOptions) => {
		defaultAutocompleteOnChange(editorOptions, event, field, setFieldValue, subField);
	};

	return (
		<>
			<AutocompleteEditor
				search={search}
				initialValue={initialValue ?? editorOptions.rowData[field]?.[subField]}
				rowProps={editorOptions}
				fieldName={field}
				subField={subField}
				valueDisplay={valueDisplay}
				onValueChangeHandler={onValueChange}
			/>
			<TableEditorErrors
				errorMessagesRef={errorMessagesRef}
				uiErrorMessagesRef={uiErrorMessagesRef}
				rowIndex={editorOptions.rowIndex}
				field={field}
			/>
		</>
	);
};
