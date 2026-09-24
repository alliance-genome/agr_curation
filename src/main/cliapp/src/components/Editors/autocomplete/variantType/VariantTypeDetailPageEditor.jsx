import React from 'react';
import { AutocompleteFormEditor } from '../base/AutocompleteFormEditor';
import { variantTypeSearch } from './utils';
import { FormErrorMessageComponent } from '../../../Error/FormErrorMessageComponent';
import { OntologyTermAdditionalFieldData } from '../../../FieldData/OntologyTermAdditionalFieldData';
import { DetailPageFieldWrapper } from '../../../DetailPageFieldWrapper';

export const VariantTypeDetailPageEditor = ({
	variantType,
	onVariantTypeValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	required = false,
}) => {
	return (
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldName="Variant Type"
			required={required}
			formField={
				<AutocompleteFormEditor
					name="variantType-input"
					search={variantTypeSearch}
					initialValue={variantType}
					fieldName="variantType"
					onValueChangeHandler={onVariantTypeValueChange}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={'variantType'} />}
			additionalDataField={
				<OntologyTermAdditionalFieldData curie={variantType?.curie} name={variantType?.name} />
			}
		/>
	);
};
