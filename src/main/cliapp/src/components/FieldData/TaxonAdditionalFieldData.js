import React from "react";

export const TaxonAdditionalFieldData = ({ curie }) => {
	if(!curie) return null;
	return <div className="p-info">{curie}</div>;
}