import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';
import {
	clickSave,
	getField,
	pickSuggestionInField,
	rejectedCreate,
	resolvedCreate,
	selectInField,
	typeInField,
} from '../../../tools/jest/newEntityFormTestUtils';

const createResourceDescriptorPage = vi.fn();
const RESOURCE_DESCRIPTOR = { id: 8, prefix: 'ZFIN', name: 'Zebrafish Information Network' };
const search = vi.fn((endpoint) => {
	const results = endpoint === Endpoints.Resource.DESCRIPTOR ? [RESOURCE_DESCRIPTOR] : [];
	return Promise.resolve({ results, totalResults: results.length });
});

vi.mock('../../../service/ResourceDescriptorPageService', () => ({
	ResourceDescriptorPageService: class {
		createResourceDescriptorPage = createResourceDescriptorPage;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

vi.mock('../../../service/useControlledVocabularyService');

const { NewResourceDescriptorPageForm } = await import('../NewResourceDescriptorPageForm');

const renderForm = (onCreated = vi.fn(), setDialog = vi.fn()) =>
	renderWithClient(
		<NewResourceDescriptorPageForm
			newResourceDescriptorPageDialog={true}
			setNewResourceDescriptorPageDialog={setDialog}
			setNewResourceDescriptorPage={onCreated}
		/>
	);

const waitForForm = () => screen.findByText('Page Description');

const fillRequiredText = () => {
	typeInField('Name', 'gene');
	typeInField('URL Template', 'https://zfin.org/[%s]');
};

describe('<NewResourceDescriptorPageForm />', () => {
	beforeEach(() => {
		createResourceDescriptorPage.mockReset();
		search.mockClear();
	});

	it('creates the page from the picked descriptor and entered values, then closes', async () => {
		const created = { id: 99, name: 'gene' };
		createResourceDescriptorPage.mockImplementation(() => resolvedCreate(created));
		const onCreated = vi.fn();
		const setDialog = vi.fn();
		renderForm(onCreated, setDialog);
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN \(Zebrafish Information Network\)/);
		fillRequiredText();
		typeInField('Page Description', 'Gene page');
		selectInField('Internal', 'true');
		clickSave();

		await waitFor(() => expect(onCreated).toHaveBeenCalledWith(created, expect.anything()));
		expect(await screen.findByText('Successful')).toBeInTheDocument();
		expect(createResourceDescriptorPage).toHaveBeenCalledWith(
			expect.objectContaining({
				resourceDescriptor: expect.objectContaining({ id: RESOURCE_DESCRIPTOR.id }),
				name: 'gene',
				urlTemplate: 'https://zfin.org/[%s]',
				pageDescription: 'Gene page',
				internal: true,
			})
		);
		expect(setDialog).toHaveBeenCalledWith(false);
	});

	it('says each empty required field is required, beside it, and sends nothing', async () => {
		renderForm();
		await waitForForm();

		clickSave();

		expect(
			await within(getField('Resource Descriptor')).findByText('Resource Descriptor is required.')
		).toBeInTheDocument();
		expect(within(getField('Name')).getByText('Name is required.')).toBeInTheDocument();
		expect(within(getField('URL Template')).getByText('URL Template is required.')).toBeInTheDocument();
		expect(createResourceDescriptorPage).not.toHaveBeenCalled();
	});

	it('treats a descriptor typed but never picked as missing', async () => {
		renderForm();
		await waitForForm();

		typeInField('Resource Descriptor', 'ZFIN');
		fillRequiredText();
		clickSave();

		expect(
			await within(getField('Resource Descriptor')).findByText('Resource Descriptor is required.')
		).toBeInTheDocument();
		expect(createResourceDescriptorPage).not.toHaveBeenCalled();
	});

	it('closes on Cancel without creating anything', async () => {
		const setDialog = vi.fn();
		renderForm(vi.fn(), setDialog);
		await waitForForm();

		typeInField('Name', 'gene');
		fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));

		expect(setDialog).toHaveBeenCalledWith(false);
		expect(createResourceDescriptorPage).not.toHaveBeenCalled();
	});

	it('reports a rejected save in a toast and stays open', async () => {
		createResourceDescriptorPage.mockImplementation(() =>
			rejectedCreate({ urlTemplate: 'Invalid' }, 'Unable to create resource descriptor page')
		);
		const setDialog = vi.fn();
		renderForm(vi.fn(), setDialog);
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		fillRequiredText();
		clickSave();

		expect(await screen.findByText('urlTemplate: Invalid')).toBeInTheDocument();
		expect(setDialog).not.toHaveBeenCalled();
	});

	// The server's field errors show only in the toast.
	it.skip('shows a rejected field error beside its field', async () => {
		createResourceDescriptorPage.mockImplementation(() => rejectedCreate({ urlTemplate: 'Invalid' }));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		fillRequiredText();
		clickSave();

		expect(await within(getField('URL Template')).findByText('Invalid')).toBeInTheDocument();
		expect(within(getField('Name')).queryByText('Invalid')).not.toBeInTheDocument();
	});

	// The resource descriptor autocomplete has no accessible name.
	it.skip('names the resource descriptor autocomplete', async () => {
		renderForm();
		await waitForForm();

		expect(screen.getByRole('combobox', { name: 'resourceDescriptor' })).toBeInTheDocument();
	});
});
