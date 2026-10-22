import { renderHook, act } from '@testing-library/react';
import { useAGMReducer } from '../useAGMReducer';

describe('useAGMReducer', () => {
	describe('EDIT_ROW_BY_KEY', () => {
		// The keys differ from the array positions, so a test that confuses the two fails.
		const renderWithSynonyms = () => {
			const hook = renderHook(() => useAGMReducer());
			act(() =>
				hook.result.current.agmDispatch({
					type: 'EDIT',
					field: 'agmSynonyms',
					value: [
						{ dataKey: 'k7', displayText: 'first' },
						{ dataKey: 'k3', displayText: 'second' },
					],
				})
			);
			return hook;
		};

		it('Edits a row by its key', () => {
			const { result } = renderWithSynonyms();

			act(() =>
				result.current.agmDispatch({
					type: 'EDIT_ROW_BY_KEY',
					entityType: 'agmSynonyms',
					dataKey: 'k3',
					field: 'displayText',
					value: 'edited',
				})
			);

			expect(result.current.agmState.agm.agmSynonyms.map((row) => row.displayText)).toEqual(['first', 'edited']);
		});

		it('Ignores an edit to a key no row has', () => {
			const { result } = renderWithSynonyms();

			act(() =>
				result.current.agmDispatch({
					type: 'EDIT_ROW_BY_KEY',
					entityType: 'agmSynonyms',
					dataKey: 'gone',
					field: 'displayText',
					value: 'edited',
				})
			);

			expect(result.current.agmState.agm.agmSynonyms.map((row) => row.displayText)).toEqual(['first', 'second']);
		});
	});
});
