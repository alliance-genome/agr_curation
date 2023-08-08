import React from 'react';

export const InCollectionAdditionalFieldData = ({ name }) => {
	if(!name) return null;
	return <div className="p-info">{name}</div>;
};
