import { act, fireEvent, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { termSetTermsFor } from '../../../service/__mocks__/useVocabularyTermSetService';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	renderSubResourceForm,
	selectInColumn,
	typeInColumn,
} from '../../../containers/allelesPage/__tests__/alleleFormTestUtils';
import { SearchService } from '../../../service/SearchService';
import { RelatedNotesForm } from '../RelatedNotesForm';
import { RelatedNotesForm as TransgenicToolRelatedNotesForm } from '../../../containers/transgenicToolsPage/detail/RelatedNotesForm';
import { useTransgenicToolReducer } from '../../../containers/transgenicToolsPage/detail/useTransgenicToolReducer';
import { RelatedNotesForm as VariantRelatedNotesForm } from '../../../containers/variantsPage/relatedNotes/RelatedNotesForm';
import { useVariantReducer } from '../../../containers/variantsPage/useVariantReducer';

vi.mock('../../../service/SearchService');
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const REFERENCE = { id: 1, curie: 'AGRKB:101' };
const NEW_REFERENCE = { id: 3, curie: 'AGRKB:103' };
const ERROR = { severity: 'error', message: 'this note is bad' };
const FIRST_ERROR = { severity: 'error', message: 'first note is bad' };
const COLUMN_MESSAGES = [
	['Note Type', 'note type is bad'],
	['Text', 'text is bad'],
	['Internal', 'internal is bad'],
	['Evidence', 'references are bad'],
];
const COLUMN_ERRORS = {
	noteType: { severity: 'error', message: 'note type is bad' },
	freeText: { severity: 'error', message: 'text is bad' },
	internal: { severity: 'error', message: 'internal is bad' },
	references: { severity: 'error', message: 'references are bad' },
};

/**
 * Renders a section against the reducer its page keeps, loaded with `notes` the way the page loads them.
 * `getRows()` reads back the notes the page will save; `setErrors(errors)` marks rows' errors, keyed by
 * `dataKey`, as a rejected save does.
 */
const renderReducerSection = ({ useReducer, stateKey, dispatchKey, entityKey, Form, notes, value }) => {
	const handle = {};
	const Harness = () => {
		const reducer = useReducer();
		handle.state = reducer[stateKey];
		handle.dispatch = reducer[dispatchKey];
		return <Form state={handle.state} dispatch={handle.dispatch} />;
	};
	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: { ...value, relatedNotes: notes } }));
	return {
		...result,
		getRows: () => handle.state[entityKey].relatedNotes,
		setErrors: (errorMessages) =>
			act(() => handle.dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: 'relatedNotes', errorMessages })),
	};
};

/**
 * The related notes sections on the detail pages: the shared section the allele and gene pages render over their
 * related notes sub-resource, and the transgenic tool's and variant's, which keep their notes in the page reducer.
 */
const SECTIONS = [
	{
		label: 'allele',
		marksInvalidCells: true,
		noteTypeSet: 'allele_note_type',
		render: (notes) => {
			const result = renderSubResourceForm(RelatedNotesForm, 'relatedNotes', notes);
			return { ...result, setErrors: result.setErrorMessages };
		},
	},
	{
		label: 'gene',
		marksInvalidCells: true,
		noteTypeSet: 'gene_note_type',
		render: (notes) => {
			const GeneRelatedNotesForm = (props) => <RelatedNotesForm {...props} noteTypeVocabType="gene_note_type" />;
			const result = renderSubResourceForm(GeneRelatedNotesForm, 'relatedNotes', notes);
			return { ...result, setErrors: result.setErrorMessages };
		},
	},
	{
		label: 'transgenic tool',
		marksInvalidCells: true,
		noteTypeSet: 'transgenic_tool_note_type',
		render: (notes) =>
			renderReducerSection({
				useReducer: useTransgenicToolReducer,
				stateKey: 'transgenicToolState',
				dispatchKey: 'transgenicToolDispatch',
				entityKey: 'transgenicTool',
				Form: TransgenicToolRelatedNotesForm,
				notes,
				value: { id: 1 },
			}),
	},
	{
		label: 'variant',
		noteTypeSet: 'variant_note_type',
		// The variant section keeps its own copy of the table, whose editors mark no cell invalid.
		marksInvalidCells: false,
		render: (notes) =>
			renderReducerSection({
				useReducer: useVariantReducer,
				stateKey: 'variantState',
				dispatchKey: 'variantDispatch',
				entityKey: 'variant',
				Form: VariantRelatedNotesForm,
				notes,
				value: { id: 1, taxon: { curie: 'NCBITaxon:6239' } },
			}),
	},
];

const dropdownLabel = (container, header, rowIndex = 0) =>
	getCellByHeader(container, header, rowIndex).querySelector('.p-dropdown-label').textContent.trim();

const textIn = (container, header, rowIndex = 0) =>
	getCellByHeader(container, header, rowIndex).querySelector('textarea').value;

const bodyRows = (container) =>
	[...container.querySelectorAll('tbody tr')].filter((row) => row.querySelectorAll('td').length > 1);

const getButton = (container, label) => {
	const button = [...container.querySelectorAll('button')].find((candidate) => candidate.textContent.trim() === label);
	if (!button) throw new Error(`No button labelled "${label}"`);
	return button;
};

/** Picks a reference suggestion and waits for its panel to finish closing, which it does after the pick. */
const pickReference = async (container, rowIndex = 0) => {
	await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/, rowIndex);
	await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
};

describe.each(SECTIONS)('the $label related notes section', (section) => {
	const note = (id, freeText, overrides = {}) => ({
		id,
		noteType: termSetTermsFor(section.noteTypeSet)[0],
		freeText,
		internal: false,
		obsolete: false,
		references: [REFERENCE],
		...overrides,
	});
	const twoNotes = () => [note(11, 'first note'), note(12, 'second note')];

	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_REFERENCE] }));
	});

	it('shows the loaded notes', () => {
		const { container } = section.render([note(11, 'first note'), note(12, 'second note', { internal: true })]);

		expect(bodyRows(container)).toHaveLength(2);
		expect(dropdownLabel(container, 'Note Type')).toBe(`${section.noteTypeSet}_alpha`);
		expect(textIn(container, 'Text')).toBe('first note');
		expect(dropdownLabel(container, 'Internal')).toBe('false');
		expect(getCellByHeader(container, 'Evidence').textContent).toContain(REFERENCE.curie);
		expect(textIn(container, 'Text', 1)).toBe('second note');
		expect(dropdownLabel(container, 'Internal', 1)).toBe('true');
	});

	it('offers its note types', () => {
		const { container } = section.render([note(11, 'first note')]);

		fireEvent.click(getCellByHeader(container, 'Note Type').querySelector('.p-dropdown'));

		const options = [...document.querySelectorAll('.p-dropdown-item')].map((item) => item.textContent.trim());
		expect(options).toEqual([`${section.noteTypeSet}_alpha`, `${section.noteTypeSet}_beta`]);
	});

	it('stores each edit on the note', async () => {
		const { container, getRows } = section.render(twoNotes());

		selectInColumn(container, 'Note Type', `${section.noteTypeSet}_beta`, 1);
		typeInColumn(container, 'Text', 'second note edited', 1);
		selectInColumn(container, 'Internal', 'true', 1);
		await pickReference(container, 1);

		expect(getRows()[1]).toMatchObject({
			id: 12,
			noteType: termSetTermsFor(section.noteTypeSet)[1],
			freeText: 'second note edited',
			internal: true,
			references: [REFERENCE, NEW_REFERENCE],
		});
		expect(getRows()[0]).toMatchObject(note(11, 'first note'));
	});

	it('adds an empty note above the others, ready to edit', () => {
		const { container, getRows } = section.render([note(11, 'first note')]);

		fireEvent.click(getButton(container, 'Add Related Note'));

		expect(bodyRows(container)).toHaveLength(2);
		expect(textIn(container, 'Text')).toBe('');
		expect(getRows()).toHaveLength(2);
		expect(getRows()[0]).toMatchObject({ internal: false, noteType: null, freeText: '' });
		expect(getRows()[1]).toMatchObject({ id: 11 });

		typeInColumn(container, 'Text', 'new note');

		expect(getRows()[0].freeText).toBe('new note');
		expect(getRows()[1].freeText).toBe('first note');
	});

	it('stores each edit on the note that moved up after the note above it is deleted', async () => {
		const { container, getRows } = section.render(twoNotes());

		clickDeleteInRow(container, 0);
		await waitFor(() => expect(bodyRows(container)).toHaveLength(1));
		selectInColumn(container, 'Note Type', `${section.noteTypeSet}_beta`);
		typeInColumn(container, 'Text', 'second note edited');
		selectInColumn(container, 'Internal', 'true');
		await pickReference(container);

		expect(getRows()).toHaveLength(1);
		expect(getRows()[0]).toMatchObject({
			id: 12,
			noteType: termSetTermsFor(section.noteTypeSet)[1],
			freeText: 'second note edited',
			internal: true,
			references: [REFERENCE, NEW_REFERENCE],
		});
	});

	it('shows each note error on its own cell of the note whose key it is stored under', () => {
		const { container, getRows, setErrors } = section.render(twoNotes());

		setErrors({ [getRows()[1].dataKey]: COLUMN_ERRORS });

		COLUMN_MESSAGES.forEach(([header, message]) => {
			expect(getCellByHeader(container, header, 1).textContent).toContain(message);
			if (section.marksInvalidCells) {
				expect(getCellByHeader(container, header, 1).querySelector('.p-invalid')).not.toBeNull();
			}
			expect(getCellByHeader(container, header, 0).textContent).not.toContain(message);
			expect(getCellByHeader(container, header, 0).querySelector('.p-invalid')).toBeNull();
		});
		COLUMN_MESSAGES.forEach(([header]) => {
			const others = COLUMN_MESSAGES.filter(([otherHeader]) => otherHeader !== header);
			others.forEach(([, message]) => expect(getCellByHeader(container, header, 1).textContent).not.toContain(message));
		});
	});

	it("keeps a rejected save's error on its note after the note above it is deleted", async () => {
		const { container, getRows, setErrors } = section.render(twoNotes());
		const [firstKey, secondKey] = getRows().map((row) => row.dataKey);
		setErrors({ [firstKey]: { freeText: FIRST_ERROR }, [secondKey]: { freeText: ERROR } });

		clickDeleteInRow(container, 0);
		await waitFor(() => expect(bodyRows(container)).toHaveLength(1));

		expect(textIn(container, 'Text')).toBe('second note');
		expect(getCellByHeader(container, 'Text').textContent).toContain('this note is bad');
		expect(container.textContent).not.toContain('first note is bad');
	});
});
