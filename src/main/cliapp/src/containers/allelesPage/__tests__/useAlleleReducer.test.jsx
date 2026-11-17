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

		it('Collapses a single-object section and drops its editing row', () => {
			const { result } = renderHook(() => useAlleleReducer());

			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_OBJECT',
					entityType: 'alleleFullName',
					value: { dataKey: 'name-1', displayText: 'a name' },
				})
			);
			expect(result.current.alleleState.entityStates.alleleFullName.show).toBe(true);
			expect(result.current.alleleState.entityStates.alleleFullName.editingRows).toEqual({ 'name-1': true });

			act(() => result.current.alleleDispatch({ type: 'RESET' }));

			expect(result.current.alleleState.allele.alleleFullName).toBeNull();
			expect(result.current.alleleState.entityStates.alleleFullName.show).toBe(false);
			expect(result.current.alleleState.entityStates.alleleFullName.editingRows).toEqual({});
		});

		it('Clears both page level and section level error messages', () => {
			const { result } = renderHook(() => useAlleleReducer());

			act(() => result.current.alleleDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { taxon: 'Required' } }));
			act(() =>
				result.current.alleleDispatch({
					type: 'UPDATE_TABLE_ERROR_MESSAGES',
					entityType: 'alleleFullName',
					errorMessages: { 'name-1': { displayText: 'Required' } },
				})
			);

			act(() => result.current.alleleDispatch({ type: 'RESET' }));

			expect(result.current.alleleState.errorMessages).toEqual({});
			expect(result.current.alleleState.entityStates.alleleFullName.errorMessages).toEqual({});
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
					type: 'ADD_OBJECT',
					entityType: 'alleleFullName',
					value: { dataKey: 'name-2', displayText: 'another' },
				})
			);

			expect(result.current.alleleState.allele.primaryExternalId).toBeUndefined();
			expect(result.current.alleleState.allele.alleleFullName.displayText).toEqual('another');
		});
	});

	describe('SET', () => {
		it('Holds none of the lists written through their own sub-resources', () => {
			const { result } = renderHook(() => useAlleleReducer());

			act(() =>
				result.current.alleleDispatch({
					type: 'SET',
					value: { id: 9, taxon: { curie: 'NCBITaxon:10090' }, alleleFullName: { displayText: 'a name' } },
				})
			);

			const { allele, entityStates } = result.current.alleleState;
			expect(allele.alleleFullName.dataKey).toBeDefined();
			expect(entityStates.alleleFullName.show).toBe(true);
			[
				'alleleSynonyms',
				'alleleSecondaryIds',
				'alleleNomenclatureEvents',
				'alleleMutationTypes',
				'alleleFunctionalImpacts',
				'alleleInheritanceModes',
				'relatedNotes',
			].forEach((field) => {
				expect(allele).not.toHaveProperty(field);
				expect(entityStates).not.toHaveProperty(field);
			});
		});
	});
});
