import { TableField } from '../fields/TableField';
import { DialogTriggerButton } from '../widgets/DialogTriggerButton';

/**
 * Cell that opens an edit dialog rather than editing in place, with the validation
 * message for the field the dialog owns. Stores nothing itself.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {(event: object, editorOptions: object, isInEdit: boolean) => void} onOpenInEdit -
 *   opens the dialog for this row
 * @param {string} errorField - the row property the dialog edits, and the key its errors
 *   are filed under
 * @param {string} [displayText] - label for the trigger; falls back to `addText`
 * @param {string} [displayHtml] - label rendered as HTML, taking precedence over `displayText`
 * @param {string} [addText] - label shown when the field is empty
 * @param {string} [tooltipObject] - entity name for the edit-message tooltip
 * @returns {JSX.Element}
 */
export const DialogTriggerTableEditor = ({
	editorOptions,
	onOpenInEdit,
	errorField,
	displayText,
	displayHtml,
	addText,
	tooltipObject,
}) => (
	<TableField editorOptions={editorOptions} field={errorField}>
		<DialogTriggerButton
			onOpen={(event) => onOpenInEdit(event, editorOptions, true)}
			displayText={displayText}
			displayHtml={displayHtml}
			addText={addText}
			tooltipObject={tooltipObject}
		/>
	</TableField>
);
