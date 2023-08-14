import React from 'react';

export const BooleanAdditionalFieldData = ({ value }) => {
	if(!value) return null;
	return <div className="p-info">{value}</div>;
};
