import React from 'react';

export function SGDStrainBackgroundAdditionalFieldData({ fieldData }){
	if (fieldData) {
		if (fieldData["curie"] !== '' && fieldData["name"] !== '')
			return <div className="p-info">{fieldData["name"]}</div>;
	}
	return null;
};
