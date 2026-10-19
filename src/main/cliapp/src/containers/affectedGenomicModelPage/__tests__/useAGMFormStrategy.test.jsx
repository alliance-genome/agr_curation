import { renderHook } from '@testing-library/react';
import { useAGMFormStrategy } from '../useAGMFormStrategy';

const ENTITY_TYPE = 'agmSecondaryIds';

const ROWS = [
	{ dataKey: 'k7', secondaryId: 'first' },
	{ dataKey: 'k3', secondaryId: 'second' },
];

const setUp = (state, dispatch = vi.fn()) =>
	renderHook(() => useAGMFormStrategy({ state, dispatch, entityType: ENTITY_TYPE }));

describe('useAGMFormStrategy', () => {
	it('reads rows from state.agm[entityType] and errors from its entity state', () => {
		const errors = { k7: { secondaryId: { severity: 'error', message: 'boom' } } };
		const { result } = setUp({
			agm: { [ENTITY_TYPE]: ROWS },
			entityStates: { [ENTITY_TYPE]: { errorMessages: errors } },
		});

		expect(result.current.readRow({ rowData: { dataKey: 'k7', secondaryId: 'STALE' } })).toEqual(ROWS[0]);
		expect(result.current.errorsAt('k7', 'secondaryId')).toEqual([errors.k7.secondaryId]);
	});

	it('survives state that has no rows or entity state for the entity type yet', () => {
		const rowData = { dataKey: 'k7' };
		const { result } = setUp({ agm: {}, entityStates: {} });

		expect(result.current.readRow({ rowData })).toBe(rowData);
		expect(result.current.errorsAt('k7', 'secondaryId')).toEqual([undefined]);
	});

	it('dispatches a dataKey-addressed EDIT_ROW_BY_KEY', () => {
		const dispatch = vi.fn();
		const { result } = setUp({ agm: { [ENTITY_TYPE]: ROWS }, entityStates: {} }, dispatch);

		result.current.write({ rowKey: 'k3', field: 'secondaryId', value: 'edited' });

		expect(dispatch).toHaveBeenCalledWith({
			type: 'EDIT_ROW_BY_KEY',
			entityType: ENTITY_TYPE,
			dataKey: 'k3',
			field: 'secondaryId',
			value: 'edited',
		});
	});
});
