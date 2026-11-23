import { FormField } from '../../fields/FormField';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { sourceGeneralConsequenceSearch } from './utils';
import { OntologyTermAdditionalFieldData } from '../../../FieldData/OntologyTermAdditionalFieldData';

/**
 * A detail page's source general consequence field: an SO term autosuggest.
 *
 * @param {object|null} sourceGeneralConsequence - the variant's SO term, or `{curie}` holding text typed
 *   while no term is picked
 * @param {(value: object|null) => void} onChange - called with the picked term, `{curie: <text>}` for
 *   typed text, or null when the input is emptied
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field, read under `sourceGeneralConsequence`
 * @returns {JSX.Element}
 */
export const SourceGeneralConsequenceDetailPageEditor = ({
	sourceGeneralConsequence,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
}) => (
	<FormField
		label="Source General Consequence"
		name="sourceGeneralConsequence"
		value={sourceGeneralConsequence}
		onChange={onChange}
		errors={errorMessages}
		labelColumnSize={labelColumnSize}
		widgetColumnSize={widgetColumnSize}
		fieldDetailsColumnSize={fieldDetailsColumnSize}
		additionalDataField={
			<OntologyTermAdditionalFieldData curie={sourceGeneralConsequence?.curie} name={sourceGeneralConsequence?.name} />
		}
	>
		{(binding) => <SingleAutocomplete {...binding} search={sourceGeneralConsequenceSearch} />}
	</FormField>
);
