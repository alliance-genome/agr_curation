import React from 'react';
import { onSelectionOver } from '../../utils/utils';

export const ExConAutocompleteTemplate = ({ item, setAutocompleteHoverItem, op, query }) => {
	return (
		<div>
			<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)}
				 dangerouslySetInnerHTML={{__html: item.conditionSummary}}/>
		</div>
	);
};
