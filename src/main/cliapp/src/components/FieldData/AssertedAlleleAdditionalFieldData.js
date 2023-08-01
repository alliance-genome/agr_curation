import React from 'react';

export function AssertedAlleleAdditionalFieldData({ fieldData }) {
	if (fieldData) {
		if (fieldData["curie"] !== '' && fieldData["type"] === "Allele")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["alleleSymbol"]["displayText"]}}></div>;
	}
	return null;
};
