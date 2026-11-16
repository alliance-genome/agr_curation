import { FormField } from '../../fields/FormField';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { taxonSearch } from './utils';
import { TaxonAdditionalFieldData } from '../../../FieldData/TaxonAdditionalFieldData';

/**
 * A detail page taxon autosuggest.
 *
 * @param {object|null} taxon - the selected taxon, or `{curie}` holding text typed while none is picked
 * @param {(value: object|null) => void} onChange - called with the picked taxon, `{curie: <text>}`
 *   for typed text, or null when the input is emptied
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field, read under `taxon`
 * @param {boolean} [required=false] - marks the field as required
 * @param {boolean} [isPending=false] - marks the field as having pending edits
 * @returns {JSX.Element}
 */
export const TaxonDetailPageEditor = ({
	taxon,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	required = false,
	isPending = false,
}) => (
	<FormField
		label="Taxon"
		name="taxon"
		value={taxon}
		onChange={onChange}
		errors={errorMessages}
		required={required}
		isPending={isPending}
		labelColumnSize={labelColumnSize}
		widgetColumnSize={widgetColumnSize}
		fieldDetailsColumnSize={fieldDetailsColumnSize}
		additionalDataField={<TaxonAdditionalFieldData curie={taxon?.curie} name={taxon?.name} />}
	>
		{(binding) => <SingleAutocomplete {...binding} search={taxonSearch} />}
	</FormField>
);
