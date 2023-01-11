import React from 'react';

export function AssertedAlleleAdditionalFieldData({ fieldData }) {
	if (fieldData) {
		if (fieldData["curie"] !== '' && fieldData["type"] === "Allele")
			return <div className="p-info">{fieldData["alleleSymbol"]["displayText"]}</div>;
	}
	return null;
};
