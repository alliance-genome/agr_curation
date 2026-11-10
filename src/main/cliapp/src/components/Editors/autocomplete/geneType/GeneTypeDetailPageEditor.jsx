import React from 'react';
import { AutocompleteFormEditor } from '../base/AutocompleteFormEditor';
import { geneTypeSearch } from './utils';
import { FormErrorMessageComponent } from '../../../Error/FormErrorMessageComponent';
import { OntologyTermAdditionalFieldData } from '../../../FieldData/OntologyTermAdditionalFieldData';
import { DetailPageFieldWrapper } from '../../../DetailPageFieldWrapper';

/**
 * A detail page's gene type field: an SO term autosuggest.
 *
 * @param {Object} props
 * @param {Object} [props.geneType] - the gene's SO term, or the text typed while no term is picked
 * @param {Function} props.onGeneTypeValueChange - called with the autosuggest's change event
 * @param {string} props.widgetColumnSize
 * @param {string} props.labelColumnSize
 * @param {string} props.fieldDetailsColumnSize
 * @param {Object} [props.errorMessages] - the page's field errors, read under `geneType`
 * @param {boolean} [props.required] - whether to mark the field as required
 * @param {boolean} [props.isPending] - whether to mark the field as having pending edits
 */
export const GeneTypeDetailPageEditor = ({
	geneType,
	onGeneTypeValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	required = false,
	isPending = false,
}) => {
	return (
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			isPending={isPending}
			fieldName="Gene Type"
			required={required}
			formField={
				<AutocompleteFormEditor
					name="geneType-input"
					search={geneTypeSearch}
					initialValue={geneType}
					fieldName="geneType"
					onValueChangeHandler={onGeneTypeValueChange}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={'geneType'} />}
			additionalDataField={<OntologyTermAdditionalFieldData curie={geneType?.curie} name={geneType?.name} />}
		/>
	);
};
