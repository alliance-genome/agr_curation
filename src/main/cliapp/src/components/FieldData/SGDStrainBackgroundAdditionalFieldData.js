import React from 'react';
import { getIdentifier } from '../../utils/utils';

export function SGDStrainBackgroundAdditionalFieldData({ fieldData }){
	if (fieldData) {
		if (getIdentifier(fieldData) !== '' && fieldData["name"] !== '')
			return <div className="p-info">{fieldData["name"]}</div>;
	}
	return null;
};
