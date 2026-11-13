import { fireEvent, render, waitFor } from '@testing-library/react';
import { DatabaseStatusForm } from '../databaseStatus/DatabaseStatusForm';
import { DatabaseStatusEditDialog } from '../databaseStatus/DatabaseStatusEditDialog';
import { GermilineTransmissionStatusForm } from '../germlineTransmissionStatus/GermlineTransmissionStatusForm';
import { GermlineTransmissionStatusEditDialog } from '../germlineTransmissionStatus/GermlineTransmissionStatusEditDialog';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	renderAlleleForm,
	selectInColumn,
} from './alleleFormTestUtils';
import { processErrors } from '../../../utils/processErrors';
import { SearchService } from '../../../service/SearchService';
import { vocabularyTermsFor } from '../../../service/__mocks__/useControlledVocabularyService';
import '../../../tools/jest/setupTests';

vi.mock('../../../service/SearchService');
vi.mock('../../../service/useControlledVocabularyService');

// The pop-ups construct a ValidationService at render and call it on Keep Edits.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const EVIDENCE = { id: 1, curie: 'AGRKB:101' };
const NEW_EVIDENCE = { id: 3, curie: 'AGRKB:103' };

/**
 * The two allele status sections differ only in the status field, its vocabulary and their labels, so each case
 * below runs against both.
 */
const SECTIONS = [
	{
		name: 'Database Status',
		entityType: 'alleleDatabaseStatus',
		field: 'databaseStatus',
		vocabulary: 'allele_db_status',
		Form: DatabaseStatusForm,
		renderDialog: (originalStatuses, props) => (
			<DatabaseStatusEditDialog
				originalDatabaseStatusData={{ originalDatabaseStatuses: originalStatuses, ...props.data }}
				setOriginalDatabaseStatusData={props.setData}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={props.setErrorMessagesMainRow}
			/>
		),
	},
	{
		name: 'Germline Transmission Status',
		entityType: 'alleleGermlineTransmissionStatus',
		field: 'germlineTransmissionStatus',
		vocabulary: 'allele_germline_transmission_status',
		Form: GermilineTransmissionStatusForm,
		renderDialog: (originalStatuses, props) => (
			<GermlineTransmissionStatusEditDialog
				originalGermlineTransmissionStatusData={{
					originalGermlineTransmissionStatuses: originalStatuses,
					...props.data,
				}}
				setOriginalGermlineTransmissionStatusData={props.setData}
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

const dropdownLabel = (container, header) =>
	getCellByHeader(container, header).querySelector('.p-dropdown-label').textContent.trim();

describe.each(SECTIONS)('allele $name', (section) => {
	const term = (index) => vocabularyTermsFor(section.vocabulary)[index];
	const status = () => ({ [section.field]: term(0), internal: false, evidence: [EVIDENCE] });

	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_EVIDENCE] }));
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	describe('detail page section', () => {
		const renderSection = (value = status()) => renderAlleleForm(section.Form, { [section.entityType]: value });
		const saved = (getState) => getState().allele[section.entityType];

		it('adds an empty status, and offers no second one', () => {
			const { container, getState } = renderSection(null);

			fireEvent.click(getButton(container, `Add ${section.name}`));

			expect(saved(getState).internal).toBe(false);
			expect(saved(getState)[section.field] ?? null).toBeNull();
			expect(container.querySelectorAll('tbody tr')).toHaveLength(1);
			expect(getCellByHeader(container, section.name).querySelector('.p-dropdown')).not.toBeNull();
			expect(getButton(container, `Add ${section.name}`)).toBeDisabled();
		});

		it('saves the whole picked status term', () => {
			const { container, getState } = renderSection();

			selectInColumn(container, section.name, `${section.vocabulary}_beta`);

			expect(saved(getState)[section.field]).toEqual(term(1));
		});

		it('saves internal as a boolean', () => {
			const { container, getState } = renderSection();

			selectInColumn(container, 'Internal', 'true');

			expect(saved(getState).internal).toBe(true);
			expect(dropdownLabel(container, 'Internal')).toBe('true');
		});

		it('saves a picked evidence alongside the existing one', async () => {
			const { container, getState } = renderSection();

			await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/);

			expect(saved(getState).evidence).toEqual([EVIDENCE, NEW_EVIDENCE]);
		});

		it('removes the status on delete', () => {
			const { container, getState } = renderSection();

			clickDeleteInRow(container);

			expect(saved(getState)).toBeNull();
			expect(container.querySelector('tbody .p-dropdown')).toBeNull();
			expect(getButton(container, `Add ${section.name}`)).toBeEnabled();
		});

		it("shows a rejected save's error on the status cell", () => {
			const { container, getState, dispatch } = renderSection();

			processErrors(
				{ supplementalData: { errorMap: { [section.entityType]: { [section.field]: 'Not a valid entry' } } } },
				dispatch,
				getState().allele
			);

			expect(getCellByHeader(container, section.name).textContent).toContain('Not a valid entry');
			expect(getCellByHeader(container, 'Internal').textContent).not.toContain('Not a valid entry');
		});
	});

	describe('Alleles table pop-up', () => {
		const openDialog = async (originalStatuses = [status()]) => {
			const editorCallback = vi.fn();
			const setErrorMessagesMainRow = vi.fn();
			const setData = vi.fn();
			const result = render(
				section.renderDialog(originalStatuses, {
					data: { isInEdit: true, dialog: true, rowIndex: 0, mainRowProps: { editorCallback } },
					setData,
					setErrorMessagesMainRow,
				})
			);

			// The pop-up copies its rows in onShow, which runs as the open transition ends.
			await waitFor(() => expect(result.baseElement.querySelector('.p-dialog-enter-done')).not.toBeNull());

			return { ...result, editorCallback, setErrorMessagesMainRow };
		};

		const keepEdits = (baseElement) => fireEvent.click(getButton(baseElement, 'Keep Edits'));

		it('opens with the status', async () => {
			const { baseElement } = await openDialog();

			expect(dropdownLabel(baseElement, section.name)).toBe(`${section.vocabulary}_alpha`);
			expect(dropdownLabel(baseElement, 'Internal')).toBe('false');
			expect(getCellByHeader(baseElement, 'Evidence').textContent).toContain(EVIDENCE.curie);
		});

		it('opens empty for an allele with no status, ready for a new one', async () => {
			const { baseElement } = await openDialog([null]);

			expect(baseElement.querySelector('tbody .p-datatable-emptymessage')).not.toBeNull();

			fireEvent.click(getButton(baseElement, `New ${section.name}`));

			await waitFor(() => expect(baseElement.querySelectorAll('tbody tr td').length).toBeGreaterThan(1));
			expect(baseElement.querySelectorAll('tbody tr')).toHaveLength(1);
			expect(dropdownLabel(baseElement, 'Internal')).toBe('false');
		});

		it('hands back the edited status and marks the row as having pending edits', async () => {
			const { baseElement, editorCallback, setErrorMessagesMainRow } = await openDialog();

			selectInColumn(baseElement, section.name, `${section.vocabulary}_beta`);
			selectInColumn(baseElement, 'Internal', 'true');
			await pickSuggestionInColumn(baseElement, 'Evidence', 'AGRKB', /AGRKB:103/);
			keepEdits(baseElement);

			await waitFor(() => expect(editorCallback).toHaveBeenCalled());
			expect(editorCallback).toHaveBeenCalledWith({
				[section.field]: term(1),
				internal: true,
				evidence: [EVIDENCE, NEW_EVIDENCE],
			});
			expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
				0: { [section.entityType]: { severity: 'warn', message: 'Pending Edits!' } },
			});
		});

		it('hands back null once the status is deleted', async () => {
			const { baseElement, editorCallback } = await openDialog();

			clickDeleteInRow(baseElement);
			keepEdits(baseElement);

			await waitFor(() => expect(editorCallback).toHaveBeenCalledWith(null));
		});

		it('offers a new status only while there is none', async () => {
			const { baseElement } = await openDialog();

			expect(getButton(baseElement, `New ${section.name}`)).toBeDisabled();

			clickDeleteInRow(baseElement);

			await waitFor(() => expect(getButton(baseElement, `New ${section.name}`)).toBeEnabled());
			fireEvent.click(getButton(baseElement, `New ${section.name}`));

			await waitFor(() => expect(baseElement.querySelectorAll('tbody tr td').length).toBeGreaterThan(1));
			expect(dropdownLabel(baseElement, 'Internal')).toBe('false');
			expect(getCellByHeader(baseElement, 'Evidence').textContent).not.toContain(EVIDENCE.curie);
			expect(getButton(baseElement, `New ${section.name}`)).toBeDisabled();
		});

		it('shows a validation error on its cell and keeps the edits back', async () => {
			validate.mockResolvedValue({ isSuccess: false, isError: true, data: { [section.field]: 'Not a valid entry' } });
			const { baseElement, editorCallback } = await openDialog();

			keepEdits(baseElement);

			await waitFor(() =>
				expect(getCellByHeader(baseElement, section.name).textContent).toContain('Not a valid entry')
			);
			expect(getCellByHeader(baseElement, 'Internal').textContent).not.toContain('Not a valid entry');
			expect(editorCallback).not.toHaveBeenCalled();
		});
	});
});
