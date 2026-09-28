import { render, renderHook } from '@testing-library/react';
import { TableField } from '../TableField';
import { TableEditorProvider } from '../TableEditorContext';
import { TextInput } from '../../widgets/TextInput';
import { usePrimeRowEditStrategy } from '../strategies/usePrimeRowEditStrategy';

const makeEditorOptions = (rowData, rowIndex = 0) => ({ rowData, rowIndex, editorCallback: vi.fn() });

describe('usePrimeRowEditStrategy', () => {
	it('addresses rows by their index on the page', () => {
		const { result } = renderHook(() => usePrimeRowEditStrategy({ errorMessages: {} }));

		expect(result.current.keyOf(makeEditorOptions({}, 4))).toBe(4);
	});

	it('reads the row from editorOptions and stores through editorCallback', () => {
		const { result } = renderHook(() => usePrimeRowEditStrategy({ errorMessages: {} }));
		const editorOptions = makeEditorOptions({ name: 'from rowData' });

		expect(result.current.readRow(editorOptions)).toBe(editorOptions.rowData);

		result.current.write({ editorOptions, rowKey: 0, field: 'name', value: 'typed' });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith('typed');
	});

	it('renders errors as an overlay so table rows do not reflow', () => {
		const { result } = renderHook(() => usePrimeRowEditStrategy({ errorMessages: {} }));

		expect(result.current.errorLayout).toBe('overlay');
	});

	// Composed with a real TableField rather than a hand-rolled stand-in: a fake that
	// mirrors the hook is exactly where the two drift apart unnoticed.
	describe('composed with TableField', () => {
		const renderWithStrategy = ({ editorOptions, errorMessages, uiErrorMessages }) => {
			const Harness = () => {
				const strategy = usePrimeRowEditStrategy({ errorMessages, uiErrorMessages });
				return (
					<TableEditorProvider strategy={strategy}>
						<TableField editorOptions={editorOptions} field="name">
							{(binding) => <TextInput {...binding} />}
						</TableField>
					</TableEditorProvider>
				);
			};
			return render(<Harness />);
		};

		// The client-side channel Disease Annotations uses for "Must select from
		// autosuggest". Without this the uiErrorMessages passthrough is unpinned.
		it('renders a client-side validation error alongside the server one', () => {
			const editorOptions = { ...makeEditorOptions({ name: 'x' }, 2), field: 'name' };
			const { getByText } = renderWithStrategy({
				editorOptions,
				errorMessages: { 2: { name: { severity: 'error', message: 'server says no' } } },
				uiErrorMessages: { 2: { name: { severity: 'error', message: 'Must select from autosuggest' } } },
			});

			expect(getByText('server says no')).toBeInTheDocument();
			expect(getByText('Must select from autosuggest')).toBeInTheDocument();
		});
	});
});
