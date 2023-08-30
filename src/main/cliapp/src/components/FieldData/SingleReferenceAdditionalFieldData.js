import React from 'react';

export function SingleReferenceAdditionalFieldData({ fieldData }) {
	let ret = [];
	if(!fieldData || fieldData.curie === '') return ret;
	if (fieldData['cross_references'] && fieldData['cross_references'].length > 0 ){
		createCrossReferenceDivs(ret, fieldData['cross_references'], 'curie', fieldData['short_citation']);
	} else if(fieldData['crossReferences'] && fieldData['crossReferences'].length > 0 ){//if the crossReference is coming from rowData
		createCrossReferenceDivs(ret, fieldData['crossReferences'], 'displayName', fieldData['short_citation']);
	}
	return ret;
};

const createCrossReferenceDivs = (resultList, crossReferences, field, shortCitation) => {
	let modList = ["PMID", "FB", "MGI", "RGD", "SGD", "WB", "ZFIN", "XB"];
	for(let i = 0; i < crossReferences.length; i++){
		let prefix = crossReferences[i][field]?.split(":")[0];
		if(modList.includes(prefix)){
			resultList.push(<div key={i} className="p-info">{crossReferences[i][field]}</div>);
		}
	}
	resultList.push(<div key={shortCitation} className="p-info">{shortCitation}</div>);
}
