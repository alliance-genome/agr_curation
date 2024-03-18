import React from 'react';
import { getIdentifier } from '../../utils/utils';

export function AssertedAlleleAdditionalFieldData({ fieldData }) {
	if (fieldData) {
		if (getIdentifier(fieldData) !== '' && fieldData["type"] === "Allele")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["alleleSymbol"]["displayText"]}}></div>;
	}
	return null;
};
