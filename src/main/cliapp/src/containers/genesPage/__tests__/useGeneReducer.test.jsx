import { act, renderHook } from '@testing-library/react';
import { useGeneReducer } from '../useGeneReducer';
import { geneDetailData } from '../mockData/mockData';

const renderReducer = (gene = geneDetailData.entity) => {
	const hook = renderHook(() => useGeneReducer());
	act(() => hook.result.current.geneDispatch({ type: 'SET', value: gene }));
	return hook;
};

describe('useGeneReducer', () => {
	it('opens each single-object section the gene holds for editing', () => {
		const { result } = renderReducer({ ...geneDetailData.entity, geneSystematicName: null });
		const { gene, entityStates } = result.current.geneState;

		['geneSymbol', 'geneFullName', 'gcrpCrossReference'].forEach((field) => {
			expect(gene[field].dataKey).toBeDefined();
			expect(entityStates[field].show).toBe(true);
			expect(entityStates[field].editingRows).toEqual({ [gene[field].dataKey]: true });
		});
		expect(entityStates.geneSystematicName.show).toBe(false);
		expect(entityStates.geneSystematicName.editingRows).toEqual({});
	});

	it('closes a section when a gene without that object is set', () => {
		const { result } = renderReducer();

		act(() =>
			result.current.geneDispatch({ type: 'SET', value: { ...geneDetailData.entity, gcrpCrossReference: null } })
		);

		expect(result.current.geneState.entityStates.gcrpCrossReference.show).toBe(false);
		expect(result.current.geneState.entityStates.gcrpCrossReference.editingRows).toEqual({});
	});

	it('applies updates dispatched together to the object as each leaves it', () => {
		const { result } = renderReducer();

		act(() => {
			result.current.geneDispatch({
				type: 'UPDATE_OBJECT',
				entityType: 'gcrpCrossReference',
				update: (crossReference) => ({ ...crossReference, displayName: 'first' }),
			});
			result.current.geneDispatch({
				type: 'UPDATE_OBJECT',
				entityType: 'gcrpCrossReference',
				update: (crossReference) => ({ ...crossReference, referencedCurie: `${crossReference.displayName}:1` }),
			});
		});

		expect(result.current.geneState.gene.gcrpCrossReference).toEqual(
			expect.objectContaining({ displayName: 'first', referencedCurie: 'first:1' })
		);
	});

	it('leaves a removed section removed when an update for it lands', () => {
		const { result } = renderReducer();

		act(() => {
			result.current.geneDispatch({ type: 'DELETE_OBJECT', entityType: 'gcrpCrossReference' });
			result.current.geneDispatch({
				type: 'UPDATE_OBJECT',
				entityType: 'gcrpCrossReference',
				update: (crossReference) => ({ ...crossReference, displayName: 'late' }),
			});
		});

		expect(result.current.geneState.gene.gcrpCrossReference).toBeNull();
	});

	it('clears the page and section errors when a gene is set', () => {
		const { result } = renderReducer();
		act(() => {
			result.current.geneDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { taxon: 'required' } });
			result.current.geneDispatch({
				type: 'UPDATE_TABLE_ERROR_MESSAGES',
				entityType: 'geneSymbol',
				errorMessages: { row: { displayText: 'required' } },
			});
		});

		act(() => result.current.geneDispatch({ type: 'SET', value: geneDetailData.entity }));

		expect(result.current.geneState.errorMessages).toEqual({});
		expect(result.current.geneState.entityStates.geneSymbol.errorMessages).toEqual({});
	});

	it('adds and removes a single-object section', () => {
		const { result } = renderReducer({ ...geneDetailData.entity, gcrpCrossReference: null });

		act(() =>
			result.current.geneDispatch({
				type: 'ADD_OBJECT',
				entityType: 'gcrpCrossReference',
				value: { dataKey: 'new-row', referencedCurie: '' },
			})
		);
		expect(result.current.geneState.entityStates.gcrpCrossReference.show).toBe(true);
		expect(result.current.geneState.entityStates.gcrpCrossReference.editingRows['new-row']).toBe(true);

		act(() => result.current.geneDispatch({ type: 'DELETE_OBJECT', entityType: 'gcrpCrossReference' }));
		expect(result.current.geneState.gene.gcrpCrossReference).toBeNull();
		expect(result.current.geneState.entityStates.gcrpCrossReference.show).toBe(false);
	});

	it('clears the page and section errors for a save', () => {
		const { result } = renderReducer();
		act(() => {
			result.current.geneDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { taxon: 'required' } });
			result.current.geneDispatch({
				type: 'UPDATE_TABLE_ERROR_MESSAGES',
				entityType: 'geneSymbol',
				errorMessages: { row: { displayText: 'required' } },
			});
		});

		act(() => result.current.geneDispatch({ type: 'SUBMIT' }));

		expect(result.current.geneState.submitted).toBe(true);
		expect(result.current.geneState.errorMessages).toEqual({});
		expect(result.current.geneState.entityStates.geneSymbol.errorMessages).toEqual({});
	});

	describe('create mode', () => {
		const renderCreateReducer = () => renderHook(() => useGeneReducer('create'));

		it('starts from a blank gene with an empty symbol open for editing', () => {
			const { result } = renderCreateReducer();
			const { gene, entityStates } = result.current.geneState;

			expect(gene).toEqual(
				expect.objectContaining({
					taxon: { curie: '' },
					geneType: { curie: '' },
					internal: false,
					obsolete: false,
				})
			);
			expect(gene.geneSymbol.displayText).toBe('');
			expect(entityStates.geneSymbol.show).toBe(true);
			expect(entityStates.geneSymbol.editingRows).toEqual({ [gene.geneSymbol.dataKey]: true });
			expect(entityStates.geneFullName.show).toBe(false);
		});

		it('returns to the blank gene on reset, with a fresh symbol row and no errors', () => {
			const { result } = renderCreateReducer();
			const startingSymbolKey = result.current.geneState.gene.geneSymbol.dataKey;

			act(() => {
				result.current.geneDispatch({
					type: 'EDIT_OBJECT',
					entityType: 'geneSymbol',
					field: 'displayText',
					value: 'abc',
				});
				result.current.geneDispatch({ type: 'EDIT', field: 'taxon', value: { curie: 'NCBITaxon:7227' } });
				result.current.geneDispatch({
					type: 'ADD_OBJECT',
					entityType: 'geneFullName',
					value: { dataKey: 'name-row', displayText: 'a name' },
				});
				result.current.geneDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { taxon: 'required' } });
				result.current.geneDispatch({
					type: 'UPDATE_TABLE_ERROR_MESSAGES',
					entityType: 'geneSymbol',
					errorMessages: { row: { displayText: 'required' } },
				});
			});

			act(() => result.current.geneDispatch({ type: 'RESET' }));
			const { gene, entityStates, errorMessages } = result.current.geneState;

			expect(gene.taxon).toEqual({ curie: '' });
			expect(gene.geneSymbol.displayText).toBe('');
			expect(gene.geneFullName).toBeUndefined();
			expect(gene.geneSymbol.dataKey).not.toBe(startingSymbolKey);
			expect(entityStates.geneSymbol.editingRows).toEqual({ [gene.geneSymbol.dataKey]: true });
			expect(entityStates.geneFullName.show).toBe(false);
			expect(entityStates.geneSymbol.errorMessages).toEqual({});
			expect(errorMessages).toEqual({});
		});
	});
});
