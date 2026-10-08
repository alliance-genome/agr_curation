import { renderHook, act, waitFor } from '@testing-library/react';
import { useSubResourceList } from '../useSubResourceList';

const load = vi.fn();
const replace = vi.fn();
const prepare = (rows) => rows.map((row) => ({ ...row, dataKey: `key-${row.curie}` }));
const strip = ({ curie }) => ({ curie });
const isSame = (rows, otherRows) =>
	JSON.stringify(rows.map((row) => row.curie).sort()) === JSON.stringify(otherRows.map((row) => row.curie).sort());

const options = { label: 'widgets', errorField: 'widgets', load, replace, prepare, strip, isSame };

beforeEach(() => {
	load.mockReset();
	replace.mockReset();
});

describe('useSubResourceList', () => {
	it('Reads the rows through the given load and readies them with prepare', async () => {
		load.mockResolvedValue({ data: { entities: [{ curie: 'W:1', extra: true }] } });

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));

		await waitFor(() => expect(result.current.rows).toHaveLength(1));
		expect(load).toHaveBeenCalledWith(5);
		expect(result.current.rows[0].dataKey).toBe('key-W:1');
		expect(result.current.isDirty).toBe(false);
	});

	it('Judges unsaved edits with the given comparison', async () => {
		load.mockResolvedValue({ data: { entities: [{ curie: 'W:1' }, { curie: 'W:2' }] } });

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));
		await waitFor(() => expect(result.current.rows).toHaveLength(2));

		act(() => {
			result.current.setRows((rows) => [...rows].reverse());
		});
		expect(result.current.isDirty).toBe(false);

		act(() => {
			result.current.setRows((rows) => rows.slice(1));
		});
		expect(result.current.isDirty).toBe(true);
	});

	it('Writes the rows as strip leaves them', async () => {
		load.mockResolvedValue({ data: { entities: [{ curie: 'W:1', extra: true }] } });
		replace.mockResolvedValue({ data: { entities: [{ curie: 'W:1' }] } });

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));

		await act(async () => {
			await result.current.save();
		});

		expect(replace).toHaveBeenCalledWith(5, [{ curie: 'W:1' }]);
	});

	it('Maps the indexed errors under the given field onto their rows', async () => {
		load.mockResolvedValue({ data: { entities: [{ curie: 'W:1' }, { curie: 'W:2' }] } });
		replace.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update widgets',
					supplementalData: { errorMap: { widgets: { 1: { curie: 'Not a valid entry' } } } },
				},
			},
		});

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));
		await waitFor(() => expect(result.current.rows).toHaveLength(2));

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome).toEqual({ isSuccess: false, message: 'Could not update widgets' });
		expect(result.current.errorMessages).toEqual({
			'key-W:2': { curie: { severity: 'error', message: 'Not a valid entry' } },
		});
	});

	it('Takes what the save returns, readied with prepare, as the rows last saved', async () => {
		load.mockResolvedValue({ data: { entities: [{ curie: 'W:1' }] } });
		replace.mockResolvedValue({ data: { entities: [{ curie: 'W:2' }] } });

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));

		act(() => {
			result.current.setRows([{ curie: 'W:2', dataKey: 'new-row' }]);
		});
		expect(result.current.isDirty).toBe(true);

		await act(async () => {
			await result.current.save();
		});

		expect(result.current.rows).toEqual([{ curie: 'W:2', dataKey: 'key-W:2' }]);
		expect(result.current.isDirty).toBe(false);
	});

	it('Writes to the owner it is given, and reads nothing without one', async () => {
		replace.mockResolvedValue({ data: { entities: [] } });

		const { result } = renderHook(() => useSubResourceList(options));
		await act(async () => {
			await result.current.save(9);
		});

		expect(load).not.toHaveBeenCalled();
		expect(replace).toHaveBeenCalledWith(9, []);
	});

	it('Refuses to save rows that are still loading', async () => {
		load.mockReturnValue(new Promise(() => {}));

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));
		expect(result.current.isLoading).toBe(true);

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome.isSuccess).toBe(false);
		expect(replace).not.toHaveBeenCalled();
	});

	it('Names the rows when it refuses to save rows that failed to read', async () => {
		vi.spyOn(console, 'warn').mockImplementation(() => {});
		load.mockRejectedValue(new Error('network'));

		const { result } = renderHook(() => useSubResourceList({ ...options, ownerId: 5 }));
		await waitFor(() => expect(result.current.loadError).toBeTruthy());

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome).toEqual({ isSuccess: false, message: 'These widgets have not been read, so they were not saved' });
		expect(replace).not.toHaveBeenCalled();
		console.warn.mockRestore();
	});
});
