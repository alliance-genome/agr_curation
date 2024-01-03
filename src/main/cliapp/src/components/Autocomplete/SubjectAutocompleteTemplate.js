import React from 'react';
import { onSelectionOver, getIdentifier } from '../../utils/utils';

export const SubjectAutocompleteTemplate = ({ item, setAutocompleteHoverItem, op, query }) => {

	if (item.geneSymbol) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{__html: item.geneSymbol.displayText + ' (' + getIdentifier(item) + ') '}}/>
			</div>
		);
	} else if (item.alleleSymbol) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.alleleSymbol.displayText + ' (' + getIdentifier(item) + ') '}}/>
			</div>
		);
	} else if (item.geneFullName) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.geneFullName.displayText + ' (' + getIdentifier(item) + ') '}}/>
			</div>
		);
	} else if (item.alleleFullName) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.alleleFullName.displayText + ' (' + getIdentifier(item) + ') '}}/>
			</div>
		);
	} else if (item.name) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.name + ' (' + getIdentifier(item) + ') '}}/>
			</div>
		);
	} else {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: getIdentifier(item)}}/>
			</div>
		);
	};
};
