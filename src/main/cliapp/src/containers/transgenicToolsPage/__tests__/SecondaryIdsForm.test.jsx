import { act, screen, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { fieldRow } from '../../../tools/jest/detailFieldTestUtils';
import { SecondaryIdsForm } from '../detail/SecondaryIdsForm';
import { useTransgenicToolReducer } from '../detail/useTransgenicToolReducer';

/** Renders the section against the real reducer, loaded with a tool holding one secondary identifier. */
const renderForm = () => {
	const handle = {};
	const Harness = () => {
		const { transgenicToolState, transgenicToolDispatch } = useTransgenicToolReducer();
		handle.dispatch = transgenicToolDispatch;
		return <SecondaryIdsForm state={transgenicToolState} dispatch={transgenicToolDispatch} />;
	};
	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: { id: 1, secondaryIdentifiers: ['FB:FBto9000393'] } }));
	return { ...result, dispatch: (action) => act(() => handle.dispatch(action)) };
};

describe('<SecondaryIdsForm /> on the transgenic tool page', () => {
	it('shows an error reported for the secondary identifiers beside them', () => {
		const { dispatch } = renderForm();

		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { secondaryIdentifiers: 'is bad' } });

		expect(within(fieldRow('Secondary IDs')).getByText('is bad')).toBeInTheDocument();
		expect(screen.getAllByText('is bad')).toHaveLength(1);
	});

	it('shows an error given as an object beside the secondary identifiers', () => {
		const { dispatch } = renderForm();

		dispatch({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { secondaryIdentifiers: { severity: 'error', message: 'is bad' } },
		});

		expect(within(fieldRow('Secondary IDs')).getByText('is bad')).toBeInTheDocument();
	});
});
