import React from 'react';

export function GeneticModifiersAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData.length > 0) {
		for(let i=0; i < fieldData.length; i++) {
			if (fieldData[i]["curie"]) {
				if (fieldData[i]["curie"] !== '' && fieldData[i]["type"] === "Gene")
					ret.push(<div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["geneSymbol"]["displayText"] + '(Gene)'}}></div>);
				else if (fieldData[i]["curie"] !== '' && fieldData[i]["type"] === "Allele")
					ret.push(<div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["alleleSymbol"]["displayText"] + '(Allele)'}}></div>);
				else if (fieldData[i]["curie"] !== '' && fieldData[i]["type"] === "AffectedGenomicModel")
					ret.push(<div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["name"] + '(AGM)'}}></div>);
			}
		}
	}
	return ret;
};
