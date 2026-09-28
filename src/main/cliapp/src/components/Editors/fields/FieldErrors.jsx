import { Message } from 'primereact/message';

/**
 * Renders a field's validation messages. Falsy entries are ignored, and nothing
 * is rendered when no messages remain.
 *
 * @param {Array<{severity: string, message: string}|null|undefined>} [messages] - messages to render
 * @param {'overlay'|'inline'|'text'} [layout='overlay'] - 'overlay' positions absolutely so table
 *   rows do not reflow, 'inline' sits in normal flow, 'text' renders bare `<small>` elements
 * @returns {JSX.Element|null}
 */
export function FieldErrors({ messages, layout = 'overlay' }) {
	const present = (messages ?? []).filter(Boolean);
	if (present.length === 0) return null;

	if (layout === 'text') {
		return present.map((entry, index) => (
			<small key={index} className="text-lg p-error">
				{entry.message}
			</small>
		));
	}

	const isOverlay = layout === 'overlay';
	const wrapperProps = isOverlay
		? { className: 'pt-1 absolute' }
		: { style: { position: 'inline-table', paddingTop: '5px' } };
	const messageStyle = isOverlay
		? { position: 'absolute', display: 'inline-table', zIndex: '5' }
		: { position: 'inline-table', zIndex: '5' };

	return (
		<div {...wrapperProps}>
			{present.map((entry, index) => (
				<Message key={index} severity={entry.severity} text={entry.message} style={messageStyle} />
			))}
		</div>
	);
}
