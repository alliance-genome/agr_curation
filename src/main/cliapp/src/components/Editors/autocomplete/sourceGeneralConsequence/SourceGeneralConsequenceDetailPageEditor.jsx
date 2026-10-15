import React from 'react';
import { AutocompleteFormEditor } from '../base/AutocompleteFormEditor';
import { sourceGeneralConsequenceSearch } from './utils';
import { FormErrorMessageComponent } from '../../../Error/FormErrorMessageComponent';
import { OntologyTermAdditionalFieldData } from '../../../FieldData/OntologyTermAdditionalFieldData';
import { DetailPageFieldWrapper } from '../../../DetailPageFieldWrapper';

export const SourceGeneralConsequenceDetailPageEditor = ({
	sourceGeneralConsequence,
	onSourceGeneralConsequenceValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
}) => {
	return (
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldName="Source General Consequence"
			formField={
				<AutocompleteFormEditor
					name="sourceGeneralConsequence-input"
					search={sourceGeneralConsequenceSearch}
					initialValue={sourceGeneralConsequence}
					fieldName="sourceGeneralConsequence"
					onValueChangeHandler={onSourceGeneralConsequenceValueChange}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={'sourceGeneralConsequence'} />}
			additionalDataField={
				<OntologyTermAdditionalFieldData
					curie={sourceGeneralConsequence?.curie}
					name={sourceGeneralConsequence?.name}
				/>
			}
		/>
	);
};
