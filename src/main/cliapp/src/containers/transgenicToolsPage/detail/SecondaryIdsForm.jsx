import { StringListInput } from '../../../components/Editors/widgets/StringListInput';
import { FormErrorMessageComponent } from '../../../components/Error/FormErrorMessageComponent';
import { DetailPageFieldWrapper } from '../../../components/DetailPageFieldWrapper';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';

/**
 * A reagent's secondary IDs (a transgenic tool's, a cassette's), edited as one comma-separated field. They are
 * plain strings on the reagent, not the slot annotations an allele or AGM carries, so there is no per-ID row to
 * edit. `entityKey` is the key the page's entity is held under.
 */
export const SecondaryIdsForm = ({
	state,
	dispatch,
	entityKey = 'transgenicTool',
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
}) => {
	const secondaryIdentifiers = state[entityKey]?.secondaryIdentifiers;

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
