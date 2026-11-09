import { FormField } from '../../fields/FormField';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { variantTypeSearch } from './utils';
import { OntologyTermAdditionalFieldData } from '../../../FieldData/OntologyTermAdditionalFieldData';

/**
 * A detail page's variant type field: an SO term autosuggest.
 *
 * @param {object|null} variantType - the variant's SO term, or `{curie}` holding text typed while no term
 *   is picked
 * @param {(value: object|null) => void} onChange - called with the picked term, `{curie: <text>}` for
 *   typed text, or null when the input is emptied
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field, read under `variantType`
 * @param {boolean} [required=false] - marks the field as required
 * @returns {JSX.Element}
 */
export const VariantTypeDetailPageEditor = ({
	variantType,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	required = false,
}) => (
	<FormField
		label="Variant Type"
		name="variantType"
		value={variantType}
		onChange={onChange}
		errors={errorMessages}
		required={required}
		labelColumnSize={labelColumnSize}
		widgetColumnSize={widgetColumnSize}
		fieldDetailsColumnSize={fieldDetailsColumnSize}
		additionalDataField={<OntologyTermAdditionalFieldData curie={variantType?.curie} name={variantType?.name} />}
	>
		{(binding) => <SingleAutocomplete {...binding} search={variantTypeSearch} />}
	</FormField>
);
