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

	describe('hasOtherPendingEdits', () => {
		const loadedAllele = { id: 9, taxon: { curie: 'NCBITaxon:10090' }, alleleSynonyms: [] };

		const renderLoaded = () => {
			const hook = renderHook(() => useAlleleReducer());
			act(() => hook.result.current.alleleDispatch({ type: 'SET', value: loadedAllele }));
			return hook;
		};

		it('Starts unset on a loaded allele', () => {
			const { result } = renderLoaded();

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(false);
		});

		it('Stays unset for an edit to a single-value field', () => {
			const { result } = renderLoaded();

			act(() => result.current.alleleDispatch({ type: 'EDIT', field: 'obsolete', value: true }));

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(false);
		});

		it('Is set by an edit to any other field', () => {
			const { result } = renderLoaded();

			act(() => result.current.alleleDispatch({ type: 'EDIT', field: 'primaryExternalId', value: 'WB:WBVar1' }));

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(true);
		});

		it('Is set by an edit to a row by its key', () => {
			const { result } = renderHook(() => useAlleleReducer());
			act(() =>
				result.current.alleleDispatch({
					type: 'SET',
					value: { ...loadedAllele, alleleSynonyms: [{ displayText: 'a' }] },
				})
			);
			const [{ dataKey }] = result.current.alleleState.allele.alleleSynonyms;
			expect(dataKey).toBeDefined();

			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_ROW_BY_KEY',
					entityType: 'alleleSynonyms',
					dataKey,
					field: 'displayText',
					value: 'b',
				})
			);

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(true);
		});

		it('Stays unset for an edit to a key no row has', () => {
			const { result } = renderLoaded();

			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_ROW_BY_KEY',
					entityType: 'alleleSynonyms',
					dataKey: 'gone',
					field: 'displayText',
					value: 'b',
				})
			);

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(false);
		});

		it('Is set by a table edit', () => {
			const { result } = renderLoaded();

			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_ROW',
					entityType: 'alleleSynonyms',
					row: { dataKey: 'row-1', displayText: 'a synonym' },
				})
			);

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(true);
		});

		it('Stays unset for edits to a single-object section', () => {
			const { result } = renderLoaded();

			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_OBJECT',
					entityType: 'alleleFullName',
					value: { dataKey: 'name-1', displayText: '' },
				})
			);
			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_OBJECT',
					entityType: 'alleleFullName',
					field: 'displayText',
					value: 'a name',
				})
			);
			act(() => result.current.alleleDispatch({ type: 'DELETE_OBJECT', entityType: 'alleleFullName' }));

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(false);
		});

		it('Is cleared when the allele is set again', () => {
			const { result } = renderLoaded();

			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_ROW',
					entityType: 'alleleSynonyms',
					row: { dataKey: 'row-1', displayText: 'a synonym' },
				})
			);
			act(() => result.current.alleleDispatch({ type: 'SET', value: loadedAllele }));

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(false);
		});

		it('Is cleared by a reset', () => {
			const { result } = renderLoaded();

			act(() => result.current.alleleDispatch({ type: 'EDIT', field: 'primaryExternalId', value: 'WB:WBVar1' }));
			act(() => result.current.alleleDispatch({ type: 'RESET' }));

			expect(result.current.alleleState.hasOtherPendingEdits).toBe(false);
		});
	});

	describe('EDIT_ROW, EDIT_ROW_BY_KEY, ADD_ROW and DELETE_ROW', () => {
		// The keys differ from the array positions, so a test that confuses the two fails.
		const renderWithSynonyms = () => {
			const hook = renderHook(() => useAlleleReducer());
			act(() =>
				hook.result.current.alleleDispatch({
					type: 'EDIT',
					field: 'alleleSynonyms',
					value: [
						{ dataKey: 'k7', displayText: 'first' },
						{ dataKey: 'k3', displayText: 'second' },
					],
				})
			);
			return hook;
		};

		it('Edits a row by its position', () => {
			const { result } = renderWithSynonyms();

			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_ROW',
					entityType: 'alleleSynonyms',
					index: 1,
					field: 'displayText',
					value: 'edited',
				})
			);

			expect(result.current.alleleState.allele.alleleSynonyms.map((row) => row.displayText)).toEqual([
				'first',
				'edited',
			]);
		});

		it('Edits a row by its key', () => {
			const { result } = renderWithSynonyms();

			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_ROW_BY_KEY',
					entityType: 'alleleSynonyms',
					dataKey: 'k3',
					field: 'displayText',
					value: 'edited',
				})
			);

			expect(result.current.alleleState.allele.alleleSynonyms.map((row) => row.displayText)).toEqual([
				'first',
				'edited',
			]);
		});

		it('Ignores an edit to a key no row has', () => {
			const { result } = renderWithSynonyms();

			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_ROW_BY_KEY',
					entityType: 'alleleSynonyms',
					dataKey: 'gone',
					field: 'displayText',
					value: 'edited',
				})
			);

			expect(result.current.alleleState.allele.alleleSynonyms.map((row) => row.displayText)).toEqual([
				'first',
				'second',
			]);
		});

		it('Adds a row at the top, open for editing, and shows the table', () => {
			const { result } = renderWithSynonyms();

			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_ROW',
					entityType: 'alleleSynonyms',
					row: { dataKey: 'k9', displayText: 'new' },
				})
			);

			const entityState = result.current.alleleState.entityStates.alleleSynonyms;
			expect(result.current.alleleState.allele.alleleSynonyms.map((row) => row.dataKey)).toEqual(['k9', 'k7', 'k3']);
			expect(entityState.editingRows).toMatchObject({ k9: true });
			expect(entityState.show).toBe(true);
		});

		it('Deletes only the row with the given key', () => {
			const { result } = renderWithSynonyms();

			act(() => result.current.alleleDispatch({ type: 'DELETE_ROW', entityType: 'alleleSynonyms', dataKey: 'k3' }));

			expect(result.current.alleleState.allele.alleleSynonyms.map((row) => row.dataKey)).toEqual(['k7']);
		});

		it('Hides the table once its last row is deleted', () => {
			const { result } = renderWithSynonyms();
			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_ROW',
					entityType: 'alleleSynonyms',
					row: { dataKey: 'k9', displayText: 'new' },
				})
			);

			act(() => result.current.alleleDispatch({ type: 'DELETE_ROW', entityType: 'alleleSynonyms', dataKey: 'k9' }));
			act(() => result.current.alleleDispatch({ type: 'DELETE_ROW', entityType: 'alleleSynonyms', dataKey: 'k7' }));
			expect(result.current.alleleState.entityStates.alleleSynonyms.show).toBe(true);

			act(() => result.current.alleleDispatch({ type: 'DELETE_ROW', entityType: 'alleleSynonyms', dataKey: 'k3' }));
			expect(result.current.alleleState.entityStates.alleleSynonyms.show).toBe(false);
		});
	});

	describe('ADD_OBJECT, EDIT_OBJECT and DELETE_OBJECT', () => {
		const addFullName = (result) =>
			act(() =>
				result.current.alleleDispatch({
					type: 'ADD_OBJECT',
					entityType: 'alleleFullName',
					value: { dataKey: 'fn1', displayText: 'a name' },
				})
			);

		it('Adds the object open for editing and shows it', () => {
			const { result } = renderHook(() => useAlleleReducer());

			addFullName(result);

			const entityState = result.current.alleleState.entityStates.alleleFullName;
			expect(result.current.alleleState.allele.alleleFullName).toEqual({ dataKey: 'fn1', displayText: 'a name' });
			expect(entityState.editingRows).toMatchObject({ fn1: true });
			expect(entityState.show).toBe(true);
		});

		it('Edits one field of the object', () => {
			const { result } = renderHook(() => useAlleleReducer());
			addFullName(result);

			act(() =>
				result.current.alleleDispatch({
					type: 'EDIT_OBJECT',
					entityType: 'alleleFullName',
					field: 'displayText',
					value: 'edited',
				})
			);

			expect(result.current.alleleState.allele.alleleFullName.displayText).toBe('edited');
		});

		it('Deletes the object and hides it', () => {
			const { result } = renderHook(() => useAlleleReducer());
			addFullName(result);

			act(() => result.current.alleleDispatch({ type: 'DELETE_OBJECT', entityType: 'alleleFullName' }));

			expect(result.current.alleleState.allele.alleleFullName).toBeNull();
			expect(result.current.alleleState.entityStates.alleleFullName.show).toBe(false);
		});
	});

	describe('UPDATE_TABLE_ERROR_MESSAGES and SUBMIT', () => {
		it('Stores table error messages for one table only', () => {
			const { result } = renderHook(() => useAlleleReducer());
			const errorMessages = { k7: { evidence: { severity: 'error', message: 'Not a valid entry' } } };

			act(() =>
				result.current.alleleDispatch({
					type: 'UPDATE_TABLE_ERROR_MESSAGES',
					entityType: 'alleleSynonyms',
					errorMessages,
				})
			);

			expect(result.current.alleleState.entityStates.alleleSynonyms.errorMessages).toEqual(errorMessages);
			expect(result.current.alleleState.entityStates.alleleMutationTypes.errorMessages).toEqual({});
		});

		it('Clears page level and every table error map on submit', () => {
			const { result } = renderHook(() => useAlleleReducer());
			act(() => result.current.alleleDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { taxon: 'Required' } }));
			act(() =>
				result.current.alleleDispatch({
					type: 'UPDATE_TABLE_ERROR_MESSAGES',
					entityType: 'alleleSynonyms',
					errorMessages: { k7: { evidence: { severity: 'error', message: 'boom' } } },
				})
			);

			act(() => result.current.alleleDispatch({ type: 'SUBMIT' }));

			expect(result.current.alleleState.submitted).toBe(true);
			expect(result.current.alleleState.errorMessages).toEqual({});
			Object.values(result.current.alleleState.entityStates).forEach((entityState) =>
				expect(entityState.errorMessages).toEqual({})
			);
		});
	});

	it('Throws on an unknown action', () => {
		// React reports the error thrown during render before rethrowing it.
		const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
		const { result } = renderHook(() => useAlleleReducer());

		try {
			expect(() => act(() => result.current.alleleDispatch({ type: 'NOT_A_REAL_ACTION' }))).toThrow(
				'Unknown action: NOT_A_REAL_ACTION'
			);
		} finally {
			consoleError.mockRestore();
		}
	});
});
