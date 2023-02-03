import React from 'react';

export function AssertedGenesAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData.length >0) {
		for(let i=0; i< fieldData.length; i++){
			if (fieldData[i]["curie"] !== '' && fieldData[i]["type"] === "Gene")
				ret.push(<div key={i} className="p-info"> {fieldData[i]["geneSymbol"]["displayText"]} </div>);
		}
	}
	return ret;
};
