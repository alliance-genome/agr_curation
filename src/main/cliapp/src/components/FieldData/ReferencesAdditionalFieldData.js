import React from 'react';
import { SingleReferenceAdditionalFieldData } from "./SingleReferenceAdditionalFieldData";

export const ReferencesAdditionalFieldData = ({ references }) => {

	const referencesDivs = references?.map((reference) => {
		return (
			<div key={reference.curie}>
				<SingleReferenceAdditionalFieldData key={reference.curie} fieldData={reference} />
			</div>
		)
	})

	return referencesDivs || null;
};
