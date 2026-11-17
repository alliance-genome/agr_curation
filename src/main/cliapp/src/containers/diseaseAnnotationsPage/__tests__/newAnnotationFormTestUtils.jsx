import { act } from '@testing-library/react';
import { useNewAnnotationReducer } from '../useNewAnnotationReducer';
import { renderWithClient } from '../../../tools/jest/utils';

/**
 * Renders a new annotation subform against the real new annotation reducer, so tests can read back
 * what the dialog will save.
 *
 * @param {(state: object, dispatch: Function) => JSX.Element} renderSubform - renders the subform
 *   from the reducer state and dispatch
 * @returns {object} the render result, plus `getState()` for the current reducer state and
 *   `dispatch(action)` to send the reducer an action from the test
 */
export const renderNewAnnotationSubform = (renderSubform) => {
	const handle = {};
	const Harness = () => {
		const { newAnnotationState, newAnnotationDispatch } = useNewAnnotationReducer();
		handle.state = newAnnotationState;
		handle.dispatch = newAnnotationDispatch;
		return renderSubform(newAnnotationState, newAnnotationDispatch);
	};

	const result = renderWithClient(<Harness />);
	return {
		...result,
		getState: () => handle.state,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};
