import { useState } from 'react';
import { act, fireEvent, waitFor } from '@testing-library/react';
import { AlleleGeneAssociationsForm } from '../AlleleGeneAssociationsForm';
import { prepareGeneAssociations } from '../utils';
import { SubResourcesProvider } from '../../../../components/SubResourcesContext';
import { renderWithClient } from '../../../../tools/jest/utils';
import { SearchService } from '../../../../service/SearchService';
import { termSetTermsFor } from '../../../../service/__mocks__/useVocabularyTermSetService';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	removeFirstTokenInColumn,
	selectInColumn,
	typeInColumn,
} from '../../__tests__/alleleFormTestUtils';

vi.mock('../../../../service/SearchService');
vi.mock('../../../../service/useVocabularyTermSetService');
vi.mock('../../../../service/useControlledVocabularyService');

const EVIDENCE = [
	{ id: 1, curie: 'AGRKB:101' },
	{ id: 2, curie: 'AGRKB:102' },
];
const NEW_GENE = { id: 33, primaryExternalId: 'WB:WBGene33', symbol: 'unc-33' };
const NEW_EVIDENCE_CODE = { id: 41, curie: 'ECO:0000316', name: 'genetic interaction evidence' };
const ERROR = { severity: 'error', message: 'second row is bad' };
const RESULTS_BY_ENDPOINT = { gene: NEW_GENE, ecoterm: NEW_EVIDENCE_CODE };

const association = (geneId, note) => ({
	id: geneId,
	relation: termSetTermsFor('allele_gene_relation')[0],
	alleleGeneAssociationObject: { id: geneId, primaryExternalId: `WB:WBGene${geneId}` },
	relatedNote: note ? { freeText: note, noteType: { name: 'comment' }, internal: false } : null,
	evidenceCode: null,
	evidence: EVIDENCE,
});

/**
 * Renders the form against a stand-in for the gene associations sub-resource that keeps its rows and
 * errors in state, as the real one does, so tests can read back what a save would send.
 */
const renderForm = (associations = [association(31, 'first note'), association(32)]) => {
	const handle = {};
	const Harness = () => {
		const [rows, setRows] = useState(() => prepareGeneAssociations(associations));
		const [errorMessages, setErrorMessages] = useState({});
		handle.rows = rows;
		handle.setRows = setRows;
		handle.setErrorMessages = setErrorMessages;
		const alleleGeneAssociations = {
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
	return {
		...result,
		getRows: () => handle.rows,
		setRows: (update) => act(() => handle.setRows(update)),
		setErrorMessages: (errors) => act(() => handle.setErrorMessages(errors)),
	};
};

describe('<AlleleGeneAssociationsForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn((endpoint) =>
			Promise.resolve({ results: [RESULTS_BY_ENDPOINT[endpoint] ?? { id: 3, curie: 'AGRKB:103' }] })
		);
	});

	it('saves the whole relation term on the row it was picked in', () => {
		const { container, getRows } = renderForm();
		const [first, second] = termSetTermsFor('allele_gene_relation');

		selectInColumn(container, 'Relationship', second.name, 1);

		expect(getRows().map((row) => row.relation)).toEqual([first, second]);
	});

	it('saves a picked gene on the row it was picked in', async () => {
		const { container, getRows } = renderForm();

		await pickSuggestionInColumn(container, 'Gene', 'unc', /WB:WBGene33/, 1);

		expect(getRows().map((row) => row.alleleGeneAssociationObject.id)).toEqual([31, 33]);
	});

	it("shows a gene's next identifier when it has no primary external ID", () => {
		const { container } = renderForm([
			{ ...association(31), alleleGeneAssociationObject: { id: 31, modInternalId: 'WBGene00031' } },
		]);

		expect(getCellByHeader(container, 'Gene', 0).querySelector('input')).toHaveValue('WBGene00031');
	});

	it('holds typed gene text without an id', () => {
		const { container, getRows } = renderForm();

		typeInColumn(container, 'Gene', 'WB:WBGene99', 1);

		expect(getRows()[1].alleleGeneAssociationObject).toEqual({ primaryExternalId: 'WB:WBGene99' });
	});

	it('matches a stored relation to its term by id', () => {
		const [first] = termSetTermsFor('allele_gene_relation');
		const { container } = renderForm([{ ...association(31), relation: { ...first } }]);

		expect(getCellByHeader(container, 'Relationship', 0).querySelector('.p-dropdown-label')).toHaveTextContent(
			first.name
		);
	});

	it('holds typed evidence code text keyed by curie', () => {
		const { container, getRows } = renderForm();

		typeInColumn(container, 'Evidence Code', 'ECO:0000316', 1);

		expect(getRows()[1].evidenceCode).toEqual({ curie: 'ECO:0000316' });
	});

	it('saves a picked evidence code', async () => {
		const { container, getRows } = renderForm();

		await pickSuggestionInColumn(container, 'Evidence Code', 'ECO', /ECO:0000316/, 1);

		expect(getRows().map((row) => row.evidenceCode)).toEqual([null, NEW_EVIDENCE_CODE]);
	});

	it('saves the remaining evidence and refreshes the curies the evidence filter matches', () => {
		const { container, getRows } = renderForm();

		removeFirstTokenInColumn(container, 'Evidence', 1);

		expect(getRows()[1]).toMatchObject({ evidence: [EVIDENCE[1]], evidenceCurieSearchFilter: 'AGRKB:102' });
		expect(getRows()[0].evidence).toEqual(EVIDENCE);
	});

	it("labels each row's note trigger with its note", () => {
		const { container } = renderForm();

		expect(getCellByHeader(container, 'Note', 0).textContent).toContain('first note');
		expect(getCellByHeader(container, 'Note', 1).textContent).toContain('Add Note');
	});

	it("opens the note dialog on the clicked row's note", async () => {
		const { container, baseElement } = renderForm();

		fireEvent.click(getCellByHeader(container, 'Note', 0).querySelector('button'));

		await waitFor(() => expect(baseElement.querySelector('.p-dialog textarea')).toHaveValue('first note'));
	});

	// The trigger reads the note from the rows, not from the row data PrimeReact captured.
	it("relabels a row's note trigger when its note changes", () => {
		const { container, setRows } = renderForm();

		setRows((rows) =>
			rows.map((row, index) => (index === 1 ? { ...row, relatedNote: { freeText: 'new note' } } : row))
		);

		expect(getCellByHeader(container, 'Note', 1).textContent).toContain('new note');
	});

	it('saves an edit on the row that moved up after the row above it is deleted', async () => {
		const { container, getRows } = renderForm();
		const secondKey = getRows()[1].dataKey;

		clickDeleteInRow(container, 0);
		await pickSuggestionInColumn(container, 'Gene', 'unc', /WB:WBGene33/, 0);

		expect(getRows().map((row) => [row.dataKey, row.alleleGeneAssociationObject.id])).toEqual([[secondKey, 33]]);
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getRows, setErrorMessages } = renderForm();

		setErrorMessages({ [getRows()[1].dataKey]: { evidenceCode: ERROR, relatedNote: ERROR } });

		expect(getCellByHeader(container, 'Evidence Code', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Note', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Evidence Code', 0).textContent).not.toContain('second row is bad');
	});
});
