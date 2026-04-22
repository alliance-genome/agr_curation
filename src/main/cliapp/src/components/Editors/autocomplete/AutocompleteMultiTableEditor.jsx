import { AutocompleteMultiEditor } from '../../Autocomplete/AutocompleteMultiEditor';
import { TableEditorErrors } from '../../Error/TableEditorErrors';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange } from '../../../utils/utils';

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
	initialValue,
}) => {
	const search = (event, setFiltered, setInputValue) => {
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const resolvedOtherFilters = typeof otherFilters === 'function' ? otherFilters(editorOptions) : otherFilters;
		setInputValue(event.query);
		autocompleteSearch(new SearchService(), endpoint, filterName, filter, setFiltered, resolvedOtherFilters);
	};

	const onValueChange = (event, setFieldValue, editorOptions) => {
		multipleAutocompleteOnChange(editorOptions, event, field, setFieldValue);
	};

	return (
		<>
			<AutocompleteMultiEditor
				search={search}
				initialValue={initialValue ?? editorOptions.rowData[field]}
				editorOptions={editorOptions}
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
