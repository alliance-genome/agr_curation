import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { clickSave, resolvedCreate, selectInField, typeInField } from '../../../tools/jest/newEntityFormTestUtils';

const createResourceDescriptor = vi.fn();
vi.mock('../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		createResourceDescriptor = createResourceDescriptor;
	},
}));

const { NewResourceDescriptorForm } = await import('../NewResourceDescriptorForm');

const renderForm = () =>
	renderWithClient(
		<NewResourceDescriptorForm
			newResourceDescriptorDialog={true}
			setNewResourceDescriptorDialog={vi.fn()}
			setNewResourceDescriptor={vi.fn()}
		/>
	);

const fillRequired = () => {
	typeInField('Prefix', 'ZFIN');
	typeInField('Name', 'Zebrafish Information Network');
	typeInField('Default URL Template', 'https://zfin.org/[%s]');
};

describe('<NewResourceDescriptorForm />', () => {
	beforeEach(() => {
		createResourceDescriptor.mockReset();
		createResourceDescriptor.mockImplementation(() => resolvedCreate({ id: 8, prefix: 'ZFIN' }));
	});

	it('creates the descriptor with the Internal value chosen', async () => {
		renderForm();
		await screen.findByText('ID Pattern');
		fillRequired();
		selectInField('Internal', 'true');

		clickSave();

		await waitFor(() =>
			expect(createResourceDescriptor).toHaveBeenCalledWith(expect.objectContaining({ prefix: 'ZFIN', internal: true }))
		);
	});

	it('creates the descriptor as not internal when Internal is left alone', async () => {
		renderForm();
		await screen.findByText('ID Pattern');
		fillRequired();

		clickSave();

		await waitFor(() =>
			expect(createResourceDescriptor).toHaveBeenCalledWith(expect.objectContaining({ internal: false }))
		);
	});
});
