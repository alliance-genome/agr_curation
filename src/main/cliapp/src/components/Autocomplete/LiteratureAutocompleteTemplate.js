import React from 'react';
import { onSelectionOver } from '../../utils/utils';

export const LiteratureAutocompleteTemplate = ({ item, setAutocompleteSelectedItem, op, query }) => {

	let otherIdsString = '';

	if (item["secondaryCrossReferences"]) otherIdsString = item["secondaryCrossReferences"].join('|') + '|'; 

	otherIdsString = otherIdsString + item["curie"];

	return (
		<div>
			<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)}>
				{item["primaryCrossReference"] + ' (' + otherIdsString + ') '}
			</div>
		</div>
	);
};
