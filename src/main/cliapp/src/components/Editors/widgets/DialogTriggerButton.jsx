import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../EditMessageTooltip';

/**
 * Underlined button that opens an editing dialog. The label falls back through
 * `displayHtml`, `displayText`, then `addText`.
 *
 * @param {(event: Event) => void} onOpen - called with the click event
 * @param {string} [displayText] - label when a value exists
 * @param {string} [displayHtml] - label rendered as markup; takes precedence over displayText
 * @param {string} [addText] - label when there is no value
 * @param {string} [tooltipObject] - entity name for the edit-message tooltip
 * @returns {JSX.Element}
 */
export function DialogTriggerButton({ onOpen, displayText, displayHtml, addText, tooltipObject }) {
	const content = displayHtml ? (
		<div className="overflow-hidden text-overflow-ellipsis" dangerouslySetInnerHTML={{ __html: displayHtml }} />
	) : (
		displayText || addText
	);

	return (
		<div>
			<Button className="p-button-text" onClick={onOpen}>
				<span style={{ textDecoration: 'underline' }}>
					{content}
					<i className="pi pi-user-edit" style={{ fontSize: '1em' }}></i>
				</span>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<EditMessageTooltip object={tooltipObject} />
			</Button>
		</div>
	);
}
