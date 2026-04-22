import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { TableEditorErrors } from '../../Error/TableEditorErrors';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange } from '../../../utils/utils';

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
		const resolvedOtherFilters = typeof otherFilters === 'function' ? otherFilters(editorOptions) : otherFilters;
		setQuery(event.query);
		autocompleteSearch(new SearchService(), endpoint, filterName, filter, setFiltered, resolvedOtherFilters);
	};

	const onValueChange = (event, setFieldValue, editorOptions) => {
		defaultAutocompleteOnChange(editorOptions, event, field, setFieldValue, subField);
	};

	return (
		<>
			<AutocompleteEditor
				search={search}
				initialValue={initialValue ?? editorOptions.rowData[field]?.[subField]}
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
