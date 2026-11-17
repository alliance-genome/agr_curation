import { renderHook, act } from '@testing-library/react';
import { useState } from 'react';
import { useSubResourceRows } from '../useSubResourceRows';

const marked = { 'row-1': { curie: { severity: 'error', message: 'refused' } } };

const renderRows = (options) =>
	renderHook(() => {
		const [rows, setRows] = useState([{ dataKey: 'row-1' }, { dataKey: 'row-2' }]);
		const [errorMessages, setErrorMessages] = useState(marked);
		return { rows, errorMessages, ...useSubResourceRows({ rows, setRows, setErrorMessages }, options) };
	});

const preventDefault = { preventDefault: () => {} };

describe('useSubResourceRows', () => {
	it('Keeps every row in edit mode', () => {
		const { result } = renderRows();

		expect(result.current.editingRows).toEqual({ 'row-1': true, 'row-2': true });
	});

	it('Adds a row below the others, or above them when asked to', () => {
		const below = renderRows();
		const above = renderRows({ prepend: true });

		act(() => below.result.current.addRow({ dataKey: 'new-row' }));
		act(() => above.result.current.addRow({ dataKey: 'new-row' }));

		expect(below.result.current.rows.map((row) => row.dataKey)).toEqual(['row-1', 'row-2', 'new-row']);
		expect(above.result.current.rows.map((row) => row.dataKey)).toEqual(['new-row', 'row-1', 'row-2']);
	});

	it('Changes only the row it names', () => {
		const { result } = renderRows();

		act(() => result.current.updateRow('row-2', (row) => ({ ...row, curie: 'AGRKB:2' })));

		expect(result.current.rows).toEqual([{ dataKey: 'row-1' }, { dataKey: 'row-2', curie: 'AGRKB:2' }]);
	});

	it('Deletes a row along with the marks on it', () => {
		const { result } = renderRows();

		act(() => result.current.deletionHandler(preventDefault, 'row-1'));

		expect(result.current.rows).toEqual([{ dataKey: 'row-2' }]);
		expect(result.current.errorMessages).toEqual({});
	});
});
