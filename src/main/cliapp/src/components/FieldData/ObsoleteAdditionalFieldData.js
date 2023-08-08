import React from 'react';

export const ObsoleteAdditionalFieldData = ({ obsolete }) => {
	if(!obsolete) return null;
	return <div className="p-info">{obsolete}</div>;
};
