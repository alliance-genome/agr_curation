import { FormField } from '../../fields/FormField';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { geneTypeSearch } from './utils';
import { OntologyTermAdditionalFieldData } from '../../../FieldData/OntologyTermAdditionalFieldData';

/**
 * A detail page's gene type field: an SO term autosuggest.
 *
 * @param {object|null} [geneType] - the gene's SO term, or `{curie}` holding text typed while no term is picked
 * @param {(value: object|null) => void} onChange - called with the picked term, `{curie: <text>}` for
 *   typed text, or null when the input is emptied
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field, read under `geneType`
 * @param {boolean} [required=false] - marks the field as required
 * @param {boolean} [isPending=false] - marks the field as having pending edits
 * @returns {JSX.Element}
 */
export const GeneTypeDetailPageEditor = ({
	geneType,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	required = false,
	isPending = false,
}) => (
	<FormField
		label="Gene Type"
		name="geneType"
		value={geneType}
		onChange={onChange}
		errors={errorMessages}
		required={required}
		isPending={isPending}
		labelColumnSize={labelColumnSize}
		widgetColumnSize={widgetColumnSize}
		fieldDetailsColumnSize={fieldDetailsColumnSize}
		additionalDataField={<OntologyTermAdditionalFieldData curie={geneType?.curie} name={geneType?.name} />}
	>
		{(binding) => <SingleAutocomplete {...binding} search={geneTypeSearch} />}
	</FormField>
);
