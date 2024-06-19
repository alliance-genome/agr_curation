import React from 'react';
import { getIdentifier } from '../../utils/utils';

export function GeneticModifiersAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData.length > 0) {
		for (let i = 0; i < fieldData.length; i++) {
			if (getIdentifier(fieldData[i]) !== '') {
				if (fieldData[i]['type'] === 'Gene')
					ret.push(
						<div
							key={i}
							className="p-info"
							dangerouslySetInnerHTML={{ __html: fieldData[i]['geneSymbol']['displayText'] + '(Gene)' }}
						></div>
					);
				else if (fieldData[i]['type'] === 'Allele')
					ret.push(
						<div
							key={i}
							className="p-info"
							dangerouslySetInnerHTML={{ __html: fieldData[i]['alleleSymbol']['displayText'] + '(Allele)' }}
						></div>
					);
				else if (fieldData[i]['type'] === 'AffectedGenomicModel')
					ret.push(
						<div key={i} className="p-info" dangerouslySetInnerHTML={{ __html: fieldData[i]['name'] + '(AGM)' }}></div>
					);
			}
		}
	}
	return ret;
}
