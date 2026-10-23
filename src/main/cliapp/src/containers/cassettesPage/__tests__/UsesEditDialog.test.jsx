import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';

const validate = vi.fn();
const search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));

vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

// Typing into the term autocomplete builds its own SearchService; stub it so no request leaves the test.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

const { UsesEditDialog } = await import('../uses/UsesEditDialog');
const { useNamespaceFilter, USE_NAMESPACE } = await import('../uses/UsesEditor');

const use = {
	id: 43,
	internal: false,
	uses: [{ curie: 'FBcv:0005073', name: 'promoter trap', namespace: USE_NAMESPACE }],
	evidence: [],
};

// PrimeReact fires the dialog's onShow, which loads its rows, only after the enter transition
const waitForRows = () => screen.findByText('promoter trap (FBcv:0005073)', {}, { timeout: 5000 });

const renderDialog = (editorCallback, setErrorMessagesMainRow = vi.fn()) => {
	const usesData = {
		originalUses: [use],
		dialog: true,
		isInEdit: true,
		rowIndex: 0,
		mainRowProps: { editorCallback },
	};
	return renderWithClient(
		<UsesEditDialog
			originalUsesData={usesData}
			setOriginalUsesData={vi.fn()}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={setErrorMessagesMainRow}
		/>
	);
};

describe('<UsesEditDialog />', () => {
	beforeEach(() => {
		validate.mockReset();
		search.mockClear();
	});

	it('shows the uses of the cassette', async () => {
		renderDialog(vi.fn());

		expect(await screen.findByText('Uses', { selector: 'h3' })).toBeInTheDocument();
		expect(await waitForRows()).toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'New Use' })).toBeInTheDocument();
	});

	it('keeps the edits once every use validates, without its row keys', async () => {
		const user = userEvent.setup();
		const editorCallback = vi.fn();
		const setErrorMessagesMainRow = vi.fn();
		validate.mockResolvedValue({ isError: false });
		renderDialog(editorCallback, setErrorMessagesMainRow);

		await waitForRows();
		await user.click(screen.getByRole('button', { name: 'Keep Edits' }));

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(validate).toHaveBeenCalledWith(Endpoints.SlotAnnotation.CASSETTE_USE, expect.objectContaining({ id: 43 }));
		const [kept] = editorCallback.mock.calls[0];
		expect(kept).toHaveLength(1);
		expect(kept[0]).not.toHaveProperty('dataKey');
		expect(kept[0].uses[0].curie).toBe('FBcv:0005073');
		// the row shows it has pending edits
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			0: { cassetteUses: expect.objectContaining({ message: 'Pending Edits!' }) },
		});
	});

	it('keeps nothing while a use fails validation', async () => {
		const user = userEvent.setup();
		const editorCallback = vi.fn();
		validate.mockResolvedValue({ isError: true, data: { uses: 'Not a valid entry' } });
		renderDialog(editorCallback);

		await waitForRows();
		await user.click(screen.getByRole('button', { name: 'Keep Edits' }));

		expect(await screen.findByText('Not a valid entry')).toBeInTheDocument();
		expect(editorCallback).not.toHaveBeenCalled();
	});
});

describe('use term search', () => {
	it('is restricted to the experimental_tool_descriptor namespace', () => {
		expect(useNamespaceFilter).toEqual({
			namespaceFilter: {
				namespace: { queryString: 'experimental_tool_descriptor', tokenOperator: 'AND', useKeywordFields: true },
			},
		});
	});
});
