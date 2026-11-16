import { render, fireEvent, renderHook } from '@testing-library/react';
import { TableField } from '../TableField';
import { TableEditorProvider } from '../TableEditorContext';
import { TextInput } from '../../widgets/TextInput';
import { usePrimeRowEditStrategy } from '../strategies/usePrimeRowEditStrategy';
import { useDataKeyRowsStrategy } from '../strategies/useDataKeyRowsStrategy';
import { useLocalRowsStrategy } from '../strategies/useLocalRowsStrategy';

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

describe('useDataKeyRowsStrategy', () => {
	const ROWS = [
		{ dataKey: 'k7', name: 'first' },
		{ dataKey: 'k3', name: 'second' },
	];

	const setUp = ({ rows = ROWS, errors = {}, write = vi.fn(), errorLayout } = {}) =>
		renderHook(() => useDataKeyRowsStrategy({ rows, errors, write, errorLayout }));

	it('addresses rows by dataKey', () => {
		const { result } = setUp();

		expect(result.current.keyOf(makeEditorOptions({ dataKey: 'k3' }, 1))).toBe('k3');
	});

	// The reason readRow exists: these tables keep every row in edit mode, so
	// PrimeReact never refreshes the rowData it captured when the editor mounted.
	it('reads the row from the supplied rows, not from the captured editorOptions.rowData', () => {
		const { result } = setUp();
		const staleRowData = { dataKey: 'k7', name: 'STALE' };

		expect(result.current.readRow(makeEditorOptions(staleRowData))).toEqual({ dataKey: 'k7', name: 'first' });
	});

	it('reads editorOptions.rowData for a row with no dataKey', () => {
		const { result } = setUp({ rows: [{ name: 'unkeyed' }] });
		const rowData = { name: 'own' };

		expect(result.current.readRow(makeEditorOptions(rowData))).toBe(rowData);
	});

	it('falls back to editorOptions.rowData when the dataKey is not among the rows', () => {
		const { result } = setUp();
		const orphan = { dataKey: 'gone', name: 'orphan' };

		expect(result.current.readRow(makeEditorOptions(orphan))).toBe(orphan);
	});

	it('reports the error for a row and field, and nothing for a row without one', () => {
		const { result } = setUp({ errors: { k7: { name: { severity: 'error', message: 'boom' } } } });

		expect(result.current.errorsAt('k7', 'name')).toEqual([{ severity: 'error', message: 'boom' }]);
		expect(result.current.errorsAt('k3', 'name')).toEqual([undefined]);
	});

	it('renders errors inline, in normal flow, by default', () => {
		const { result } = setUp();

		expect(result.current.errorLayout).toBe('inline');
	});

	it('takes an errorLayout override', () => {
		const { result } = setUp({ errorLayout: 'overlay' });

		expect(result.current.errorLayout).toBe('overlay');
	});

	it('drives a TableField end to end without touching editorCallback', () => {
		const write = vi.fn();
		const editorOptions = makeEditorOptions({ dataKey: 'k7', name: 'STALE' });

		const Harness = () => {
			const strategy = useDataKeyRowsStrategy({
				rows: ROWS,
				errors: { k7: { name: { severity: 'error', message: 'boom' } } },
				write,
			});
			return (
				<TableEditorProvider strategy={strategy}>
					<TableField editorOptions={editorOptions} field="name">
						{(binding) => <TextInput {...binding} />}
					</TableField>
				</TableEditorProvider>
			);
		};

		const { container, getByText } = render(<Harness />);

		// value came from the supplied rows, not from the stale captured rowData
		expect(container.querySelector('input')).toHaveValue('first');
		expect(getByText('boom')).toBeInTheDocument();

		fireEvent.change(container.querySelector('input'), { target: { value: 'typed' } });

		expect(write).toHaveBeenCalledWith(expect.objectContaining({ rowKey: 'k7', field: 'name', value: 'typed' }));
		expect(editorOptions.editorCallback).not.toHaveBeenCalled();
	});
});

describe('useLocalRowsStrategy', () => {
	const LOCAL_ROWS = [
		{ dataKey: 'k7', name: 'first' },
		{ dataKey: 'k3', name: 'second' },
	];

	const renderStrategy = (props = {}) => {
		const setRows = vi.fn();
		const { result } = renderHook(() => useLocalRowsStrategy({ rows: LOCAL_ROWS, setRows, ...props }));
		return { strategy: result.current, setRows };
	};

	/** Runs the updater the strategy handed to setRows, against `previous`. */
	const applyWrite = (setRows, previous) => setRows.mock.calls[0][0](previous);

	it('stores through the setter, changing only the addressed row', () => {
		const { strategy, setRows } = renderStrategy();

		strategy.write({ rowKey: 'k3', field: 'name', value: 'edited' });

		expect(applyWrite(setRows, LOCAL_ROWS)).toEqual([
			{ dataKey: 'k7', name: 'first' },
			{ dataKey: 'k3', name: 'edited' },
		]);
	});

	// The updater form matters: it is applied to whatever rows are current when React
	// runs it, so a write captured from an earlier render still stores correctly.
	it('writes against the rows current at apply time, not at capture time', () => {
		const { strategy, setRows } = renderStrategy();

		strategy.write({ rowKey: 'k3', field: 'name', value: 'edited' });

		expect(applyWrite(setRows, [{ dataKey: 'k3', name: 'moved and renamed' }])).toEqual([
			{ dataKey: 'k3', name: 'edited' },
		]);
	});

	// These dialogs initialise their rows to null and reset them to [] on hide, so the
	// updater can outlive the rows it was built for.
	it('stores an empty list rather than throwing when there are no rows to write to', () => {
		const { strategy, setRows } = renderStrategy({ rows: null });

		strategy.write({ rowKey: 'k3', field: 'name', value: 'edited' });

		expect(applyWrite(setRows, null)).toEqual([]);
		expect(applyWrite(setRows, [])).toEqual([]);
	});

	it('reads rows and errors by dataKey, and lays errors out inline by default', () => {
		const errors = { k3: { name: { severity: 'error', message: 'bad' } } };
		const { strategy } = renderStrategy({ errors });

		expect(strategy.keyOf(makeEditorOptions({ dataKey: 'k3' }))).toBe('k3');
		expect(strategy.readRow(makeEditorOptions({ dataKey: 'k3', name: 'STALE' }))).toEqual(LOCAL_ROWS[1]);
		expect(strategy.errorsAt('k3', 'name')).toEqual([{ severity: 'error', message: 'bad' }]);
		expect(strategy.errorLayout).toBe('inline');
	});
});
