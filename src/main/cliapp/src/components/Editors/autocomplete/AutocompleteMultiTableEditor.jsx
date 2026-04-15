import { AutocompleteMultiEditor } from '../../Autocomplete/AutocompleteMultiEditor';
import { TableEditorErrors } from '../../Error/TableEditorErrors';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange } from '../../../utils/utils';

const searchService = new SearchService();

export const AutocompleteMultiTableEditor = ({
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
}) => {
	const search = (event, setFiltered, setInputValue) => {
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	};

	const onValueChange = (event, setFieldValue, editorOptions) => {
		multipleAutocompleteOnChange(editorOptions, event, field, setFieldValue);
	};

	return (
		<>
			<AutocompleteMultiEditor
				search={search}
				initialValue={editorOptions.rowData[field]}
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
