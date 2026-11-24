import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { clickSave, resolvedCreate, selectInField, typeInField } from '../../../tools/jest/newEntityFormTestUtils';

const createVocabulary = vi.fn();
vi.mock('../../../service/VocabularyService', () => ({
	VocabularyService: class {
		createVocabulary = createVocabulary;
	},
}));

const { NewVocabularyForm } = await import('../NewVocabularyForm');

describe('<NewVocabularyForm />', () => {
	beforeEach(() => {
		createVocabulary.mockReset();
		createVocabulary.mockImplementation(() => resolvedCreate({ id: 5, name: 'Colours' }));
	});

	it('creates the vocabulary with the Obsolete value chosen', async () => {
		renderWithClient(
			<NewVocabularyForm newVocabularyDialog={true} setNewVocabularyDialog={vi.fn()} setNewVocabulary={vi.fn()} />
		);
		await screen.findByText('Vocabulary Name');
		typeInField('Vocabulary Name', 'Colours');
		typeInField('Vocabulary Label', 'colours');
		typeInField('Vocabulary Description', 'Colour names');
		selectInField('Obsolete', 'true');

		clickSave();

		await waitFor(() =>
			expect(createVocabulary).toHaveBeenCalledWith(
				expect.objectContaining({ name: 'Colours', vocabularyLabel: 'colours', obsolete: true })
			)
		);
	});
});
