import React, { useRef, useState } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { onSelectionOver } from '../../utils/utils';
import { EditorTooltip } from "./EditorTooltip";
import { getIdentifier } from '../../utils/utils';

export const AutocompleteFormEditor = (
	{
		search,
		initialValue,
		name,
		rowProps,
		classNames,
		inputClassNames,
		fieldName,
		subField = "curie",
		valueDisplay,
		onValueChangeHandler,
		disabled
	}
) => {
	const [suggestions, setSuggestions] = useState([]);
	const [query, setQuery] = useState(initialValue);
	const [autocompleteHoverItem, setAutocompleteHoverItem] = useState({});
	const op = useRef(null);

	const itemTemplate = (item) => {
		if(valueDisplay) return valueDisplay(item, setAutocompleteHoverItem, op, query);

		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{__html: item.name + ' (' + getIdentifier(item) + ') '}}/>
			</div>
		);
	};

	return (
		<div>
			<AutoComplete
				name={name}
				panelStyle={{width: '15%', display: 'flex', maxHeight: '350px'}}
				field={subField}
				value={initialValue}
				disabled={disabled}
				suggestions={suggestions}
				itemTemplate={itemTemplate}
				completeMethod={(event) => search(event, setSuggestions, setQuery, rowProps)}
				onHide={(e) => op.current.hide(e)}
				onChange={(e) => onValueChangeHandler(e)}
				className={classNames}
				inputClassName={inputClassNames}
			/>
			<EditorTooltip op={op} autocompleteHoverItem={autocompleteHoverItem} dataType={fieldName}/>
		</div>
	)
}
