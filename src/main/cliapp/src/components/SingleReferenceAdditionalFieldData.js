import React from 'react';

export function SingleReferenceAdditionalFieldData({ fieldData }) {
	if (fieldData) {
		if (fieldData["curie"] !== '' && (fieldData['cross_references'] && fieldData['cross_references'].length > 0) ){
			for(let i=0; i<fieldData['cross_references'].length; i++){
				if(fieldData['cross_references'][i].curie.indexOf('PMID:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}else if(fieldData['cross_references'][i].curie.indexOf('FB:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}else if(fieldData['cross_references'][i].curie.indexOf('MGI:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}else if(fieldData['cross_references'][i].curie.indexOf('RGD:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}else if(fieldData['cross_references'][i].curie.indexOf('SGD:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}else if(fieldData['cross_references'][i].curie.indexOf('WB:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}else if(fieldData['cross_references'][i].curie.indexOf('ZFIN:') > -1){
					return <div className="p-info">{fieldData['cross_references'][i].curie}</div>;
				}
			}
		}
	}
	return null;
};
