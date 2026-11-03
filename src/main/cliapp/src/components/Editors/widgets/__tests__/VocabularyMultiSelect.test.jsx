import { render, fireEvent } from '@testing-library/react';
import { VocabularyMultiSelect } from '../VocabularyMultiSelect';
import { pickOption } from './widgetTestUtils';

const TERMS = [
	{ id: 1, name: 'alpha' },
	{ id: 2, name: 'beta' },
];

const chipLabels = (container) =>
	[...container.querySelectorAll('.p-multiselect-token-label')].map((node) => node.textContent);

describe('<VocabularyMultiSelect />', () => {
	it('renders a chip per selected term', () => {
		const { container } = render(<VocabularyMultiSelect value={TERMS} onChange={vi.fn()} options={TERMS} />);

		expect(chipLabels(container)).toEqual(['alpha', 'beta']);
	});

	it('renders no chips for a null value', () => {
		const { container } = render(<VocabularyMultiSelect value={null} onChange={vi.fn()} options={TERMS} />);

		expect(chipLabels(container)).toEqual([]);
	});

	it('emits an array of term objects', () => {
		const onChange = vi.fn();
		const { container } = render(<VocabularyMultiSelect value={[]} onChange={onChange} options={TERMS} />);

		pickOption(container, 'beta', { multi: true });

		expect(onChange).toHaveBeenCalledWith([{ id: 2, name: 'beta' }]);
	});

	it('emits an empty array when the last selection is removed', () => {
		const onChange = vi.fn();
		const { container } = render(<VocabularyMultiSelect value={[TERMS[1]]} onChange={onChange} options={TERMS} />);

		pickOption(container, 'beta', { multi: true });

		expect(onChange).toHaveBeenCalledWith([]);
	});

	// The `options || EMPTY_OPTIONS` fallback is load-bearing: with a value present,
	// PrimeReact searches the option list on open and throws on undefined. That throw
	// surfaces as an unhandled error rather than a failed assertion, so assert on it
	// directly. Reachable whenever a row already has terms and the vocabulary is still
	// loading.
	it('opens with a value but no options supplied, as when a vocabulary has not loaded', () => {
		const onError = vi.fn();
		window.addEventListener('error', onError);
		try {
			const { container } = render(<VocabularyMultiSelect value={[TERMS[0]]} onChange={vi.fn()} />);

			fireEvent.click(container.querySelector('.p-multiselect'));

			expect(onError).not.toHaveBeenCalled();
		} finally {
			window.removeEventListener('error', onError);
		}
	});

	it('honours a non-default optionLabel', () => {
		const options = [{ id: 1, abbreviation: 'MGI' }];
		const { container } = render(
			<VocabularyMultiSelect value={options} onChange={vi.fn()} options={options} optionLabel="abbreviation" />
		);

		expect(chipLabels(container)).toEqual(['MGI']);
	});

	// The value and the option are separate objects here, so only dataKey can match
	// them; PrimeReact's deep-equality fallback would fail on the differing label.
	it('matches values to options by dataKey alone', () => {
		const { container } = render(
			<VocabularyMultiSelect value={[{ id: 2, name: 'STALE LABEL' }]} onChange={vi.fn()} options={TERMS} dataKey="id" />
		);

		expect(chipLabels(container)).toEqual(['beta']);
	});

	it('shows the placeholder when nothing is selected', () => {
		const { container } = render(
			<VocabularyMultiSelect value={[]} onChange={vi.fn()} options={TERMS} placeholder="alpha, beta" />
		);

		expect(container.querySelector('.p-multiselect-label')).toHaveTextContent('alpha, beta');
	});

	it('applies invalid styling', () => {
		const { container } = render(<VocabularyMultiSelect value={[]} onChange={vi.fn()} options={TERMS} invalid />);

		expect(container.querySelector('.p-multiselect')).toHaveClass('p-invalid');
	});

	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(
			<VocabularyMultiSelect id="diseaseQualifiers" value={[]} onChange={vi.fn()} options={TERMS} />
		);

		expect(container.querySelector('input#diseaseQualifiers')).toBeInTheDocument();
	});

	// PrimeReact puts aria-label on the root element as well as the focusable input,
	// so this asserts the input rather than querying by accessible name.
	it('exposes name on the focusable input without emitting a name attribute', () => {
		const { container } = render(
			<VocabularyMultiSelect value={[]} onChange={vi.fn()} options={TERMS} name="diseaseQualifiers" />
		);

		expect(container.querySelector('input[aria-label="diseaseQualifiers"]')).toBeInTheDocument();
		expect(container.querySelector('[name]')).toBeNull();
	});
});
