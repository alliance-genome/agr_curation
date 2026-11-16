import { fireEvent, render, waitFor } from '@testing-library/react';
import { SymbolEditDialog } from '../SymbolEditDialog';
import { FullNameEditDialog } from '../FullNameEditDialog';
import { vocabularyTermsFor } from '../../../../service/__mocks__/useControlledVocabularyService';
import { termSetTermsFor } from '../../../../service/__mocks__/useVocabularyTermSetService';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	selectInColumn,
	typeInColumn,
} from '../../../allelesPage/__tests__/alleleFormTestUtils';
import { SearchService } from '../../../../service/SearchService';

vi.mock('../../../../service/SearchService');
vi.mock('../../../../service/useControlledVocabularyService');
vi.mock('../../../../service/useVocabularyTermSetService');

// The pop-ups construct a ValidationService at render and call it on Keep Edits.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const EVIDENCE = { id: 1, curie: 'AGRKB:101' };
const NEW_EVIDENCE = { id: 3, curie: 'AGRKB:103' };

/**
 * The name pop-ups the entity tables open: the symbol's, and the full name's, which the gene table also opens
 * titled Systematic Name over the systematic name types. Each edits one name in local state and hands it back to
 * the main row on Keep Edits.
 */
const DIALOGS = [
	{
		label: 'Symbol',
		title: 'Symbol',
		nameTypeSet: 'symbol_name_type',
		field: 'alleleSymbol',
		endpoint: 'allelesymbolslotannotation',
		canAddAndDelete: false,
		render: (originalNames, props) => (
			<SymbolEditDialog
				field={props.field}
				endpoint={props.endpoint}
				originalSymbolData={{ originalSymbols: originalNames, ...props.data }}
				setOriginalSymbolData={vi.fn()}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={props.setErrorMessagesMainRow}
			/>
		),
	},
	{
		label: 'Full Name',
		title: 'Full Name',
		nameTypeSet: 'full_name_type',
		field: 'alleleFullName',
		endpoint: 'allelefullnameslotannotation',
		canAddAndDelete: true,
		render: (originalNames, props) => (
			<FullNameEditDialog
				field={props.field}
				endpoint={props.endpoint}
				originalFullNameData={{ originalFullNames: originalNames, ...props.data }}
				setOriginalFullNameData={vi.fn()}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={props.setErrorMessagesMainRow}
			/>
		),
	},
	{
		label: 'Systematic Name',
		title: 'Systematic Name',
		nameTypeSet: 'systematic_name_type',
		field: 'geneSystematicName',
		endpoint: 'genesystematicnameslotannotation',
		canAddAndDelete: true,
		render: (originalNames, props) => (
			<FullNameEditDialog
				field={props.field}
				endpoint={props.endpoint}
				title="Systematic Name"
				nameTypeVocabularyTermSet="systematic_name_type"
				originalFullNameData={{ originalFullNames: originalNames, ...props.data }}
				setOriginalFullNameData={vi.fn()}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={props.setErrorMessagesMainRow}
			/>
		),
	},
];

const getButton = (baseElement, label) => {
	const button = [...baseElement.querySelectorAll('button')].find(
		(candidate) => candidate.textContent.trim() === label
	);
	if (!button) throw new Error(`No button labelled "${label}"`);
	return button;
};

const queryButton = (baseElement, label) =>
	[...baseElement.querySelectorAll('button')].find((candidate) => candidate.textContent.trim() === label);

const dropdownLabel = (container, header) =>
	getCellByHeader(container, header).querySelector('.p-dropdown-label').textContent.trim();

const textIn = (container, header) => getCellByHeader(container, header).querySelector('input').value;

const columnHeaders = (container) =>
	[...container.querySelectorAll('thead tr:first-child th')].map((header) => header.textContent.trim());

/** Picks an evidence suggestion and waits for its panel to finish closing, which it does after the pick. */
const pickEvidence = async (container) => {
	await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/);
	await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
};

describe.each(DIALOGS)('$label pop-up', (dialog) => {
	const loadedName = () => ({
		displayText: 'loaded',
		formatText: 'loaded format',
		synonymScope: vocabularyTermsFor('synonym_scope')[0],
		nameType: termSetTermsFor(dialog.nameTypeSet)[0],
		synonymUrl: 'https://example.org',
		internal: false,
		evidence: [EVIDENCE],
	});

	const openDialog = async (originalNames = [loadedName()]) => {
		const editorCallback = vi.fn();
		const setErrorMessagesMainRow = vi.fn();
		const result = render(
			dialog.render(originalNames, {
				field: dialog.field,
				endpoint: dialog.endpoint,
				data: { isInEdit: true, dialog: true, rowIndex: 0, mainRowProps: { editorCallback } },
				setErrorMessagesMainRow,
			})
		);
		// The pop-up copies its name in onShow, which runs as the open transition ends. Before then the table shows
		// its empty message, so a pop-up opened with a name waits for that name's row.
		await waitFor(() => {
			expect(result.baseElement.querySelector('.p-dialog-enter-done')).not.toBeNull();
			if (originalNames.some(Boolean)) {
				expect(result.baseElement.querySelectorAll('tbody tr td').length).toBeGreaterThan(1);
			}
		});
		return { ...result, editorCallback, setErrorMessagesMainRow };
	};

	const keepEdits = (baseElement) => fireEvent.click(getButton(baseElement, 'Keep Edits'));

	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_EVIDENCE] }));
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	it('opens titled, with the name', async () => {
		const { baseElement } = await openDialog();

		expect(baseElement.querySelector('.p-dialog h3').textContent).toBe(dialog.title);
		expect(textIn(baseElement, 'Display Text')).toBe('loaded');
		expect(textIn(baseElement, 'Format Text')).toBe('loaded format');
		expect(textIn(baseElement, 'Synonym URL')).toBe('https://example.org');
		expect(dropdownLabel(baseElement, 'Synonym Scope')).toBe('synonym_scope_alpha');
		expect(dropdownLabel(baseElement, 'Name Type')).toBe(`${dialog.nameTypeSet}_alpha`);
		expect(dropdownLabel(baseElement, 'Internal')).toBe('false');
		expect(getCellByHeader(baseElement, 'Evidence').textContent).toContain(EVIDENCE.curie);
	});

	it('hands back the edited name, validated against its endpoint, and marks the row as having pending edits', async () => {
		const { baseElement, editorCallback, setErrorMessagesMainRow } = await openDialog();

		typeInColumn(baseElement, 'Display Text', 'edited');
		typeInColumn(baseElement, 'Format Text', 'edited format');
		typeInColumn(baseElement, 'Synonym URL', 'https://example.org/edited');
		selectInColumn(baseElement, 'Synonym Scope', 'synonym_scope_beta');
		selectInColumn(baseElement, 'Name Type', `${dialog.nameTypeSet}_beta`);
		selectInColumn(baseElement, 'Internal', 'true');
		await pickEvidence(baseElement);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith({
			displayText: 'edited',
			formatText: 'edited format',
			synonymUrl: 'https://example.org/edited',
			synonymScope: vocabularyTermsFor('synonym_scope')[1],
			nameType: termSetTermsFor(dialog.nameTypeSet)[1],
			internal: true,
			evidence: [EVIDENCE, NEW_EVIDENCE],
		});
		expect(validate.mock.calls[0][0]).toBe(dialog.endpoint);
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			0: { [dialog.field]: { severity: 'warn', message: 'Pending Edits!' } },
		});
	});

	it('shows a validation error on its cell and keeps the edits back', async () => {
		validate.mockResolvedValue({ isSuccess: false, isError: true, data: { displayText: 'Required field is empty' } });
		const { baseElement, editorCallback } = await openDialog();

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Display Text').textContent).toContain('Required field is empty')
		);
		expect(getCellByHeader(baseElement, 'Format Text').textContent).not.toContain('Required field is empty');
		expect(editorCallback).not.toHaveBeenCalled();
	});

	if (dialog.canAddAndDelete) {
		it('hands back null once the name is deleted', async () => {
			const { baseElement, editorCallback } = await openDialog();

			clickDeleteInRow(baseElement);
			keepEdits(baseElement);

			await waitFor(() => expect(editorCallback).toHaveBeenCalledWith(null));
		});

		it('offers no new name while it has one', async () => {
			const { baseElement } = await openDialog();

			expect(getButton(baseElement, 'New Name')).toBeDisabled();
		});

		it('offers a new name, with the first name type, when it has none', async () => {
			const { baseElement, editorCallback } = await openDialog([null]);

			fireEvent.click(getButton(baseElement, 'New Name'));

			await waitFor(() => expect(baseElement.querySelectorAll('tbody tr td').length).toBeGreaterThan(1));
			expect(dropdownLabel(baseElement, 'Name Type')).toBe(`${dialog.nameTypeSet}_alpha`);
			expect(getButton(baseElement, 'New Name')).toBeDisabled();

			keepEdits(baseElement);

			await waitFor(() => expect(editorCallback).toHaveBeenCalled());
			expect(editorCallback.mock.calls[0][0]).toMatchObject({
				displayText: '',
				formatText: '',
				internal: false,
				nameType: termSetTermsFor(dialog.nameTypeSet)[0],
			});
		});
	} else {
		it('offers no new name and no delete', async () => {
			const { baseElement } = await openDialog();

			expect(queryButton(baseElement, 'New Name')).toBeUndefined();
			expect(columnHeaders(baseElement)).not.toContain('Actions');
			expect(baseElement.querySelector('.p-dialog tbody button')).toBeNull();
		});
	}
});

describe.each(DIALOGS)('$label pop-up errors', (dialog) => {
	const FIELD_ERRORS = {
		displayText: 'display text is bad',
		formatText: 'format text is bad',
		synonymScope: 'scope is bad',
		nameType: 'name type is bad',
		synonymUrl: 'url is bad',
		internal: 'internal is bad',
		evidence: 'evidence is bad',
	};
	const CELLS = [
		['Display Text', 'display text is bad'],
		['Format Text', 'format text is bad'],
		['Synonym Scope', 'scope is bad'],
		['Name Type', 'name type is bad'],
		['Synonym URL', 'url is bad'],
		['Internal', 'internal is bad'],
		['Evidence', 'evidence is bad'],
	];

	it('shows each validation error on the cell that caused it, with invalid styling', async () => {
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: false, isError: true, data: FIELD_ERRORS });
		const name = {
			displayText: 'loaded',
			formatText: 'loaded',
			synonymScope: null,
			nameType: termSetTermsFor(dialog.nameTypeSet)[0],
			synonymUrl: '',
			internal: false,
			evidence: [EVIDENCE],
		};
		const { baseElement } = render(
			dialog.render([name], {
				field: dialog.field,
				endpoint: dialog.endpoint,
				data: { isInEdit: true, dialog: true, rowIndex: 0, mainRowProps: { editorCallback: vi.fn() } },
				setErrorMessagesMainRow: vi.fn(),
			})
		);
		await waitFor(() => expect(baseElement.querySelectorAll('tbody tr td').length).toBeGreaterThan(1));

		fireEvent.click(getButton(baseElement, 'Keep Edits'));

		await waitFor(() => expect(getCellByHeader(baseElement, 'Evidence').textContent).toContain('evidence is bad'));
		CELLS.forEach(([header, message]) => {
			const cell = getCellByHeader(baseElement, header);
			expect(cell.textContent).toContain(message);
			expect(cell.querySelector('.p-invalid')).not.toBeNull();
		});
	});
});
