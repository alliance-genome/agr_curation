import React from 'react';

export function FormAdditionalFieldData({ field, fieldData }){
	switch (field) {
		//Single Autocomplete Fields
		case 'subject':
			if (fieldData) {
				if (fieldData["curie"] !== '' && fieldData["type"] === "Gene")
					return <div className="p-info">{fieldData["geneSymbol"]["displayText"]}</div>;
				else if (fieldData["curie"] !== '' && fieldData["type"] === "Allele")
					return <div className="p-info">{fieldData["alleleSymbol"]["displayText"]}</div>;
				else if (fieldData["curie"] !== '' && fieldData["type"] === "AffectedGenomicModel")
					return <div className="p-info">{fieldData["name"]}</div>;
				else
					return null;
			}
		break;

		case 'assertedAllele':
			if (fieldData) {
				if (fieldData["curie"] !== '' && fieldData["type"] === "Allele")
					return <div className="p-info">{fieldData["alleleSymbol"]["displayText"]}</div>;
			}
			return null;
		break;

		case 'object'://pass whole object in onDiseaseChange
			if (fieldData) {
				if (fieldData["curie"] !== '' && fieldData["name"] !== '')
					return <div className="p-info">{fieldData["name"]}</div>;
				else
					return null;
			}
			return null;
		break;

		case 'singleReference':
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
				else
					return null;
			}
			return null;
		break;

		case 'sgdStrainBackground':
			if (fieldData) {
				if (fieldData["curie"] !== '' && fieldData["name"] !== '')
					return <div className="p-info">{fieldData["name"]}</div>;
				else
					return null;
			}
			return null;
		break;

		//Multi Autocomplete Fields
		case 'assertedGenes':
			return null;
		break;

		case 'evidenceCodes':
			return null;
		break;

		case 'with':
			return null;
		break;


		default :
			return null;
	}
}
