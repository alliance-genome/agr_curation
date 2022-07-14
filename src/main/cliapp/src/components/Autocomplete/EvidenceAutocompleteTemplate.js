import React from 'react';
import { onSelectionOver } from '../../utils/utils';

export const EvidenceAutocompleteTemplate = ({ item, setAutocompleteSelectedItem, op, query }) => {

	return (
		<div>
			<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)}
				 dangerouslySetInnerHTML={{__html: item.abbreviation + ' - ' + item.name + ' (' + item.curie + ') '}}/>
		</div>
	);
};
