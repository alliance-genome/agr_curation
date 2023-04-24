import React, { useRef, useState } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { onSelectionOver } from '../../utils/utils';
import { EditorTooltip } from "./EditorTooltip";

export const AutocompleteFormMultiEditor = (
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
	const [inputValue, setInputValue] = useState(initialValue);
	const [autocompleteHoverItem, setAutocompleteHoverItem] = useState({});
	const op = useRef(null);

	const itemTemplate = (item) => {
		if(valueDisplay) return valueDisplay(item, setAutocompleteHoverItem, op, inputValue);

		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, inputValue, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
			</div>
		);
	};

	return (
		<div style={{width: '300px'}}>
			<AutoComplete
				ref={customRef}
				name={name}
				multiple={true}
				panelStyle={{width: '15%', display: 'flex', maxHeight: '350px'}}
				field={subField}
				value={initialValue}
				disabled={disabled}
				suggestions={suggestions}
				itemTemplate={itemTemplate}
				completeMethod={(event) => search(event, setSuggestions, setInputValue, rowProps)}
				onHide={(e) => op.current.hide(e)}
				onChange={(e) => onValueChangeHandler(e)}
				className={classNames}
			/>
			<EditorTooltip op={op} autocompleteHoverItem={autocompleteHoverItem} dataType={fieldName}/>
		</div>
	)
}
