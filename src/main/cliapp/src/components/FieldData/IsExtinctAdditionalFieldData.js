import React from 'react';

export const IsExtinctAdditionalFieldData = ({ isExtinct }) => {
	if(!isExtinct) return null;
	return <div className="p-info">{isExtinct}</div>;
};
