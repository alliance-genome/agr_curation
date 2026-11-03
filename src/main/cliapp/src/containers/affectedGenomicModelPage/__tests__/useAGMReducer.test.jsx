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

	describe('SET', () => {
		const storedAgm = {
			agmFullName: { displayText: 'wild isolate 1' },
			agmSecondaryIds: [{ secondaryId: 'WB:WBStrain00000001' }],
		};

		it('Shows the sections of the lists and objects the AGM holds', () => {
			const { result } = renderHook(() => useAGMReducer());

			act(() => result.current.agmDispatch({ type: 'SET', value: storedAgm }));

			const { entityStates, agm } = result.current.agmState;
			expect(entityStates.agmSecondaryIds.show).toBe(true);
			expect(entityStates.agmSecondaryIds.editingRows).toEqual({ [agm.agmSecondaryIds[0].dataKey]: true });
			expect(entityStates.agmFullName.show).toBe(true);
		});

		it('Closes the sections of the lists and objects a later AGM lacks', () => {
			const { result } = renderHook(() => useAGMReducer());
			act(() => result.current.agmDispatch({ type: 'SET', value: storedAgm }));

			act(() => result.current.agmDispatch({ type: 'SET', value: { taxon: { curie: 'NCBITaxon:6239' } } }));

			const { entityStates, agm } = result.current.agmState;
			expect(agm.agmSecondaryIds).toEqual([]);
			expect(entityStates.agmSecondaryIds.show).toBe(false);
			expect(entityStates.agmSecondaryIds.editingRows).toEqual({});
			expect(entityStates.agmFullName.show).toBe(false);
			expect(entityStates.agmFullName.editingRows).toEqual({});
		});
	});
});
