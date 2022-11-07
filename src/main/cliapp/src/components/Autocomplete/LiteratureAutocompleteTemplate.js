import React from 'react';
import { onSelectionOver, getRefString } from '../../utils/utils';

export const LiteratureAutocompleteTemplate = ({ item, setAutocompleteHoverItem, op, query }) => {

	return (
		<div>
			<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteHoverItem)}>
				{getRefString(item)}
			</div>
		</div>
	);
};
