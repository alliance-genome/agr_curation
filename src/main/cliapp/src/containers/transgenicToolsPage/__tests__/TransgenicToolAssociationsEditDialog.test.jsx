import React, { useState } from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const { getTransgenicToolAssociations, replaceTransgenicToolAssociations } = vi.hoisted(() => ({
	getTransgenicToolAssociations: vi.fn(),
	replaceTransgenicToolAssociations: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/TransgenicToolService', () => ({
	TransgenicToolService: class {
		getTransgenicToolAssociations = getTransgenicToolAssociations;
		replaceTransgenicToolAssociations = replaceTransgenicToolAssociations;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/useVocabularyTermSetService');
vi.mock('../../../service/useControlledVocabularyService');

const { TransgenicToolAssociationsEditDialog } = await import('../TransgenicToolAssociationsEditDialog');

const association = {
	id: 21,
	relation: { id: 31, name: 'is_compatible_with' },
	transgenicToolTransgenicToolAssociationObject: {
		id: 12,
		primaryExternalId: 'FB:FBto0000135',
		transgenicToolSymbol: { displayText: 'UAS' },
	},
	transgenicToolAssociationSubject: { id: 7 },
	internal: false,
	obsolete: false,
};

const editorCallback = vi.fn();

// The dialog as the table holds it: open for tool 7, closing through its own state
const Harness = () => {
	const [data, setData] = useState({ dialog: true, transgenicToolId: 7, mainRowProps: { editorCallback } });
	return (
		<>
			<span data-testid="open">{String(data.dialog)}</span>
			<button onClick={() => setData((previous) => ({ ...previous, dialog: true }))}>Reopen</button>
			<TransgenicToolAssociationsEditDialog data={data} setData={setData} />
		</>
	);
};

const renderDialog = () => renderWithClient(<Harness />);

const findAssociation = () => screen.findByDisplayValue('UAS (FB:FBto0000135)', {}, { timeout: 5000 });

describe('<TransgenicToolAssociationsEditDialog />', () => {
	beforeEach(() => {
		getTransgenicToolAssociations.mockReset();
		replaceTransgenicToolAssociations.mockReset();
		editorCallback.mockReset();
	});

	it("reads the tool's associations through their own endpoint", async () => {
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [association] } });
		renderDialog();

		expect(await findAssociation()).toBeInTheDocument();
		expect(getTransgenicToolAssociations).toHaveBeenCalledWith(7);
		expect(screen.getByText('Transgenic Tool Associations')).toBeInTheDocument();
	});

	it('saves the associations, hands the row their summary and closes', async () => {
		const user = userEvent.setup();
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [association] } });
		replaceTransgenicToolAssociations.mockResolvedValue({ data: { entities: [association] } });
		renderDialog();

		await findAssociation();
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(replaceTransgenicToolAssociations).toHaveBeenCalled());
		const [toolId, [sent]] = replaceTransgenicToolAssociations.mock.calls[0];
		expect(toolId).toBe(7);
		expect(sent).not.toHaveProperty('dataKey');
		expect(sent).not.toHaveProperty('transgenicToolAssociationSubject');
		expect(sent.transgenicToolTransgenicToolAssociationObject).toEqual({ type: 'TransgenicTool', id: 12 });
		expect(editorCallback).toHaveBeenCalledWith([
			{ relation: 'is_compatible_with', symbol: 'UAS', identifier: 'FB:FBto0000135' },
		]);
		await waitFor(() => expect(screen.getByTestId('open')).toHaveTextContent('false'));
	});

	it('saves a deleted association as an empty list', async () => {
		const user = userEvent.setup();
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [association] } });
		replaceTransgenicToolAssociations.mockResolvedValue({ data: { entities: [] } });
		renderDialog();

		const row = (await findAssociation()).closest('tr');
		await user.click(row.querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(replaceTransgenicToolAssociations).toHaveBeenCalledWith(7, []));
		expect(editorCallback).toHaveBeenCalledWith([]);
	});

	it("shows the API's errors on the rows they belong to and stays open", async () => {
		const user = userEvent.setup();
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [] } });
		replaceTransgenicToolAssociations.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not update Transgenic Tool Associations for: [7]',
					supplementalData: {
						errorMap: {
							transgenicToolTransgenicToolAssociations: {
								0: { transgenicToolTransgenicToolAssociationObject: 'Required field is empty' },
							},
						},
					},
				},
			},
		});
		renderDialog();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Add Association' })).toBeEnabled());
		await user.click(screen.getByRole('button', { name: 'Add Association' }));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Required field is empty')).toBeInTheDocument();
		expect(screen.getByTestId('open')).toHaveTextContent('true');
		expect(editorCallback).not.toHaveBeenCalled();
	});

	it('refuses to save while the associations could not be read', async () => {
		getTransgenicToolAssociations.mockRejectedValue(new Error('down'));
		renderDialog();

		expect(await screen.findByText(/Could not load these associations/)).toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
		expect(replaceTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('reads the associations afresh when reopened, dropping unsaved edits', async () => {
		const user = userEvent.setup();
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [association] } });
		renderDialog();

		const row = (await findAssociation()).closest('tr');
		await user.click(row.querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Cancel' }));
		await waitFor(() => expect(screen.getByTestId('open')).toHaveTextContent('false'));

		await user.click(screen.getByRole('button', { name: 'Reopen' }));

		expect(await findAssociation()).toBeInTheDocument();
		expect(getTransgenicToolAssociations).toHaveBeenCalledTimes(2);
	});
});
