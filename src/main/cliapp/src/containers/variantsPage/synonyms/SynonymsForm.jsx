import { StringListInput } from '../../../components/Editors/widgets/StringListInput';
import { FormErrorMessageComponent } from '../../../components/Error/FormErrorMessageComponent';
import { DetailPageFieldWrapper } from '../../../components/DetailPageFieldWrapper';
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
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldName="Synonyms"
			formField={
				<StringListInput
					id="synonyms"
					name="synonyms"
					value={synonyms}
					onChange={onSynonymsValueChange}
					multiline={true}
					rows={3}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={state.errorMessages} errorField={'synonyms'} />}
			additionalDataField={<StringListTemplate list={synonyms} />}
		/>
	);
};
