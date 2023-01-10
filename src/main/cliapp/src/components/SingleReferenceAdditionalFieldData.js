import React from 'react';

export function SingleReferenceAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData["curie"] !== '' && (fieldData['cross_references'] && fieldData['cross_references'].length > 0) ){
		for(let i=0; i<fieldData['cross_references'].length; i++){
			let curieData = fieldData['cross_references'][i].curie.split(":");
			if(["PMID", "FB", "MGI", "RGD", "SGD", "WB", "ZFIN", "XB"].includes(curieData[0])){
				ret.push(<div key={i} className="p-info">{fieldData['cross_references'][i].curie}</div>);
			}
		}
	}
	return ret;
};
