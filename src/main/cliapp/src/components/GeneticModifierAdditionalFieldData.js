import React from 'react';

export function GeneticModifierAdditionalFieldData({ fieldData }) {
	if (fieldData && fieldData["curie"]) {
		if (fieldData["curie"] !== '' && fieldData["type"] === "Gene")
			return <div className="p-info">{fieldData["geneSymbol"]["displayText"]} (Gene)</div>;
		else if (fieldData["curie"] !== '' && fieldData["type"] === "Allele")
			return <div className="p-info">{fieldData["alleleSymbol"]["displayText"]} (Allele)</div>;
		else if (fieldData["curie"] !== '' && fieldData["type"] === "AffectedGenomicModel")
			return <div className="p-info">{fieldData["name"]} (AGM)</div>;
	}
	return null;
};
