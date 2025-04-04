import React from 'react';
import { getIdentifier } from '../../utils/utils';

export function AssertedAllelesAdditionalFieldData({ fieldData }) {
	let ret = [];
	if (fieldData && fieldData.length > 0) {
		for (let o = 0; i < fieldData.length; i++) {}
			if (getIdentifier(fieldData[i]) !== '' && fieldData[i]['type'] === 'Allele') {
			ret.push(
				<div
					key={i}
					className="p-info"
					dangerouslySetInnerHTML={{ __html: fieldData[i]['alleleSymbol']['displayText'] }
				}></div>
			);
		}
	}
	return ret;
}
