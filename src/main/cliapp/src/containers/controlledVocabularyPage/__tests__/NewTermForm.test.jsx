import { screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { getField, selectInField } from '../../../tools/jest/newEntityFormTestUtils';
import { NewTermForm } from '../NewTermForm';

const renderForm = (newTerm, newTermDispatch = vi.fn()) => {
	renderWithClient(
		<NewTermForm
			newTermDialog={true}
			setNewTermDialog={vi.fn()}
			newTerm={newTerm}
			setNewTerm={vi.fn()}
			newTermDispatch={newTermDispatch}
			vocabularies={[]}
			vocabularyService={{ createTerm: vi.fn() }}
		/>
	);
	return newTermDispatch;
};

describe('<NewTermForm />', () => {
	it('sets Obsolete to the boolean chosen', async () => {
		const newTermDispatch = renderForm({ name: '' });
		await screen.findByText('Add Term');
		expect(getField('Obsolete')).toHaveTextContent('Select Obsolete');

		selectInField('Obsolete', 'true');

		expect(newTermDispatch).toHaveBeenCalledWith({ field: 'obsolete', value: true });
	});

	it('shows the chosen Obsolete value', async () => {
		renderForm({ name: '', obsolete: false });
		await screen.findByText('Add Term');

		expect(getField('Obsolete')).toHaveTextContent('false');
	});
});
