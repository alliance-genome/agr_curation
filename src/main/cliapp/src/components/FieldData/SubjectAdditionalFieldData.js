import React from 'react';

export function SubjectAdditionalFieldData({ fieldData }) {
	if (fieldData && fieldData["curie"]) {
		if (fieldData["curie"] !== '' && fieldData["type"] === "Gene")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["geneSymbol"]["displayText"] + '(Gene)'}}></div>;
		else if (fieldData["curie"] !== '' && fieldData["type"] === "Allele")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["alleleSymbol"]["displayText"] + '(Allele)'}}></div>;
		else if (fieldData["curie"] !== '' && fieldData["type"] === "AffectedGenomicModel")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["name"] + '(AGM)'}}></div>;
	}
	return null;
};
