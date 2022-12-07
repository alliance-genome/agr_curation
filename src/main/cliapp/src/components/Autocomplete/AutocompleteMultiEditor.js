import React, { useRef, useState } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { onSelectionOver } from '../../utils/utils';
import { EditorTooltip } from "./EditorTooltip";

export const AutocompleteMultiEditor = (
	{
		search,
		initialValue,
		name,
		rowProps,
		classNames,
		fieldName,
		subField = "curie",
		valueDisplay,
		onValueChangeHandler,
		customRef,
		disabled
	}
) => {
	const [suggestions, setSuggestions] = useState([]);
	const [fieldValue, setFieldValue] = useState(initialValue);

	const [inputValue, setInputValue] = useState(initialValue);
	const [autocompleteHoverItem, setAutocompleteHoverItem] = useState({});
	const op = useRef(null);

	const itemTemplate = (item) => {
		if(valueDisplay) return valueDisplay(item, setAutocompleteHoverItem, op, inputValue);

		let nameValue = '';
		if (item.geneFullName) {
			nameValue = item.geneFullName.displayText;
		} else if (item.alleleFullName) {
			nameValue = item.alleleFullName.displayText;
		} else if (item.name) {
			nameValue = item.name;
		}
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, inputValue, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{__html: nameValue + ' (' + item.curie + ') '}}/>
				</div>
		);
	};

	return (
		<div>
			<AutoComplete
				ref={customRef}
				name={name}
				multiple={true}
				panelStyle={{width: '15%', display: 'flex', maxHeight: '350px'}}
				field={subField}
				value={fieldValue}
				disabled={disabled}
				suggestions={suggestions}
				itemTemplate={itemTemplate}
				completeMethod={(event) => search(event, setSuggestions, setInputValue, rowProps)}
				onHide={(e) => op.current.hide(e)}
				onChange={(e) => onValueChangeHandler(e, setFieldValue, rowProps)}
				className={classNames}
			/>
			<EditorTooltip op={op} autocompleteHoverItem={autocompleteHoverItem} dataType={fieldName}/>
		</div>
	)
}
