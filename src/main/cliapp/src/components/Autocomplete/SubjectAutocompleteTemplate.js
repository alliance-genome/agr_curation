import React from 'react';
import { onSelectionOver } from '../../utils/utils';

export const SubjectAutocompleteTemplate = ({ item, setAutocompleteHoverItem, op, query }) => {

	if (item.geneSymbol) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)}
					dangerouslySetInnerHTML={{__html: item.geneSymbol.displayText + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else if (item.alleleSymbol) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.alleleSymbol.displayText + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else if (item.geneFullName) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.geneFullName.displayText + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else if (item.alleleFullName) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.alleleFullName.displayText + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else if (item.name) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)} dangerouslySetInnerHTML={{__html: item.curie}}/>
			</div>
		);
	};
};
