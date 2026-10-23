import { StringListInput } from '../../../components/Editors/widgets/StringListInput';
import { FormErrorMessageComponent } from '../../../components/Error/FormErrorMessageComponent';
import { DetailPageFieldWrapper } from '../../../components/DetailPageFieldWrapper';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';

/**
 * A transgenic tool's secondary IDs, edited as one comma-separated field. They are plain strings on the
 * tool, not the slot annotations an allele or AGM carries, so there is no per-ID row to edit.
 */
export const SecondaryIdsForm = ({ state, dispatch, widgetColumnSize, labelColumnSize, fieldDetailsColumnSize }) => {
	const secondaryIdentifiers = state.transgenicTool?.secondaryIdentifiers;

	return (
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldName="Secondary IDs"
			formField={
				<StringListInput
					id="secondaryIdentifiers"
					name="secondaryIdentifiers"
					value={secondaryIdentifiers}
					onChange={(value) => dispatch({ type: 'EDIT', field: 'secondaryIdentifiers', value })}
					multiline={true}
					rows={3}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={state.errorMessages} errorField={'secondaryIdentifiers'} />}
			additionalDataField={<StringListTemplate list={secondaryIdentifiers} />}
		/>
	);
};
