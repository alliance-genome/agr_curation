import React from 'react'
import { EllipsisTableCell } from './EllipsisTableCell';
import { ListTableCell } from './ListTableCell';
import { AutocompleteMultiEditor } from './Autocomplete/AutocompleteMultiEditor';
import { DialogErrorMessageComponent } from './DialogErrorMessageComponent';
import { SearchService } from '../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange, getRefStrings } from '../utils/utils';
import { LiteratureAutocompleteTemplate } from './Autocomplete/LiteratureAutocompleteTemplate';


export const evidenceTemplate = (rowData) => {
	if (rowData && rowData.evidence) {
		const refStrings = getRefStrings(rowData.evidence);
		const listTemplate = (item) => {
			return (
				<EllipsisTableCell>
					{item}
				</EllipsisTableCell>
			);
		};
		return <ListTableCell template={listTemplate} listData={refStrings} />
	}
};

export const evidenceEditorTemplate = (props, errorMessages) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={evidenceSearch}
				initialValue={props.rowData.evidence}
				rowProps={props}
				fieldName='evidence'
				subField='curie'
				valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
					<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
				onValueChangeHandler={onEvidenceValueChange}
			/>
			<DialogErrorMessageComponent
				errorMessages={errorMessages[props.rowIndex]}
				errorField={"evidence"}
			/>
		</>
	);
};

export const onEvidenceValueChange = (event, setFieldValue, props) => {
	multipleAutocompleteOnChange(props, event, "evidence", setFieldValue);
};

export const evidenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ["curie", "cross_references.curie"];
	const endpoint = "literature-reference";
	const filterName = "evidenceFilter";
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
}

