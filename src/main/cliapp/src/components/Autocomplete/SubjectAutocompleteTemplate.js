import React from 'react';
import { onSelectionOver } from '../../utils/utils';

export const SubjectAutocompleteTemplate = ({ item, setAutocompleteSelectedItem, op, query }) => {

	if (item.symbol) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)}
					dangerouslySetInnerHTML={{__html: item.symbol + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else if (item.name) {
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)} dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
			</div>
		);
	} else {
		return null;
	};
};
