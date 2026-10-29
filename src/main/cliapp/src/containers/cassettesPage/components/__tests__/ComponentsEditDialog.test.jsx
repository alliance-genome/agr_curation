import React, { useState } from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../../tools/jest/utils';

const { getComponentList, replaceComponentList } = vi.hoisted(() => ({
	getComponentList: vi.fn(),
	replaceComponentList: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../../service/CassetteService', () => ({
	CassetteService: class {
		getComponentList = getComponentList;
		replaceComponentList = replaceComponentList;
	},
}));

vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../../service/useVocabularyTermSetService');
vi.mock('../../../../service/useControlledVocabularyService');

const { ComponentsEditDialog } = await import('../ComponentsEditDialog');
const { TRANSGENIC_TOOL_COMPONENTS, FREE_TEXT_COMPONENTS } = await import('../componentConfigs');

const toolAssociation = {
	id: 21,
	relation: { id: 31, name: 'expresses' },
	cassetteTransgenicToolAssociationObject: {
		type: 'TransgenicTool',
		id: 12,
		primaryExternalId: 'FB:FBto0000135',
		transgenicToolSymbol: { displayText: 'UAS' },
	},
	evidence: [],
	cassetteAssociationSubject: { id: 7 },
};

const editorCallback = vi.fn();

// The dialog as the table holds it: open for cassette 7, closing through its own state
const Harness = ({ config }) => {
	const [data, setData] = useState({ dialog: true, cassetteId: 7, mainRowProps: { editorCallback } });
	return (
		<>
			<span data-testid="open">{String(data.dialog)}</span>
			<ComponentsEditDialog data={data} setData={setData} config={config} />
		</>
	);
};

const renderDialog = (config = TRANSGENIC_TOOL_COMPONENTS) => renderWithClient(<Harness config={config} />);

describe('<ComponentsEditDialog />', () => {
	beforeEach(() => {
		getComponentList.mockReset();
		replaceComponentList.mockReset();
		editorCallback.mockReset();
	});

	it("reads the cassette's list through its own endpoint", async () => {
		getComponentList.mockResolvedValue({ data: { entities: [toolAssociation] } });
		renderDialog();

		expect(await screen.findByDisplayValue('UAS (FB:FBto0000135)')).toBeInTheDocument();
		expect(getComponentList).toHaveBeenCalledWith(7, 'transgenic-tool-associations');
		expect(screen.getByText('Transgenic Tool Components')).toBeInTheDocument();
	});

	it('saves the list without a deleted row, then hands the saved rows to the cassette row and closes', async () => {
		const user = userEvent.setup();
		getComponentList.mockResolvedValue({ data: { entities: [toolAssociation] } });
		replaceComponentList.mockResolvedValue({ data: { entities: [] } });
		renderDialog();

		const row = (await screen.findByDisplayValue('UAS (FB:FBto0000135)')).closest('tr');
		await user.click(row.querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(replaceComponentList).toHaveBeenCalledWith(7, 'transgenic-tool-associations', []));
		expect(editorCallback).toHaveBeenCalledWith([]);
		await waitFor(() => expect(screen.getByTestId('open')).toHaveTextContent('false'));
	});

	it('sends rows without their pop-up key and cassette', async () => {
		const user = userEvent.setup();
		getComponentList.mockResolvedValue({ data: { entities: [toolAssociation] } });
		replaceComponentList.mockResolvedValue({ data: { entities: [toolAssociation] } });
		renderDialog();

		await screen.findByDisplayValue('UAS (FB:FBto0000135)');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(replaceComponentList).toHaveBeenCalled());
		const [sent] = replaceComponentList.mock.calls[0][2];
		expect(sent).not.toHaveProperty('dataKey');
		expect(sent).not.toHaveProperty('cassetteAssociationSubject');
		expect(sent.id).toBe(21);
	});

	it("shows the API's errors on the rows they belong to and stays open", async () => {
		const user = userEvent.setup();
		getComponentList.mockResolvedValue({ data: { entities: [] } });
		replaceComponentList.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not update Transgenic Tool Components for: [7]',
					supplementalData: {
						errorMap: {
							cassetteTransgenicToolAssociations: {
								0: { cassetteTransgenicToolAssociationObject: 'Required field is empty' },
							},
						},
					},
				},
			},
		});
		renderDialog();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Add Row' })).toBeEnabled());
		await user.click(screen.getByRole('button', { name: 'Add Row' }));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Required field is empty')).toBeInTheDocument();
		expect(screen.getByTestId('open')).toHaveTextContent('true');
		expect(editorCallback).not.toHaveBeenCalled();
	});

	it('edits free text components with their own fields', async () => {
		getComponentList.mockResolvedValue({
			data: {
				entities: [
					{
						id: 3,
						relation: { id: 31, name: 'expresses' },
						componentSymbol: 'GAL4',
						taxonText: 'Saccharomyces cerevisiae',
					},
				],
			},
		});
		renderDialog(FREE_TEXT_COMPONENTS);

		expect(await screen.findByDisplayValue('GAL4')).toBeInTheDocument();
		expect(screen.getByDisplayValue('Saccharomyces cerevisiae')).toBeInTheDocument();
		expect(getComponentList).toHaveBeenCalledWith(7, 'components');
	});
});
