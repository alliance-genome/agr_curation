import { ConditionRelationHandleDropdown } from '../ConditionRelationHandleSelector';
import { ErrorMessageComponent } from '../Error/ErrorMessageComponent';

export const ConditionRelationHandleTableEditor = ({ editorOptions, errorMessagesRef }) => {
	if (!editorOptions.rowData?.conditionRelations || !editorOptions.rowData.conditionRelations[0]?.handle) {
		return null;
	}

	const onValueChange = (editorOptions, event) => {
		const conditionRelations = [...editorOptions.rowData.conditionRelations];
		if (typeof event.value === 'object') {
			conditionRelations[0] = event.value;
		} else {
			conditionRelations[0] = { ...conditionRelations[0], handle: event.value };
		}
		editorOptions.editorCallback(conditionRelations);
	};

	return (
		<>
			<ConditionRelationHandleDropdown
				field="conditionRelationHandle"
				editorChange={onValueChange}
				editorOptions={editorOptions}
				showClear={false}
				placeholderText={editorOptions.rowData.conditionRelations[0].handle}
			/>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
				errorField={'conditionRelationHandle'}
			/>
		</>
	);
};
