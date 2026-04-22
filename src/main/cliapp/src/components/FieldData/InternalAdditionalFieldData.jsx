import React from 'react';

export const InternalAdditionalFieldData = ({ internal }) => {
	if (!internal) return null;
	return <div className="p-info">{internal}</div>;
};
