import React from 'react';

export const TaxonAdditionalFieldData = ({ curie, name }) => {
	if (!curie) return null;
	return <div className="p-info">{`${name} (${curie})`}</div>;
};
