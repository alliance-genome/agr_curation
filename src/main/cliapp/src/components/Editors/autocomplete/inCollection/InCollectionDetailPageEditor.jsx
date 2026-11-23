import { FormField } from '../../fields/FormField';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { inCollectionSearch } from './utils';
import { InCollectionAdditionalFieldData } from '../../../FieldData/InCollectionAdditionalFieldData';
import { VocabTermAutocompleteTemplate } from '../base/templates/VocabTermAutocompleteTemplate';

/**
 * A detail page in-collection autosuggest over vocabulary terms.
 *
 * @param {object|null} inCollection - the selected term, or `{name}` holding text typed while none is picked
 * @param {(value: object|null) => void} onChange - called with the picked term, `{name: <text>}` for
 *   typed text, or null when the input is emptied
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field, read under `inCollection`
 * @param {boolean} [isPending=false] - marks the field as having pending edits
 * @returns {JSX.Element}
 */
export const InCollectionDetailPageEditor = ({
	inCollection,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	isPending = false,
}) => (
	<FormField
		label="In Collection"
		name="inCollection"
		value={inCollection}
		onChange={onChange}
		errors={errorMessages}
		isPending={isPending}
		labelColumnSize={labelColumnSize}
		widgetColumnSize={widgetColumnSize}
		fieldDetailsColumnSize={fieldDetailsColumnSize}
		additionalDataField={<InCollectionAdditionalFieldData name={inCollection?.name} />}
	>
		{(binding) => (
			<SingleAutocomplete
				{...binding}
				search={inCollectionSearch}
				subField="name"
				valueDisplay={(item, setHoverItem, tooltip, query) => (
					<VocabTermAutocompleteTemplate
						item={item}
						op={tooltip}
						query={query}
						setAutocompleteSelectedItem={setHoverItem}
					/>
				)}
			/>
		)}
	</FormField>
);
