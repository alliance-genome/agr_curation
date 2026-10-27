import { renderHook, act } from '@testing-library/react';
import { FAILURE_HOLD_MS, SAVE_STATUS, SUCCESS_HOLD_MS, useSaveProgress } from '../useSaveProgress';

const ROWS = [
	{ key: 'allele', label: 'Allele' },
	{ key: 'alleleSynonyms', label: 'Synonyms' },
];

const statuses = (result) => result.current.steps.map(({ key, status }) => [key, status]);

describe('useSaveProgress', () => {
	beforeEach(() => {
		vi.useFakeTimers();
	});

	afterEach(() => {
		vi.useRealTimers();
	});

	it('Opens with every row queued', () => {
		const { result } = renderHook(() => useSaveProgress());

		act(() => result.current.begin(ROWS));

		expect(result.current.isOpen).toBe(true);
		expect(statuses(result)).toEqual([
			['allele', SAVE_STATUS.QUEUED],
			['alleleSynonyms', SAVE_STATUS.QUEUED],
		]);
	});

	it('Moves a row through saving to saved or failed, keeping only its status', () => {
		const { result } = renderHook(() => useSaveProgress());
		act(() => result.current.begin(ROWS));

		act(() => result.current.start('allele'));
		expect(statuses(result)[0]).toEqual(['allele', SAVE_STATUS.SAVING]);

		act(() => result.current.settle('allele', { isSuccess: true }));
		act(() => result.current.settle('alleleSynonyms', { isSuccess: false, message: 'Could not update synonyms' }));

		expect(result.current.steps).toEqual([
			{ key: 'allele', label: 'Allele', status: SAVE_STATUS.SAVED },
			{ key: 'alleleSynonyms', label: 'Synonyms', status: SAVE_STATUS.FAILED },
		]);
	});

	it('Counts as saving until every row has settled, but not through the hold', () => {
		const { result } = renderHook(() => useSaveProgress());
		expect(result.current.isSaving).toBe(false);

		act(() => result.current.begin(ROWS));
		expect(result.current.isSaving).toBe(true);

		act(() => result.current.settle('allele', { isSuccess: true }));
		expect(result.current.isSaving).toBe(true);

		act(() => result.current.settle('alleleSynonyms', { isSuccess: false }));
		expect(result.current.isOpen).toBe(true);
		expect(result.current.isSaving).toBe(false);
	});

	it('Marks the rows not yet written as skipped and leaves settled ones alone', () => {
		const { result } = renderHook(() => useSaveProgress());
		act(() => result.current.begin(ROWS));
		act(() => result.current.settle('allele', { isSuccess: false }));

		act(() => result.current.skipRemaining());

		expect(statuses(result)).toEqual([
			['allele', SAVE_STATUS.FAILED],
			['alleleSynonyms', SAVE_STATUS.SKIPPED],
		]);
	});

	it('Closes shortly after a save where everything saved, and resolves true once closed', async () => {
		const { result } = renderHook(() => useSaveProgress());
		const closed = vi.fn();
		act(() => result.current.begin(ROWS));
		act(() => ROWS.forEach(({ key }) => result.current.settle(key, { isSuccess: true })));

		act(() => {
			result.current.finish().then(closed);
		});
		await act(async () => vi.advanceTimersByTime(SUCCESS_HOLD_MS - 1));
		expect(result.current.isOpen).toBe(true);
		expect(closed).not.toHaveBeenCalled();

		await act(async () => vi.advanceTimersByTime(1));
		expect(result.current.isOpen).toBe(false);
		expect(closed).toHaveBeenCalledWith(true);
	});

	it.each([
		['failed', (result) => result.current.settle('alleleSynonyms', { isSuccess: false })],
		['was skipped', (result) => result.current.skipRemaining()],
	])('Stays open longer when a row %s', async (_case, leaveRowUnsaved) => {
		const { result } = renderHook(() => useSaveProgress());
		const closed = vi.fn();
		act(() => result.current.begin(ROWS));
		act(() => result.current.settle('allele', { isSuccess: true }));
		act(() => leaveRowUnsaved(result));

		act(() => {
			result.current.finish().then(closed);
		});
		await act(async () => vi.advanceTimersByTime(FAILURE_HOLD_MS - 1));
		expect(result.current.isOpen).toBe(true);
		expect(closed).not.toHaveBeenCalled();

		await act(async () => vi.advanceTimersByTime(1));
		expect(result.current.isOpen).toBe(false);
		expect(closed).toHaveBeenCalledWith(true);
	});

	it('Marks a failed row saved when it saves on a later try', () => {
		const { result } = renderHook(() => useSaveProgress());
		act(() => result.current.begin(ROWS));
		act(() => result.current.settle('allele', { isSuccess: false }));

		act(() => result.current.settle('allele', { isSuccess: true }));

		expect(result.current.steps[0]).toEqual({ key: 'allele', label: 'Allele', status: SAVE_STATUS.SAVED });
	});

	it('Keeps the overlay open for a save begun before the last one closed, and resolves the earlier finish false', async () => {
		const { result } = renderHook(() => useSaveProgress());
		const earlierClosed = vi.fn();
		act(() => result.current.begin(ROWS));
		act(() => ROWS.forEach(({ key }) => result.current.settle(key, { isSuccess: true })));
		act(() => {
			result.current.finish().then(earlierClosed);
		});

		act(() => result.current.begin([{ key: 'alleleSynonyms', label: 'Synonyms' }]));
		await act(async () => vi.advanceTimersByTime(FAILURE_HOLD_MS));

		expect(earlierClosed).toHaveBeenCalledWith(false);
		expect(result.current.isOpen).toBe(true);
		expect(statuses(result)).toEqual([['alleleSynonyms', SAVE_STATUS.QUEUED]]);
	});

	it('Restarts the hold when finished twice, resolving the first finish false', async () => {
		const { result } = renderHook(() => useSaveProgress());
		const firstClosed = vi.fn();
		const secondClosed = vi.fn();
		act(() => result.current.begin(ROWS));
		act(() => ROWS.forEach(({ key }) => result.current.settle(key, { isSuccess: true })));

		act(() => {
			result.current.finish().then(firstClosed);
		});
		await act(async () => vi.advanceTimersByTime(SUCCESS_HOLD_MS - 1));
		act(() => {
			result.current.finish().then(secondClosed);
		});
		await act(async () => vi.advanceTimersByTime(SUCCESS_HOLD_MS - 1));

		expect(firstClosed).toHaveBeenCalledWith(false);
		expect(result.current.isOpen).toBe(true);

		await act(async () => vi.advanceTimersByTime(1));
		expect(secondClosed).toHaveBeenCalledWith(true);
		expect(result.current.isOpen).toBe(false);
	});

	it('Resolves true at once when finished with the overlay closed', async () => {
		const { result } = renderHook(() => useSaveProgress());
		const closed = vi.fn();

		await act(async () => {
			await result.current.finish().then(closed);
		});

		expect(closed).toHaveBeenCalledWith(true);
	});

	it('Resolves a pending finish false and closes nothing after unmounting', async () => {
		const { result, unmount } = renderHook(() => useSaveProgress());
		const closed = vi.fn();
		act(() => result.current.begin(ROWS));
		act(() => {
			result.current.finish().then(closed);
		});

		unmount();
		await act(async () => vi.advanceTimersByTime(FAILURE_HOLD_MS));

		expect(closed).toHaveBeenCalledTimes(1);
		expect(closed).toHaveBeenCalledWith(false);
		expect(vi.getTimerCount()).toBe(0);
	});

	it('Resolves false at once when finished after unmounting', async () => {
		const { result, unmount } = renderHook(() => useSaveProgress());
		const closed = vi.fn();
		act(() => result.current.begin(ROWS));
		const { finish } = result.current;

		unmount();
		await act(async () => {
			await finish().then(closed);
		});

		expect(closed).toHaveBeenCalledWith(false);
		expect(vi.getTimerCount()).toBe(0);
	});
});
