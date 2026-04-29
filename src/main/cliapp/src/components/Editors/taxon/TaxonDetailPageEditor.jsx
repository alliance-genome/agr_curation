import React from 'react';
import { AutocompleteFormEditor } from '../autocomplete/base/AutocompleteFormEditor';
import { taxonSearch } from './utils';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { TaxonAdditionalFieldData } from '../../FieldData/TaxonAdditionalFieldData';
import { DetailPageFieldWrapper } from '../../DetailPageFieldWrapper';

export const TaxonDetailPageEditor = ({
	taxon,
	onTaxonValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
}) => {
	return (
		<>
			<DetailPageFieldWrapper
				labelColumnSize={labelColumnSize}
				fieldDetailsColumnSize={fieldDetailsColumnSize}
				widgetColumnSize={widgetColumnSize}
				fieldName="Taxon"
				formField={
					<AutocompleteFormEditor
						name="taxon-input"
						search={taxonSearch}
						initialValue={taxon}
						fieldName="taxon"
						onValueChangeHandler={onTaxonValueChange}
					/>
				}
				errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={'taxon'} />}
				additionalDataField={<TaxonAdditionalFieldData curie={taxon?.curie} name={taxon?.name} />}
			/>
		</>
	);
};
