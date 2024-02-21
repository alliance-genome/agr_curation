import React from 'react';
import { getIdentifier } from '../../utils/utils';

export function SubjectAdditionalFieldData({ fieldData }) {
	if (fieldData && getIdentifier(fieldData) !== '') {
		if (fieldData["type"] === "Gene")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["geneSymbol"]["displayText"] + '(Gene)'}}></div>;
		else if (fieldData["type"] === "Allele")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["alleleSymbol"]["displayText"] + '(Allele)'}}></div>;
		else if (fieldData["type"] === "AffectedGenomicModel")
			return <div className="p-info" dangerouslySetInnerHTML={{__html: fieldData["name"] + '(AGM)'}}></div>;
	}
	return null;
};
