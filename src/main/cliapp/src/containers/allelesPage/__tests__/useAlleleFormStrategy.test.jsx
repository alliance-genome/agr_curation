import { renderHook } from '@testing-library/react';
import { useAlleleFormStrategy } from '../useAlleleFormStrategy';

const ENTITY_TYPE = 'alleleMutationTypes';

const ROWS = [
	{ dataKey: 'k7', name: 'first' },
	{ dataKey: 'k3', name: 'second' },
];

const makeState = (rows = ROWS, errorMessages = {}) => ({
	allele: { [ENTITY_TYPE]: rows },
	entityStates: { [ENTITY_TYPE]: { errorMessages } },
});

const setUp = (state = makeState(), dispatch = vi.fn()) =>
	renderHook(() => useAlleleFormStrategy({ state, dispatch, entityType: ENTITY_TYPE }));

/**
 * This is the only place that knows useAlleleReducer's state shape and action
 * names. useDataKeyRowsStrategy, which it wraps, knows neither.
 */
describe('useAlleleFormStrategy', () => {
	it('reads rows from state.allele[entityType]', () => {
		const { result } = setUp();

		expect(result.current.readRow({ rowData: { dataKey: 'k7', name: 'STALE' } })).toEqual({
			dataKey: 'k7',
			name: 'first',
		});
	});

	it('reads errors from state.entityStates[entityType].errorMessages', () => {
		const errors = { k7: { name: { severity: 'error', message: 'boom' } } };
		const { result } = setUp(makeState(ROWS, errors));

		expect(result.current.errorsAt('k7', 'name')).toEqual([{ severity: 'error', message: 'boom' }]);
	});

	// Rows are addressed by dataKey: ADD_ROW unshifts, so a row's index changes after
	// its editor mounts.
	it('dispatches a dataKey-addressed EDIT_ROW_BY_KEY', () => {
		const dispatch = vi.fn();
		const { result } = setUp(makeState(), dispatch);

		result.current.write({ rowKey: 'k3', field: 'name', value: 'edited' });

		expect(dispatch).toHaveBeenCalledWith({
			type: 'EDIT_ROW_BY_KEY',
			entityType: ENTITY_TYPE,
			dataKey: 'k3',
			field: 'name',
			value: 'edited',
		});
	});

	it('renders errors inline, in normal flow', () => {
		const { result } = setUp();

		expect(result.current.errorLayout).toBe('inline');
	});

	// An entity type is read before SET has populated it on first render.
	it('survives state that has no rows or entity state for the entity type yet', () => {
		const rowData = { dataKey: 'k7' };
		const { result } = setUp({ allele: {}, entityStates: {} });

		expect(result.current.readRow({ rowData })).toBe(rowData);
		expect(result.current.errorsAt('k7', 'name')).toEqual([undefined]);
	});
});
