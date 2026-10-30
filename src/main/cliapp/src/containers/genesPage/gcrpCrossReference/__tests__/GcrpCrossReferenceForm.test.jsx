import React, { useEffect } from 'react';
import { act, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../../tools/jest/utils';

// The curie column suggests descriptors through a search.
vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const { GcrpCrossReferenceForm } = await import('../GcrpCrossReferenceForm');
const { useGeneReducer } = await import('../../useGeneReducer');
const { geneDetailData } = await import('../../mockData/mockData');

const addButton = () => screen.getByRole('button', { name: 'Add GCRP Cross Reference' });
const curieInput = (container) => container.querySelector('[aria-label="referencedCurie"]');

// The form reads the reducer's state, so it is driven through the real reducer.
const renderForm = (gene = geneDetailData.entity) => {
	const handle = {};
	const Harness = () => {
		const { geneState, geneDispatch } = useGeneReducer();
		handle.state = geneState;
		handle.dispatch = geneDispatch;
		useEffect(() => geneDispatch({ type: 'SET', value: gene }), [geneDispatch]);
		return <GcrpCrossReferenceForm state={geneState} dispatch={geneDispatch} />;
	};
	return { ...renderWithClient(<Harness />), handle };
};

describe('<GcrpCrossReferenceForm />', () => {
	it("shows the gene's GCRP cross reference and offers no second one", async () => {
		const { container } = renderForm();

		await waitFor(() => expect(curieInput(container)).toHaveValue('UniProtKB:P91931'));
		expect(addButton()).toBeDisabled();
	});

	it('removes the GCRP cross reference, then adds an empty one', async () => {
		const user = userEvent.setup();
		const { container, handle } = renderForm();
		await waitFor(() => expect(curieInput(container)).not.toBeNull());

		await user.click(container.querySelector('.pi-trash').closest('button'));

		expect(handle.state.gene.gcrpCrossReference).toBeNull();
		expect(curieInput(container)).toBeNull();
		expect(addButton()).toBeEnabled();

		await user.click(addButton());

		expect(curieInput(container)).toHaveValue('');
		expect(handle.state.gene.gcrpCrossReference.dataKey).toBeDefined();
		expect(addButton()).toBeDisabled();
	});

	it('keeps an edit on the gene', async () => {
		const user = userEvent.setup();
		const { container, handle } = renderForm();
		await waitFor(() => expect(curieInput(container)).not.toBeNull());

		await user.type(curieInput(container), '9');

		expect(handle.state.gene.gcrpCrossReference.referencedCurie).toBe('UniProtKB:P919319');
	});

	it('shows a message for the GCRP cross reference that no cell shows', async () => {
		const { container, handle } = renderForm();
		await waitFor(() => expect(curieInput(container)).not.toBeNull());

		act(() =>
			handle.dispatch({
				type: 'UPDATE_ERROR_MESSAGES',
				errorMessages: { gcrpCrossReference: 'id - Not a valid entry' },
			})
		);

		expect(await screen.findByText('id - Not a valid entry')).toBeInTheDocument();
	});
});
