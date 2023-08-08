import React from 'react';

export function EvidenceCodesAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData.length >0) {
		for(let i=0; i< fieldData.length; i++){
			ret.push(<div key={i} className="p-info"> {fieldData[i]["abbreviation"] + " " + fieldData[i]["name"]} </div>);
		}
	}
	return ret;
};
