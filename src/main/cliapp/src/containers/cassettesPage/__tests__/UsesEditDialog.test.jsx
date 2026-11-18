import React from 'react';
import { fireEvent, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';

const validate = vi.fn();
const NEW_USE_TERM = {
	id: 77,
	curie: 'FBcv:0005074',
	name: 'enhancer trap',
	namespace: 'experimental_tool_descriptor',
};
const NEW_EVIDENCE = { id: 3, curie: 'AGRKB:103' };
const RESULTS_BY_ENDPOINT = {
	[Endpoints.Ontology.FBCV]: [NEW_USE_TERM],
	[Endpoints.Document.LITERATURE_REFERENCE]: [NEW_EVIDENCE],
};
const search = vi.fn((endpoint) => {
	const results = RESULTS_BY_ENDPOINT[endpoint] ?? [];
	return Promise.resolve({ results, totalResults: results.length });
});

vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

// The use term and evidence autocompletes each build a SearchService; the stub offers each its own results, so
// a search sent to the wrong endpoint finds nothing.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { UsesEditDialog } = await import('../uses/UsesEditDialog');
const { clickDeleteInRow, getCellByHeader, pickSuggestionInColumn, selectInColumn, typeInColumn } = await import(
	'../../allelesPage/__tests__/alleleFormTestUtils'
);

const USE_NAMESPACE = 'experimental_tool_descriptor';

const use = {
	id: 43,
	internal: false,
	uses: [{ curie: 'FBcv:0005073', name: 'promoter trap', namespace: USE_NAMESPACE }],
	evidence: [],
};

// PrimeReact fires the dialog's onShow, which loads its rows, only after the enter transition
const waitForRows = () => screen.findByText('promoter trap (FBcv:0005073)', {}, { timeout: 5000 });

const renderDialog = (editorCallback, setErrorMessagesMainRow = vi.fn(), entityProps = {}) => {
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
			{...entityProps}
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

	it("validates a transgenic tool's uses against its own endpoint and files the row's state under its field", async () => {
		const user = userEvent.setup();
		const editorCallback = vi.fn();
		const setErrorMessagesMainRow = vi.fn();
		validate.mockResolvedValue({ isError: false });
		renderDialog(editorCallback, setErrorMessagesMainRow, {
			field: 'transgenicToolUses',
			endpoint: Endpoints.SlotAnnotation.TRANSGENIC_TOOL_USE,
		});

		await waitForRows();
		await user.click(screen.getByRole('button', { name: 'Keep Edits' }));

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(validate).toHaveBeenCalledWith(
			Endpoints.SlotAnnotation.TRANSGENIC_TOOL_USE,
			expect.objectContaining({ id: 43 })
		);
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			0: { transgenicToolUses: expect.objectContaining({ message: 'Pending Edits!' }) },
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

describe('<UsesEditDialog /> edits', () => {
	const EVIDENCE = { id: 1, curie: 'AGRKB:101' };
	const PROMOTER_TRAP = { id: 76, curie: 'FBcv:0005073', name: 'promoter trap', namespace: USE_NAMESPACE };
	const useRow = (id, overrides = {}) => ({
		id,
		internal: false,
		uses: [PROMOTER_TRAP],
		evidence: [EVIDENCE],
		...overrides,
	});
	const VALID = { isSuccess: true, isError: false, data: {} };

	const bodyRows = (baseElement) =>
		[...baseElement.querySelectorAll('.p-dialog tbody tr')].filter((row) => row.querySelectorAll('td').length > 1);

	const tokens = (container, header, rowIndex = 0) =>
		[...getCellByHeader(container, header, rowIndex).querySelectorAll('.p-autocomplete-token-label')].map((token) =>
			token.textContent.trim()
		);

	const getButton = (baseElement, label) => {
		const button = [...baseElement.querySelectorAll('button')].find(
			(candidate) => candidate.textContent.trim() === label
		);
		if (!button) throw new Error(`No button labelled "${label}"`);
		return button;
	};

	const openDialog = async (originalUses) => {
		const editorCallback = vi.fn();
		const setErrorMessagesMainRow = vi.fn();
		const result = renderWithClient(
			<UsesEditDialog
				originalUsesData={{ originalUses, dialog: true, isInEdit: true, rowIndex: 0, mainRowProps: { editorCallback } }}
				setOriginalUsesData={vi.fn()}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={setErrorMessagesMainRow}
			/>
		);
		// The pop-up copies its uses in onShow, which runs as the open transition ends.
		await waitFor(() => {
			expect(result.baseElement.querySelector('.p-dialog-enter-done')).not.toBeNull();
			expect(bodyRows(result.baseElement)).toHaveLength(originalUses.length);
		});
		return { ...result, editorCallback, setErrorMessagesMainRow };
	};

	/** Picks a suggestion and waits for its panel to finish closing, which it does after the pick. */
	const pick = async (container, header, query, suggestion, rowIndex = 0) => {
		await pickSuggestionInColumn(container, header, query, suggestion, rowIndex);
		await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
	};

	const keepEdits = (baseElement) => fireEvent.click(getButton(baseElement, 'Keep Edits'));

	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue(VALID);
		search.mockClear();
	});

	it("shows each use's terms, internal flag and evidence", async () => {
		const { baseElement } = await openDialog([useRow(43), useRow(44, { internal: true, evidence: [] })]);

		expect(tokens(baseElement, 'Uses')).toEqual(['promoter trap (FBcv:0005073)']);
		expect(getCellByHeader(baseElement, 'Internal').querySelector('.p-dropdown-label').textContent).toBe('false');
		expect(tokens(baseElement, 'Evidence')).toEqual(['AGRKB:101']);
		expect(getCellByHeader(baseElement, 'Internal', 1).querySelector('.p-dropdown-label').textContent).toBe('true');
		expect(tokens(baseElement, 'Evidence', 1)).toEqual([]);
	});

	it('searches only the experimental tool descriptor terms', async () => {
		const { baseElement } = await openDialog([useRow(43)]);

		typeInColumn(baseElement, 'Uses', 'enhancer');

		await waitFor(() => expect(search).toHaveBeenCalled());
		const [endpoint, , , , filters] = search.mock.calls.at(-1);
		expect(endpoint).toBe(Endpoints.Ontology.FBCV);
		expect(filters.namespaceFilter).toEqual({
			namespace: { queryString: USE_NAMESPACE, tokenOperator: 'AND', useKeywordFields: true },
		});
	});

	it('hands back each edit to the use', async () => {
		const { baseElement, editorCallback } = await openDialog([useRow(43)]);

		await pick(baseElement, 'Uses', 'enhancer', /FBcv:0005074/);
		selectInColumn(baseElement, 'Internal', 'true');
		await pick(baseElement, 'Evidence', 'AGRKB', /AGRKB:103/);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ id: 43, internal: true, uses: [PROMOTER_TRAP, NEW_USE_TERM], evidence: [EVIDENCE, NEW_EVIDENCE] },
		]);
	});

	it('hands back a new use added with New Use', async () => {
		const { baseElement, editorCallback } = await openDialog([useRow(43)]);

		fireEvent.click(getButton(baseElement, 'New Use'));
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(2));
		await pick(baseElement, 'Uses', 'enhancer', /FBcv:0005074/, 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const handedBack = editorCallback.mock.calls[0][0];
		expect(handedBack).toHaveLength(2);
		expect(handedBack[0]).toEqual(useRow(43));
		expect(handedBack[1]).toMatchObject({ internal: false, uses: [NEW_USE_TERM] });
		expect(handedBack[1]).not.toHaveProperty('dataKey');
	});

	it('saves each edit on the use that moved up after the use above it is deleted', async () => {
		const { baseElement, editorCallback } = await openDialog([useRow(43), useRow(44, { evidence: [] })]);

		clickDeleteInRow(baseElement, 0);
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(1));
		await pick(baseElement, 'Uses', 'enhancer', /FBcv:0005074/);
		selectInColumn(baseElement, 'Internal', 'true');
		await pick(baseElement, 'Evidence', 'AGRKB', /AGRKB:103/);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ id: 44, internal: true, uses: [PROMOTER_TRAP, NEW_USE_TERM], evidence: [NEW_EVIDENCE] },
		]);
	});

	it('shows each validation error on its own cell of the use that caused it', async () => {
		const columnErrors = [
			['Uses', 'uses', 'uses are bad'],
			['Internal', 'internal', 'internal is bad'],
			['Evidence', 'evidence', 'evidence is bad'],
		];
		validate.mockResolvedValueOnce(VALID).mockResolvedValueOnce({
			isSuccess: false,
			isError: true,
			data: Object.fromEntries(columnErrors.map(([, field, message]) => [field, message])),
		});
		const { baseElement, editorCallback, setErrorMessagesMainRow } = await openDialog([useRow(43), useRow(44)]);

		keepEdits(baseElement);

		await waitFor(() => expect(getCellByHeader(baseElement, 'Uses', 1).textContent).toContain('uses are bad'));
		columnErrors.forEach(([header, , message]) => {
			expect(getCellByHeader(baseElement, header, 1).textContent).toContain(message);
			expect(getCellByHeader(baseElement, header, 0).textContent).not.toContain(message);
		});
		expect(editorCallback).not.toHaveBeenCalled();
		expect(setErrorMessagesMainRow).not.toHaveBeenCalled();
	});

	it('shows a validation error on the right use after the use above it is deleted', async () => {
		validate.mockResolvedValue({ isSuccess: false, isError: true, data: { uses: 'remaining use is bad' } });
		const { baseElement } = await openDialog([useRow(43), useRow(44, { evidence: [] })]);

		clickDeleteInRow(baseElement, 0);
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(1));
		keepEdits(baseElement);

		await waitFor(() => expect(getCellByHeader(baseElement, 'Uses').textContent).toContain('remaining use is bad'));
		expect(tokens(baseElement, 'Evidence')).toEqual([]);
	});
});
