import { render, fireEvent, waitFor } from '@testing-library/react';
import { VocabularySelect } from '../VocabularySelect';
import { pickOption } from './widgetTestUtils';

const TERMS = [
	{ id: 1, name: 'exact' },
	{ id: 2, name: 'broad' },
];

describe('<VocabularySelect />', () => {
	it('renders the current term', () => {
		const { container } = render(<VocabularySelect value={TERMS[0]} onChange={vi.fn()} options={TERMS} />);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('exact');
	});

	// Vocabulary fields store the whole term, because the API expects {id, name}.
	// Unwrapping to a scalar is BooleanSelect's job, via optionValue.
	it('emits the whole term object, not its name', () => {
		const onChange = vi.fn();
		const { container } = render(<VocabularySelect value={null} onChange={onChange} options={TERMS} />);

		pickOption(container, 'broad');

		expect(onChange).toHaveBeenCalledWith({ id: 2, name: 'broad' });
	});

	it('renders a clear affordance when showClear is set', () => {
		const { container } = render(<VocabularySelect value={TERMS[0]} onChange={vi.fn()} options={TERMS} showClear />);

		expect(container.querySelector('.p-dropdown-clear-icon')).toBeInTheDocument();
	});

	// PrimeReact's clear icon binds onPointerUp, not onClick, and clearing hands back
	// an undefined value. Emitting null instead keeps "explicitly cleared" distinct
	// from "absent" in the saved payload.
	it('emits null when cleared', () => {
		const onChange = vi.fn();
		const { container } = render(<VocabularySelect value={TERMS[0]} onChange={onChange} options={TERMS} showClear />);

		fireEvent.pointerUp(container.querySelector('.p-dropdown-clear-icon'));

		expect(onChange).toHaveBeenCalledWith(null);
	});

	// The `options || EMPTY_OPTIONS` fallback is load-bearing: PrimeReact reads
	// props.options unguarded on open and throws on undefined. That throw surfaces as
	// an unhandled error rather than a failed assertion, so assert on it directly.
	it('opens with no options supplied, as when a vocabulary has not loaded', () => {
		const onError = vi.fn();
		window.addEventListener('error', onError);
		try {
			const { container } = render(<VocabularySelect value={null} onChange={vi.fn()} />);

			fireEvent.click(container.querySelector('.p-dropdown'));

			expect(onError).not.toHaveBeenCalled();
			expect(document.querySelectorAll('.p-dropdown-item')).toHaveLength(0);
		} finally {
			window.removeEventListener('error', onError);
		}
	});

	it('shows the placeholder when nothing is selected', () => {
		const { container } = render(
			<VocabularySelect value={null} onChange={vi.fn()} options={TERMS} placeholder="pick one" />
		);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('pick one');
	});

	it('emits the optionValue property instead of the term when optionValue is set', () => {
		const onChange = vi.fn();
		const { container } = render(
			<VocabularySelect value={null} onChange={onChange} options={TERMS} optionValue="name" />
		);

		pickOption(container, 'broad');

		expect(onChange).toHaveBeenCalledWith('broad');
	});

	it('selects a bare value against its option when optionValue is set', () => {
		const { container } = render(
			<VocabularySelect value="broad" onChange={vi.fn()} options={TERMS} optionValue="name" />
		);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('broad');
	});

	// PrimeReact fires onShow after the overlay's enter transition, not synchronously
	// on click, hence the waitFor.
	it('calls onShow when the panel opens, so options can be loaded on demand', async () => {
		const onShow = vi.fn();
		const { container } = render(<VocabularySelect value={null} onChange={vi.fn()} options={TERMS} onShow={onShow} />);

		expect(onShow).not.toHaveBeenCalled();

		fireEvent.click(container.querySelector('.p-dropdown'));

		await waitFor(() => expect(onShow).toHaveBeenCalledTimes(1));
	});

	it('applies invalid styling', () => {
		const { container } = render(<VocabularySelect value={null} onChange={vi.fn()} options={TERMS} invalid />);

		expect(container.querySelector('.p-dropdown')).toHaveClass('p-invalid');
	});

	it('honours a non-default optionLabel', () => {
		const options = [{ id: 1, abbreviation: 'MGI' }];
		const { container } = render(
			<VocabularySelect value={options[0]} onChange={vi.fn()} options={options} optionLabel="abbreviation" />
		);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('MGI');
	});

	// The value differs from its option in a NON-key field on purpose: PrimeReact
	// falls back to deep equality without a dataKey, so a value that merely differs
	// by object identity would match anyway and the assertion would prove nothing.
	it('matches a value to its option by dataKey alone', () => {
		const { container } = render(
			<VocabularySelect value={{ id: 2, name: 'STALE LABEL' }} onChange={vi.fn()} options={TERMS} dataKey="id" />
		);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('broad');
	});

	// `id` reaches the focusable input as PrimeReact's inputId; a plain `id` would
	// land on the root div instead and break `<label htmlFor>` association.
	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(
			<VocabularySelect id="variantStatus" value={null} onChange={vi.fn()} options={TERMS} />
		);

		expect(container.querySelector('input#variantStatus')).toBeInTheDocument();
	});

	// `name` is the accessible name only. A DOM name attribute on a column called
	// Name or Prefix invites the browser to autofill a saved profile over the cell.
	it('exposes name as the accessible name without emitting a name attribute', () => {
		const { container, getByLabelText } = render(
			<VocabularySelect value={TERMS[0]} onChange={vi.fn()} options={TERMS} name="variantStatus" />
		);

		expect(getByLabelText('variantStatus')).toBeInTheDocument();
		expect(container.querySelector('[name]')).toBeNull();
	});
});
