import React from 'react';
import { onSelectionOver } from '../../utils/utils';

export const ExConAutocompleteTemplate = ({ item, setAutocompleteSelectedItem, op, query }) => {

	return (
		<div>
			<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)}
				 dangerouslySetInnerHTML={{__html: item.conditionSummary}}/>
		</div>
	);
};
