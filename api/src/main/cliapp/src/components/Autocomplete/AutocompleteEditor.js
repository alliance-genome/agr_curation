import React, { useRef, useState } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { onSelectionOver } from '../../utils/utils';
import { EditorTooltip } from "./EditorTooltip";

export const AutocompleteEditor = (
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
	}
) => {
	const [suggestions, setSuggestions] = useState([]);
	const [fieldValue, setFieldValue] = useState(initialValue);
	const [query, setQuery] = useState(initialValue);
	const [autocompleteHoverItem, setAutocompleteHoverItem] = useState({});
	const op = useRef(null);

	const itemTemplate = (item) => {
		if(valueDisplay) return valueDisplay(item, setAutocompleteHoverItem, op, query);

		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
			</div>
		);
	};

	return (
		<div>
			<AutoComplete
				name={name}
				panelStyle={{width: '15%', display: 'flex', maxHeight: '350px'}}
				field={subField}
				value={fieldValue}
				suggestions={suggestions}
				itemTemplate={itemTemplate}
				completeMethod={(event) => search(event, setSuggestions, setQuery, rowProps)}
				onHide={(e) => op.current.hide(e)}
				onChange={(e) => onValueChangeHandler(e, setFieldValue, rowProps, fieldName)}
				className={classNames}
			/>
			<EditorTooltip op={op} autocompleteHoverItem={autocompleteHoverItem} dataType={fieldName}/>
		</div>
	)
}
