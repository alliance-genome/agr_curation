import React, { useRef, useState } from 'react';
import { AutoComplete } from 'primereact/autocomplete';
import { onSelectionOver } from '../../utils/utils';
import { EditorTooltip } from './EditorTooltip';
import { getIdentifier } from '../../utils/utils';

export const AutocompleteMultiEditor = ({
	search,
	initialValue,
	rowProps,
	classNames,
	fieldName,
	subField = 'curie',
	valueDisplay,
	onValueChangeHandler,
	customRef,
	disabled,
}) => {
	const [suggestions, setSuggestions] = useState([]);
	const [fieldValue, setFieldValue] = useState(initialValue);

	const [inputValue, setInputValue] = useState(initialValue);
	const [autocompleteHoverItem, setAutocompleteHoverItem] = useState({});
	const op = useRef(null);

	const itemTemplate = (item) => {
		if (valueDisplay) return valueDisplay(item, setAutocompleteHoverItem, op, inputValue);

		return (
			<div>
				<div
					onMouseOver={(event) => onSelectionOver(event, item, inputValue, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{ __html: item.name + ' (' + getIdentifier(item) + ') ' }}
				/>
			</div>
		);
	};

	return (
		<div>
			<AutoComplete
				ref={customRef}
				name={fieldName}
				aria-label={fieldName}
				multiple={true}
				panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
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
			<EditorTooltip op={op} autocompleteHoverItem={autocompleteHoverItem} dataType={fieldName} />
		</div>
	);
};
