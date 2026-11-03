import { renderHook, act } from '@testing-library/react';
import { useVariantReducer } from '../useVariantReducer';

describe('useVariantReducer', () => {
	describe('SET', () => {
		it('Loads a variant and opens the sub-tables it carries', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() =>
				result.current.variantDispatch({
					type: 'SET',
					value: {
						curie: 'AGRKB:101000000000001',
						relatedNotes: [{ freeText: 'a note' }],
						references: [{ curie: 'PMID:1' }],
					},
				})
			);

			expect(result.current.variantState.variant.curie).toEqual('AGRKB:101000000000001');
			expect(result.current.variantState.entityStates.relatedNotes.show).toBe(true);
			expect(result.current.variantState.entityStates.references.show).toBe(true);
		});

		it('Gives each sub-table row a dataKey so rows stay identifiable', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() =>
				result.current.variantDispatch({
					type: 'SET',
					value: { relatedNotes: [{ freeText: 'first' }, { freeText: 'second' }] },
				})
			);

			const [first, second] = result.current.variantState.variant.relatedNotes;
			expect(first.dataKey).toBeDefined();
			expect(second.dataKey).toBeDefined();
			expect(first.dataKey).not.toEqual(second.dataKey);
		});

		it('Leaves a variant with no sub-table entries with empty, collapsed tables', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() => result.current.variantDispatch({ type: 'SET', value: { curie: 'AGRKB:101000000000002' } }));

			expect(result.current.variantState.variant.relatedNotes).toEqual([]);
			expect(result.current.variantState.entityStates.relatedNotes.show).toBe(false);
		});

		it('Keeps synonyms as plain strings, the shape the API reads back', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() => result.current.variantDispatch({ type: 'SET', value: { synonyms: ['syn one', 'syn two'] } }));

			expect(result.current.variantState.variant.synonyms).toEqual(['syn one', 'syn two']);
		});
	});

	describe('EDIT', () => {
		it('Replaces a field value', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() => result.current.variantDispatch({ type: 'EDIT', field: 'variantType', value: { curie: 'SO:0000667' } }));

			expect(result.current.variantState.variant.variantType).toEqual({ curie: 'SO:0000667' });
		});

		it('Replaces the whole synonyms list', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() => result.current.variantDispatch({ type: 'EDIT', field: 'synonyms', value: ['only one'] }));

			expect(result.current.variantState.variant.synonyms).toEqual(['only one']);
		});
	});

	describe('ADD_ROW / DELETE_ROW', () => {
		it('Adds a row, shows its table, and marks it for editing', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() =>
				result.current.variantDispatch({
					type: 'ADD_ROW',
					entityType: 'relatedNotes',
					row: { dataKey: 'row-1', freeText: 'a note' },
				})
			);

			expect(result.current.variantState.variant.relatedNotes).toHaveLength(1);
			expect(result.current.variantState.entityStates.relatedNotes.show).toBe(true);
			expect(result.current.variantState.entityStates.relatedNotes.editingRows).toEqual({ 'row-1': true });
		});

		it('Drops a duplicate reference, which the API stores once', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() =>
				result.current.variantDispatch({
					type: 'ADD_ROW',
					entityType: 'references',
					row: { dataKey: 'ref-1', curie: 'PMID:1' },
				})
			);
			act(() =>
				result.current.variantDispatch({
					type: 'ADD_ROW',
					entityType: 'references',
					row: { dataKey: 'ref-2', curie: 'PMID:1' },
				})
			);

			expect(result.current.variantState.variant.references).toHaveLength(1);
		});

		it('Collapses a table once its last row is deleted', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() =>
				result.current.variantDispatch({
					type: 'ADD_ROW',
					entityType: 'relatedNotes',
					row: { dataKey: 'row-1', freeText: 'a note' },
				})
			);
			act(() => result.current.variantDispatch({ type: 'DELETE_ROW', entityType: 'relatedNotes', dataKey: 'row-1' }));

			expect(result.current.variantState.variant.relatedNotes).toEqual([]);
			expect(result.current.variantState.entityStates.relatedNotes.show).toBe(false);
		});
	});

	describe('SUBMIT', () => {
		it('Clears both page level and table level error messages', () => {
			const { result } = renderHook(() => useVariantReducer());

			act(() =>
				result.current.variantDispatch({
					type: 'UPDATE_ERROR_MESSAGES',
					errorMessages: { variantType: 'Required' },
				})
			);
			act(() =>
				result.current.variantDispatch({
					type: 'UPDATE_TABLE_ERROR_MESSAGES',
					entityType: 'relatedNotes',
					errorMessages: { 'row-1': { freeText: 'Required' } },
				})
			);

			act(() => result.current.variantDispatch({ type: 'SUBMIT' }));

			expect(result.current.variantState.submitted).toBe(true);
			expect(result.current.variantState.errorMessages).toEqual({});
			expect(result.current.variantState.entityStates.relatedNotes.errorMessages).toEqual({});
		});
	});
});
