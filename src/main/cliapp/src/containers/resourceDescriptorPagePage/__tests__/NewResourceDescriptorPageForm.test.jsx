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
				resourceDescriptor: RESOURCE_DESCRIPTOR,
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

	it('says a name or URL template of only spaces is required', async () => {
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		typeInField('Name', '   ');
		typeInField('URL Template', '  ');
		clickSave();

		expect(await within(getField('Name')).findByText('Name is required.')).toBeInTheDocument();
		expect(within(getField('URL Template')).getByText('URL Template is required.')).toBeInTheDocument();
		expect(createResourceDescriptorPage).not.toHaveBeenCalled();
	});

	it('clears a required message once the field is filled', async () => {
		renderForm();
		await waitForForm();

		clickSave();
		expect(await within(getField('Name')).findByText('Name is required.')).toBeInTheDocument();
		typeInField('Name', 'gene');

		expect(within(getField('Name')).queryByText('Name is required.')).not.toBeInTheDocument();
		expect(within(getField('URL Template')).getByText('URL Template is required.')).toBeInTheDocument();
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

	it('shows a rejected field error beside its field', async () => {
		createResourceDescriptorPage.mockImplementation(() => rejectedCreate({ urlTemplate: 'Invalid' }));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		fillRequiredText();
		clickSave();

		expect(await within(getField('URL Template')).findByText('Invalid')).toBeInTheDocument();
		expect(within(getField('Name')).queryByText('Invalid')).not.toBeInTheDocument();
	});

	it('clears a rejected field error when saving again, and sends Internal as false by default', async () => {
		createResourceDescriptorPage
			.mockImplementationOnce(() => rejectedCreate({ urlTemplate: 'Invalid' }))
			.mockImplementationOnce(() => new Promise(() => {}));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		fillRequiredText();
		clickSave();
		expect(await within(getField('URL Template')).findByText('Invalid')).toBeInTheDocument();
		clickSave();

		await waitFor(() => expect(within(getField('URL Template')).queryByText('Invalid')).not.toBeInTheDocument());
		expect(createResourceDescriptorPage).toHaveBeenLastCalledWith(expect.objectContaining({ internal: false }));
	});

	it('clears rejected field errors and required messages on Cancel', async () => {
		createResourceDescriptorPage.mockImplementation(() => rejectedCreate({ urlTemplate: 'Invalid' }));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		typeInField('URL Template', 'https://zfin.org/[%s]');
		typeInField('Name', 'gene');
		clickSave();
		expect(await within(getField('URL Template')).findByText('Invalid')).toBeInTheDocument();
		typeInField('Name', '');
		expect(within(getField('Name')).getByText('Name is required.')).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));

		await waitFor(() => expect(within(getField('URL Template')).queryByText('Invalid')).not.toBeInTheDocument());
		expect(within(getField('Name')).queryByText('Name is required.')).not.toBeInTheDocument();
	});

	it('leaves no field errors from a save rejected after Cancel', async () => {
		let rejectSave;
		createResourceDescriptorPage.mockImplementation(
			() =>
				new Promise((resolve, reject) => {
					rejectSave = reject;
				})
		);
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Resource Descriptor', 'ZF', /ZFIN/);
		fillRequiredText();
		clickSave();
		await waitFor(() => expect(createResourceDescriptorPage).toHaveBeenCalled());
		fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));
		rejectSave({
			response: { data: { errorMessage: 'Validation failed', errorMessages: { urlTemplate: 'Invalid' } } },
		});

		expect(await screen.findByText('urlTemplate: Invalid')).toBeInTheDocument();
		expect(within(getField('URL Template')).queryByText('Invalid')).not.toBeInTheDocument();
	});

	it('names the resource descriptor autocomplete', async () => {
		renderForm();
		await waitForForm();

		expect(screen.getByRole('combobox', { name: 'resourceDescriptor' })).toBeInTheDocument();
	});
});
