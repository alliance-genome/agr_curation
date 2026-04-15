import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../EditMessageTooltip';
import { ErrorMessageComponent } from '../Error/ErrorMessageComponent';

export const ConditionRelationsTableEditor = ({ editorOptions, errorMessagesRef, onOpenInEdit, isHandle }) => {
	if (isHandle) return null;

	const conditionRelations = editorOptions.rowData?.conditionRelations;

	return (
		<>
			<div>
				<Button
					className="p-button-text"
					onClick={(event) => onOpenInEdit(event, editorOptions, true)}
				>
					<span style={{ textDecoration: 'underline' }}>
						{conditionRelations?.length > 0
							? `Conditions (${conditionRelations.length})`
							: 'Add Condition'}
						<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
					</span>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<EditMessageTooltip />
				</Button>
			</div>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
				errorField={'conditionRelations'}
				style={{ fontSize: '1em' }}
			/>
		</>
	);
};
