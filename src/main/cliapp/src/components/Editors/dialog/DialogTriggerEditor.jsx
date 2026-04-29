import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../EditMessageTooltip';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const DialogTriggerEditor = ({
	editorOptions,
	errorMessagesRef,
	onOpenInEdit,
	errorField,
	displayText,
	displayHtml,
	addText,
	tooltipObject,
}) => {
	const content = displayHtml ? (
		<div className="overflow-hidden text-overflow-ellipsis" dangerouslySetInnerHTML={{ __html: displayHtml }} />
	) : (
		displayText || addText
	);

	return (
		<>
			<div>
				<Button className="p-button-text" onClick={(event) => onOpenInEdit(event, editorOptions, true)}>
					<span style={{ textDecoration: 'underline' }}>
						{content}
						<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
					</span>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<EditMessageTooltip object={tooltipObject} />
				</Button>
			</div>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
				errorField={errorField}
				style={{ fontSize: '1em' }}
			/>
		</>
	);
};
