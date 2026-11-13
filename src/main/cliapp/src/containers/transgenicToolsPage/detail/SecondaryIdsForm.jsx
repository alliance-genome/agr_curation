import { StringListInput } from '../../../components/Editors/widgets/StringListInput';
import { FormField } from '../../../components/Editors/fields/FormField';
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
		<FormField
			label="Secondary IDs"
			name="secondaryIdentifiers"
			value={secondaryIdentifiers}
			onChange={(value) => dispatch({ type: 'EDIT', field: 'secondaryIdentifiers', value })}
			errors={state.errorMessages}
			labelColumnSize={labelColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			additionalDataField={<StringListTemplate list={secondaryIdentifiers} />}
		>
			{(binding) => <StringListInput {...binding} id="secondaryIdentifiers" multiline={true} rows={3} />}
		</FormField>
	);
};
