import { renderHook, act } from '@testing-library/react';
import { useAlleleReducer } from '../useAlleleReducer';

describe('useAlleleReducer', () => {
	describe('RESET', () => {
		it('Clears field values', () => {
			const { result } = renderHook(() => useAlleleReducer());

			act(() => result.current.alleleDispatch({ type: 'EDIT', field: 'primaryExternalId', value: 'WB:WBVar1' }));
			expect(result.current.alleleState.allele.primaryExternalId).toEqual('WB:WBVar1');

			act(() => result.current.alleleDispatch({ type: 'RESET' }));
			expect(result.current.alleleState.allele.primaryExternalId).toBeUndefined();
		});

		it('Collapses a sub-table and drops its editing rows', () => {
			const { result } = renderHook(() => useAlleleReducer());

			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_ROW',
					entityType: 'alleleSynonyms',
					row: { dataKey: 'row-1', displayText: 'a synonym' },
				})
			);
			expect(result.current.alleleState.entityStates.alleleSynonyms.show).toBe(true);
			expect(result.current.alleleState.entityStates.alleleSynonyms.editingRows).toEqual({ 'row-1': true });

			act(() => result.current.alleleDispatch({ type: 'RESET' }));

			expect(result.current.alleleState.allele.alleleSynonyms).toEqual([]);
			expect(result.current.alleleState.entityStates.alleleSynonyms.show).toBe(false);
			expect(result.current.alleleState.entityStates.alleleSynonyms.editingRows).toEqual({});
		});

		it('Clears both page level and table level error messages', () => {
			const { result } = renderHook(() => useAlleleReducer());

			act(() => result.current.alleleDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { taxon: 'Required' } }));
			act(() =>
				result.current.alleleDispatch({
					type: 'UPDATE_TABLE_ERROR_MESSAGES',
					entityType: 'alleleSynonyms',
					errorMessages: { 'row-1': { displayText: 'Required' } },
				})
			);

			act(() => result.current.alleleDispatch({ type: 'RESET' }));

			expect(result.current.alleleState.errorMessages).toEqual({});
			expect(result.current.alleleState.entityStates.alleleSynonyms.errorMessages).toEqual({});
			expect(result.current.alleleState.submitted).toBe(false);
		});

		it('Leaves a second reset with a clean allele to edit', () => {
			// Resetting more than once, and editing in between, stays writable.
			const { result } = renderHook(() => useAlleleReducer());

			act(() => result.current.alleleDispatch({ type: 'RESET' }));
			act(() => result.current.alleleDispatch({ type: 'EDIT', field: 'primaryExternalId', value: 'WB:WBVar2' }));
			act(() => result.current.alleleDispatch({ type: 'RESET' }));
			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_ROW',
					entityType: 'alleleSynonyms',
					row: { dataKey: 'row-2', displayText: 'another' },
				})
			);

			expect(result.current.alleleState.allele.primaryExternalId).toBeUndefined();
			expect(result.current.alleleState.allele.alleleSynonyms).toHaveLength(1);
		});
	});
});
