import React from 'react';
import { onSelectionOver, getRefString } from '../../utils/utils';

export const LiteratureAutocompleteTemplate = ({ item, setAutocompleteSelectedItem, op, query }) => {

	return (
		<div>
			<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)}>
				{getRefString(item)}
			</div>
		</div>
	);
};
