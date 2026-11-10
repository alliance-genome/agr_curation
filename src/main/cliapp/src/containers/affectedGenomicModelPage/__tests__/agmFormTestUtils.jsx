import { act } from '@testing-library/react';
import { useAGMReducer } from '../useAGMReducer';
import { renderWithClient } from '../../../tools/jest/utils';

/**
 * Renders an AGM detail page subform against the real AGM reducer, loaded with `agm` the way the
 * detail page loads it. Tests read the reducer state back to see what a curator's edit will save.
 *
 * @param {Function} Form - the subform component, taking `state` and `dispatch`
 * @param {object} agm - the AGM to load, holding the subform's rows
 * @returns {object} the render result, plus `getState()` for the current reducer state and
 *   `dispatch(action)` to send the reducer an action from the test
 */
export const renderAGMForm = (Form, agm) => {
	const handle = {};
	const Harness = () => {
		const { agmState, agmDispatch } = useAGMReducer();
		handle.state = agmState;
		handle.dispatch = agmDispatch;
		return <Form state={agmState} dispatch={agmDispatch} />;
	};

	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: agm }));

	return {
		...result,
		getState: () => handle.state,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};
