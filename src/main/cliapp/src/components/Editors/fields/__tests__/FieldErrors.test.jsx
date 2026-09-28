import { render } from '@testing-library/react';
import { FieldErrors } from '../FieldErrors';
import { normalizeError } from '../normalizeError';

describe('normalizeError', () => {
	it('passes through a {severity, message} object', () => {
		expect(normalizeError({ severity: 'warn', message: 'Pending Edits!' })).toEqual({
			severity: 'warn',
			message: 'Pending Edits!',
		});
	});

	it('defaults a missing severity to error', () => {
		expect(normalizeError({ message: 'boom' })).toEqual({ severity: 'error', message: 'boom' });
	});

	// The detail-page form errors are plain strings — processErrors puts
	// data.errorMessages straight into state. Absorbing that here is what lets one
	// renderer replace all three predecessors.
	it('lifts a bare string into an error object', () => {
		expect(normalizeError('Required field is empty')).toEqual({
			severity: 'error',
			message: 'Required field is empty',
		});
	});

	it.each([null, undefined, ''])('treats %p as no error', (input) => {
		expect(normalizeError(input)).toBeNull();
	});
});

describe('<FieldErrors />', () => {
	const ERROR = { severity: 'error', message: 'Not a valid entry' };

	it('renders nothing when messages is undefined', () => {
		const { container } = render(<FieldErrors />);

		expect(container).toBeEmptyDOMElement();
	});

	// normalizeError returns null for "no error", so an array built from several
	// channels routinely contains nulls. Those must not render or throw. The layout
	// is irrelevant here — FieldErrors bails out before branching on it.
	it('ignores null entries', () => {
		const { container } = render(<FieldErrors messages={[null, undefined]} />);

		expect(container).toBeEmptyDOMElement();
	});

	it('renders only the real messages when nulls are mixed in', () => {
		const { container, getByText } = render(<FieldErrors messages={[null, ERROR, undefined]} />);

		expect(getByText('Not a valid entry')).toBeInTheDocument();
		expect(container.querySelectorAll('.p-inline-message')).toHaveLength(1);
	});

	it('renders every message when more than one applies', () => {
		const { getByText } = render(
			<FieldErrors messages={[ERROR, { severity: 'error', message: 'Must select from autosuggest' }]} />
		);

		expect(getByText('Not a valid entry')).toBeInTheDocument();
		expect(getByText('Must select from autosuggest')).toBeInTheDocument();
	});

	// Overlay must stay absolutely positioned or it reflows table rows, and it is
	// what a strategy that names no layout gets.
	it('defaults to the overlay layout', () => {
		const { container } = render(<FieldErrors messages={[ERROR]} />);

		expect(container.firstChild).toHaveClass('absolute');
	});

	it('positions the overlay layout absolutely', () => {
		const { container } = render(<FieldErrors messages={[ERROR]} layout="overlay" />);

		expect(container.firstChild).toHaveClass('absolute');
	});

	it('positions the inline layout in flow', () => {
		const { container } = render(<FieldErrors messages={[ERROR]} layout="inline" />);

		expect(container.firstChild).not.toHaveClass('absolute');
		expect(container.firstChild.style.position).toBe('inline-table');
	});

	it('renders the text layout as a bare p-error small, with no Message chrome', () => {
		const { container, getByText } = render(<FieldErrors messages={[ERROR]} layout="text" />);

		expect(getByText('Not a valid entry').tagName).toBe('SMALL');
		expect(getByText('Not a valid entry')).toHaveClass('p-error');
		expect(container.querySelector('.p-inline-message')).toBeNull();
	});

	it('carries the severity through to the message', () => {
		const { container } = render(<FieldErrors messages={[{ severity: 'warn', message: 'Pending Edits!' }]} />);

		expect(container.querySelector('.p-inline-message-warn')).toBeInTheDocument();
	});
});
