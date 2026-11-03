import { fireEvent } from '@testing-library/react';
import { RelatedNotesForm } from '../RelatedNotesForm';
import { renderNewAnnotationSubform } from './newAnnotationFormTestUtils';
import {
	clickDeleteInRow,
	getCellByHeader,
	selectInColumn,
	typeInColumn,
} from '../../allelesPage/__tests__/alleleFormTestUtils';
import { termSetTermsFor } from '../../../service/__mocks__/useVocabularyTermSetService';

vi.mock('../../../service/useVocabularyTermSetService');

const ERROR = { severity: 'error', message: 'Text is required' };

const renderForm = () =>
	renderNewAnnotationSubform((state, dispatch) => (
		<RelatedNotesForm
			dispatch={dispatch}
			relatedNotes={state.newAnnotation.relatedNotes}
			showRelatedNotes={state.showRelatedNotes}
			errorMessages={state.relatedNotesErrorMessages}
			editingRows={state.relatedNotesEditingRows}
		/>
	));

const notes = (getState) => getState().newAnnotation.relatedNotes;

const addNote = (getByRole) => fireEvent.click(getByRole('button', { name: 'Add Note' }));

describe('<RelatedNotesForm />', () => {
	it('saves each column on the row it was edited in', () => {
		const { container, getByRole, getState } = renderForm();
		addNote(getByRole);
		addNote(getByRole);
		const [, otherType] = termSetTermsFor('da_note_type');

		typeInColumn(container, 'Text', 'second note', 1);
		selectInColumn(container, 'Note Type', otherType.name, 1);
		selectInColumn(container, 'Internal', 'true', 1);

		expect(notes(getState)[1]).toMatchObject({ freeText: 'second note', noteType: otherType, internal: true });
		expect(notes(getState)[0]).toMatchObject({ freeText: '' });
	});

	// Deleting a row moves the rows after it up while their keys stay the same, and a note added
	// afterwards must not share a key with one that remains.
	it('keeps edits and errors on the right rows after a delete and another add', () => {
		const { container, getByRole, getState, dispatch } = renderForm();
		addNote(getByRole);
		addNote(getByRole);
		clickDeleteInRow(container, 0);
		addNote(getByRole);

		typeInColumn(container, 'Text', 'kept', 0);
		typeInColumn(container, 'Text', 'added', 1);
		const [kept, added] = notes(getState);
		dispatch({
			type: 'UPDATE_ERROR_MESSAGES',
			errorType: 'relatedNotesErrorMessages',
			errorMessages: { [added.dataKey]: { freeText: ERROR } },
		});

		expect(notes(getState).map((note) => note.freeText)).toEqual(['kept', 'added']);
		expect(kept.dataKey).not.toEqual(added.dataKey);
		expect(getCellByHeader(container, 'Text', 1).textContent).toContain('Text is required');
		expect(getCellByHeader(container, 'Text', 0).textContent).not.toContain('Text is required');
	});

	it('hides the table once its last note is deleted', () => {
		const { container, getByRole, getState } = renderForm();
		addNote(getByRole);

		clickDeleteInRow(container, 0);

		expect(notes(getState)).toEqual([]);
		expect(container.querySelector('table')).toBeNull();
	});
});
