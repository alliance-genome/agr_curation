import { useState } from 'react';
import { act, fireEvent, waitFor } from '@testing-library/react';
import { AlleleGeneAssociationsForm } from '../AlleleGeneAssociationsForm';
import { prepareGeneAssociations } from '../utils';
import { SubResourcesProvider } from '../../../../components/SubResourcesContext';
import { renderWithClient } from '../../../../tools/jest/utils';
import { termSetTermsFor } from '../../../../service/__mocks__/useVocabularyTermSetService';
import { clickDeleteInRow, getCellByHeader, selectInColumn, typeInColumn } from '../../__tests__/alleleFormTestUtils';

vi.mock('../../../../service/SearchService');
vi.mock('../../../../service/useVocabularyTermSetService');
vi.mock('../../../../service/useControlledVocabularyService');

// The note pop-up constructs a ValidationService at render and validates its note on Keep Edits.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const NOTE_TYPE_SET = 'allele_genomic_entity_association_note_type';
const VALID = { isSuccess: true, isError: false, data: {} };

const association = (geneId, relatedNote = null) => ({
	id: geneId,
	relation: termSetTermsFor('allele_gene_relation')[0],
	alleleGeneAssociationObject: { id: geneId, primaryExternalId: `WB:WBGene${geneId}` },
	relatedNote,
	evidenceCode: null,
	evidence: [],
});

const firstNote = () => ({
	id: 71,
	noteType: termSetTermsFor(NOTE_TYPE_SET)[0],
	freeText: 'first note',
	internal: false,
});

/** Renders the section against a stand-in for its sub-resource that keeps rows and errors in state. */
const renderForm = () => {
	const handle = {};
	const Harness = () => {
		const [rows, setRows] = useState(() => prepareGeneAssociations([association(31, firstNote()), association(32)]));
		const [errorMessages, setErrorMessages] = useState({});
		handle.rows = rows;
		const alleleGeneAssociations = {
			label: 'gene associations',
			rows,
			setRows,
			errorMessages,
			setErrorMessages,
			isLoading: false,
			loadError: null,
			isSaving: false,
			isDirty: false,
			save: vi.fn(),
		};
		return (
			<SubResourcesProvider value={{ alleleGeneAssociations }}>
				<AlleleGeneAssociationsForm />
			</SubResourcesProvider>
		);
	};
	const result = renderWithClient(<Harness />);
	return { ...result, getRows: () => handle.rows };
};

const dialog = () => document.querySelector('.p-dialog');

const dialogRows = () =>
	[...dialog().querySelectorAll('tbody tr')].filter((row) => row.querySelectorAll('td').length > 1);

const getButton = (container, label) => {
	const button = [...container.querySelectorAll('button')].find((candidate) => candidate.textContent.trim() === label);
	if (!button) throw new Error(`No button labelled "${label}"`);
	return button;
};

/** Opens the note pop-up on a row and waits for it to hold that row's note, or the default new note. */
const openNote = async (container, rowIndex) => {
	fireEvent.click(getCellByHeader(container, 'Note', rowIndex).querySelector('button'));
	await waitFor(() => {
		expect(document.querySelector('.p-dialog-enter-done')).not.toBeNull();
		expect(dialogRows()).toHaveLength(1);
	});
};

const keepEdits = () => fireEvent.click(getButton(dialog(), 'Keep Edits'));

const dropdownLabel = (container, header, rowIndex = 0) =>
	getCellByHeader(container, header, rowIndex).querySelector('.p-dropdown-label').textContent.trim();

describe('the allele gene associations note pop-up', () => {
	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue(VALID);
	});

	it("opens with the row's note", async () => {
		const { container } = renderForm();

		await openNote(container, 0);

		expect(dropdownLabel(dialog(), 'Note Type')).toBe(`${NOTE_TYPE_SET}_alpha`);
		expect(getCellByHeader(dialog(), 'Text').querySelector('textarea')).toHaveValue('first note');
		expect(dropdownLabel(dialog(), 'Internal')).toBe('false');
		expect(getButton(dialog(), 'New Note')).toBeDisabled();
	});

	it("stores the edited note, validated as a note, on its row and marks the row's note as pending", async () => {
		const { container, getRows } = renderForm();

		await openNote(container, 0);
		selectInColumn(dialog(), 'Note Type', `${NOTE_TYPE_SET}_beta`);
		typeInColumn(dialog(), 'Text', 'first note edited');
		selectInColumn(dialog(), 'Internal', 'true');
		keepEdits();

		await waitFor(() => expect(getRows()[0].relatedNote.freeText).toBe('first note edited'));
		expect(getRows()[0].relatedNote).toMatchObject({
			id: 71,
			noteType: termSetTermsFor(NOTE_TYPE_SET)[1],
			freeText: 'first note edited',
			internal: true,
		});
		expect(getRows()[1].relatedNote).toBeNull();
		expect(validate).toHaveBeenCalledWith('note', expect.objectContaining({ id: 71, freeText: 'first note edited' }));
		expect(getCellByHeader(container, 'Note', 0).textContent).toContain('Pending Edits!');
		expect(getCellByHeader(container, 'Note', 1).textContent).not.toContain('Pending Edits!');
	});

	it('stores a new note written on a row that had none', async () => {
		const { container, getRows } = renderForm();

		await openNote(container, 1);
		typeInColumn(dialog(), 'Text', 'second note');
		keepEdits();

		await waitFor(() => expect(getRows()[1].relatedNote).toBeTruthy());
		expect(getRows()[1].relatedNote).toMatchObject({ noteType: { name: 'comment' }, freeText: 'second note' });
		expect(getRows()[0].relatedNote).toMatchObject(firstNote());
	});

	it('replaces a deleted note with a new one', async () => {
		const { container, getRows } = renderForm();

		await openNote(container, 0);
		clickDeleteInRow(dialog(), 0);
		await waitFor(() => expect(dialogRows()).toHaveLength(0));
		expect(getButton(dialog(), 'New Note')).not.toBeDisabled();
		fireEvent.click(getButton(dialog(), 'New Note'));
		await waitFor(() => expect(dialogRows()).toHaveLength(1));
		typeInColumn(dialog(), 'Text', 'replacement note');
		keepEdits();

		await waitFor(() => expect(getRows()[0].relatedNote?.freeText).toBe('replacement note'));
		expect(getRows()[0].relatedNote).not.toHaveProperty('id');
	});

	it.each([
		['with field errors', { freeText: 'text is bad' }],
		['without saying why', undefined],
	])('keeps the edits back and says so when the note fails validation %s', async (_description, data) => {
		validate.mockResolvedValue({ isSuccess: false, isError: true, data });
		const { container, getRows } = renderForm();

		await openNote(container, 0);
		typeInColumn(dialog(), 'Text', 'first note edited');
		keepEdits();

		await waitFor(() => expect(validate).toHaveBeenCalled());
		// Lets the validation result settle; a passing note would be stored by now.
		await act(() => new Promise((resolve) => setTimeout(resolve, 50)));
		expect(getRows()[0].relatedNote.freeText).toBe('first note');
		expect(dialogRows()).toHaveLength(1);
		expect(getCellByHeader(container, 'Note', 0).textContent).not.toContain('Pending Edits!');
		expect(document.body.textContent).toContain('Please fix validation errors before saving');
	});

	it.each([
		['Text', 'freeText'],
		['Note Type', 'noteType'],
		['Internal', 'internal'],
	])('shows a validation error on the %s cell, with invalid styling', async (header, field) => {
		validate.mockResolvedValue({ isSuccess: false, isError: true, data: { [field]: `${field} is bad` } });
		const { container } = renderForm();

		await openNote(container, 0);
		keepEdits();

		await waitFor(() => expect(getCellByHeader(dialog(), header).textContent).toContain(`${field} is bad`));
		expect(getCellByHeader(dialog(), header).querySelector('.p-invalid')).not.toBeNull();
		const otherHeaders = ['Text', 'Note Type', 'Internal'].filter((other) => other !== header);
		otherHeaders.forEach((other) => expect(getCellByHeader(dialog(), other).textContent).not.toContain('is bad'));
	});
});
