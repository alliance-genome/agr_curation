import { useEffect } from 'react';
import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';
import {
	clickSave,
	getField,
	pickSuggestionInField,
	rejectedCreate,
	resolvedCreate,
	typeInField,
} from '../../../tools/jest/newEntityFormTestUtils';

const createVocabularyTermSet = vi.fn();
const VOCABULARY = { id: 3, name: 'allele_note_type' };
const TERM = { id: 31, name: 'comment' };
const RESULTS_BY_ENDPOINT = {
	[Endpoints.Vocabulary.VOCABULARY]: [VOCABULARY],
	[Endpoints.Vocabulary.TERM]: [TERM],
};
const search = vi.fn((endpoint) => {
	const results = RESULTS_BY_ENDPOINT[endpoint] ?? [];
	return Promise.resolve({ results, totalResults: results.length });
});

vi.mock('../../../service/VocabularyTermSetService', () => ({
	VocabularyTermSetService: class {
		createVocabularyTermSet = createVocabularyTermSet;
	},
}));

// Each autocomplete gets its own results by endpoint, so a search sent to the wrong endpoint finds nothing.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

const { NewVocabularyTermSetForm } = await import('../NewVocabularyTermSetForm');
const { useNewVocabularyTermSetReducer } = await import('../useNewVocabularyTermSetReducer');

const renderForm = (onCreated = vi.fn()) => {
	const Harness = () => {
		const { newVocabularyTermSetState, newVocabularyTermSetDispatch } = useNewVocabularyTermSetReducer();
		useEffect(() => newVocabularyTermSetDispatch({ type: 'OPEN_DIALOG' }), [newVocabularyTermSetDispatch]);
		return (
			<NewVocabularyTermSetForm
				newVocabularyTermSetState={newVocabularyTermSetState}
				newVocabularyTermSetDispatch={newVocabularyTermSetDispatch}
				setNewVocabularyTermSet={onCreated}
			/>
		);
	};
	return renderWithClient(<Harness />);
};

const waitForForm = () => screen.findByText('Vocabulary Terms');
const memberTermsInput = () => getField('Vocabulary Terms').querySelector('input');

describe('<NewVocabularyTermSetForm />', () => {
	beforeEach(() => {
		createVocabularyTermSet.mockReset();
		search.mockClear();
	});

	it('creates the term set from the entered values, picked vocabulary and terms, then closes', async () => {
		const created = { id: 99, name: 'Note types' };
		createVocabularyTermSet.mockImplementation(() => resolvedCreate(created));
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		typeInField('Name', 'Note types');
		typeInField('Label', 'note_types');
		typeInField('Description', 'The note types');
		await pickSuggestionInField('Vocabulary', 'allele', /allele_note_type/);
		await pickSuggestionInField('Vocabulary Terms', 'com', /comment/);
		clickSave();

		await waitFor(() => expect(onCreated).toHaveBeenCalledWith(created, expect.anything()));
		expect(await screen.findByText('Successful')).toBeInTheDocument();
		expect(createVocabularyTermSet).toHaveBeenCalledWith(
			expect.objectContaining({
				name: 'Note types',
				vocabularyLabel: 'note_types',
				vocabularyTermSetDescription: 'The note types',
				vocabularyTermSetVocabulary: VOCABULARY,
				memberTerms: [TERM],
			})
		);
		await waitFor(() => expect(screen.queryByText('Vocabulary Terms')).not.toBeInTheDocument());
	});

	it('searches only the picked vocabulary for terms', async () => {
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Vocabulary', 'allele', /allele_note_type/);
		await pickSuggestionInField('Vocabulary Terms', 'com', /comment/);

		expect(search).toHaveBeenCalledWith(
			Endpoints.Vocabulary.TERM,
			15,
			0,
			[],
			expect.objectContaining({ vocabularyFilter: { 'vocabulary.name': { queryString: VOCABULARY.name } } })
		);
	});

	it('keeps Vocabulary Terms disabled until a vocabulary is picked', async () => {
		renderForm();
		await waitForForm();

		expect(memberTermsInput()).toBeDisabled();
		await pickSuggestionInField('Vocabulary', 'allele', /allele_note_type/);
		expect(memberTermsInput()).toBeEnabled();
	});

	it('disables Vocabulary Terms again when the picked vocabulary is edited', async () => {
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Vocabulary', 'allele', /allele_note_type/);
		typeInField('Vocabulary', 'allele_note');

		expect(memberTermsInput()).toBeDisabled();
	});

	it('keeps Vocabulary Terms disabled while the vocabulary is only typed', async () => {
		renderForm();
		await waitForForm();

		typeInField('Vocabulary', 'allele');

		expect(memberTermsInput()).toBeDisabled();
	});

	it('sends a typed vocabulary that was never picked as an unknown vocabulary', async () => {
		createVocabularyTermSet.mockImplementation(() => rejectedCreate({ vocabularyTermSetVocabulary: 'Invalid' }));
		renderForm();
		await waitForForm();

		typeInField('Vocabulary', 'no_such_vocabulary');
		clickSave();

		await waitFor(() => expect(createVocabularyTermSet).toHaveBeenCalled());
		expect(createVocabularyTermSet.mock.calls[0][0].vocabularyTermSetVocabulary).toEqual({
			name: 'no_such_vocabulary',
		});
		expect(await within(getField('Vocabulary')).findByText('Invalid')).toBeInTheDocument();
	});

	it('shows a rejected field error beside its field and stays open', async () => {
		createVocabularyTermSet.mockImplementation(() =>
			rejectedCreate({ name: 'Required field is empty' }, 'Unable to create term set')
		);
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		typeInField('Label', 'note_types');
		clickSave();

		expect(await within(getField('Name')).findByText('Required field is empty')).toBeInTheDocument();
		expect(within(getField('Label')).queryByText('Required field is empty')).not.toBeInTheDocument();
		expect(getField('Name').querySelector('input')).toHaveClass('p-invalid');
		expect(getField('Label').querySelector('input')).not.toHaveClass('p-invalid');
		expect(screen.getByText('Unable to create term set')).toBeInTheDocument();
		expect(onCreated).not.toHaveBeenCalled();
	});

	it('closes on Cancel without creating anything', async () => {
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		typeInField('Name', 'Note types');
		fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));

		await waitFor(() => expect(screen.queryByText('Vocabulary Terms')).not.toBeInTheDocument());
		expect(createVocabularyTermSet).not.toHaveBeenCalled();
		expect(onCreated).not.toHaveBeenCalled();
	});

	it('names the vocabulary and terms autocompletes', async () => {
		renderForm();
		await waitForForm();

		expect(screen.getByRole('combobox', { name: 'vocabularyTermSetVocabulary' })).toBeInTheDocument();
		expect(screen.getByRole('combobox', { name: 'memberTerms' })).toBeInTheDocument();
	});
});
