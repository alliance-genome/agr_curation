import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../EditMessageTooltip';
import { ErrorMessageComponent } from '../Error/ErrorMessageComponent';

export const RelatedNotesTableEditor = ({ editorOptions, errorMessagesRef, onOpenInEdit }) => {
	const relatedNotes = editorOptions?.rowData?.relatedNotes;

	return (
		<>
			<div>
				<Button
					className="p-button-text"
					onClick={(event) => onOpenInEdit(event, editorOptions, true)}
				>
					<span style={{ textDecoration: 'underline' }}>
						{relatedNotes?.length > 0
							? `Notes(${relatedNotes.length}) `
							: 'Add Note'}
						<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
					</span>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<EditMessageTooltip />
				</Button>
			</div>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
				errorField={'relatedNotes'}
				style={{ fontSize: '1em' }}
			/>
		</>
	);
};
