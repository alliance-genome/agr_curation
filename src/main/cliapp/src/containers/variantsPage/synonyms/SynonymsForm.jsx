import { StringListInput } from '../../../components/Editors/widgets/StringListInput';
import { FormField } from '../../../components/Editors/fields/FormField';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';

/**
 * A variant's synonyms, edited as one comma-separated field. They are plain strings on the
 * variant, not the slot annotations an allele or construct carries, so there is no per-synonym
 * row to edit.
 */
export const SynonymsForm = ({ state, dispatch, widgetColumnSize, labelColumnSize, fieldDetailsColumnSize }) => {
	const synonyms = state.variant?.synonyms;

	const onSynonymsValueChange = (value) => {
		dispatch({
			type: 'EDIT',
			field: 'synonyms',
			value,
		});
	};

	return (
		<FormField
			label="Synonyms"
			name="synonyms"
			value={synonyms}
			onChange={onSynonymsValueChange}
			errors={state.errorMessages}
			labelColumnSize={labelColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			additionalDataField={<StringListTemplate list={synonyms} />}
		>
			{(binding) => <StringListInput {...binding} id="synonyms" multiline={true} rows={3} />}
		</FormField>
	);
};
