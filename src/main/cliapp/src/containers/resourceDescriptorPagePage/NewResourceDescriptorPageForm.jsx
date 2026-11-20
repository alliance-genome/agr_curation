import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ResourceDescriptorPageService } from '../../service/ResourceDescriptorPageService';
import { FormField } from '../../components/Editors/fields/FormField';
import { DIALOG_FIELD_LAYOUT } from '../../components/Editors/fields/dialogFieldLayout';
import { TextInput } from '../../components/Editors/widgets/TextInput';
import { TextArea } from '../../components/Editors/widgets/TextArea';
import { BooleanSelect } from '../../components/Editors/widgets/BooleanSelect';
import { SingleAutocomplete } from '../../components/Editors/widgets/SingleAutocomplete';
import {
	resourceDescriptorSearch,
	resourceDescriptorSearchConfig,
} from '../../components/Editors/autocomplete/resourceDescriptor/utils';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

const emptyResourceDescriptorPage = {
	resourceDescriptor: null,
	name: '',
	urlTemplate: '',
	pageDescription: '',
	internal: false,
};

/**
 * The messages for the required fields left empty: a resource descriptor must be picked from the suggestions, and
 * the name and URL template must hold more than spaces.
 *
 * @param {object} resourceDescriptorPage - the page being created
 * @returns {object} messages keyed by field, empty when every required field is filled
 */
const findMissingRequiredFields = (resourceDescriptorPage) => {
	const missing = {};
	if (!resourceDescriptorPage.resourceDescriptor?.id) missing.resourceDescriptor = 'Resource Descriptor is required.';
	if (!resourceDescriptorPage.name?.trim()) missing.name = 'Name is required.';
	if (!resourceDescriptorPage.urlTemplate?.trim()) missing.urlTemplate = 'URL Template is required.';
	return missing;
};

/**
 * The New Resource Descriptor Page dialog on the Resource Descriptor Pages table: resource descriptor, name, URL
 * template, page description and internal. Save checks the required fields first and says beside each empty one
 * that it is required; a rejected save shows each field's error beside it and lists them in a toast.
 *
 * @param {boolean} newResourceDescriptorPageDialog - whether the dialog is open
 * @param {(open: boolean) => void} setNewResourceDescriptorPageDialog - opens or closes the dialog
 * @param {(page: object, queryClient: object) => void} [setNewResourceDescriptorPage] - called with the created
 *   page; without it the pages query is refetched
 * @returns {JSX.Element}
 */
export const NewResourceDescriptorPageForm = ({
	newResourceDescriptorPageDialog,
	setNewResourceDescriptorPageDialog,
	setNewResourceDescriptorPage,
}) => {
	const queryClient = useQueryClient();
	const [resourceDescriptorPage, setResourceDescriptorPage] = useState({ ...emptyResourceDescriptorPage });
	const [submitted, setSubmitted] = useState(false);
	const [rejectedFieldErrors, setRejectedFieldErrors] = useState({});
	// Counts form resets, so a save answered after the form was reset leaves no field errors in it
	const formResetCount = useRef(0);
	const toast_success = useRef(null);
	const toast_error = useRef(null);

	let resourceDescriptorPageService = null;

	const getService = () => {
		if (!resourceDescriptorPageService) {
			resourceDescriptorPageService = new ResourceDescriptorPageService();
		}
		return resourceDescriptorPageService;
	};

	const mutation = useMutation({
		mutationFn: (newResourceDescriptorPagePayload) => {
			return getService().createResourceDescriptorPage(newResourceDescriptorPagePayload);
		},
	});

	const errors = submitted
		? { ...rejectedFieldErrors, ...findMissingRequiredFields(resourceDescriptorPage) }
		: rejectedFieldErrors;

	const resetForm = () => {
		formResetCount.current += 1;
		setSubmitted(false);
		setRejectedFieldErrors({});
		setResourceDescriptorPage({ ...emptyResourceDescriptorPage });
	};

	const hideDialog = () => {
		setNewResourceDescriptorPageDialog(false);
		resetForm();
	};

	const saveResourceDescriptorPage = (event) => {
		event.preventDefault();
		setSubmitted(true);
		setRejectedFieldErrors({});
		if (Object.keys(findMissingRequiredFields(resourceDescriptorPage)).length === 0) {
			const resetCountAtSave = formResetCount.current;
			mutation.mutate(resourceDescriptorPage, {
				onSuccess: (data) => {
					if (setNewResourceDescriptorPage) {
						setNewResourceDescriptorPage(data.data.entity, queryClient);
					} else {
						queryClient.invalidateQueries({ queryKey: ['resourceDescriptorPages'] });
					}
					toast_success.current.show({
						severity: 'success',
						summary: 'Successful',
						detail: 'New Resource Descriptor Page Added',
					});
					setNewResourceDescriptorPageDialog(false);
					resetForm();
				},
				onError: (error) => {
					const fieldErrors = error.response?.data?.errorMessages;
					if (resetCountAtSave === formResetCount.current) setRejectedFieldErrors(fieldErrors ?? {});
					const fieldDetail =
						fieldErrors && Object.keys(fieldErrors).length > 0
							? Object.entries(fieldErrors)
									.map(([field, message]) => `${field}: ${message}`)
									.join('; ')
							: null;
					const summary = error.response?.data?.errorMessage || 'Page error: ';
					toast_error.current.show([
						{
							life: 7000,
							severity: 'error',
							summary,
							detail: fieldDetail || error.message,
							sticky: false,
						},
					]);
				},
			});
		}
	};

	const fieldProps = (field) => ({
		name: field,
		value: resourceDescriptorPage[field],
		onChange: (value) => setResourceDescriptorPage((previous) => ({ ...previous, [field]: value })),
		errors,
		...DIALOG_FIELD_LAYOUT,
	});

	const dialogFooter = (
		<>
			<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
			<Button
				label="Save"
				icon="pi pi-check"
				className="p-button-text"
				onClick={saveResourceDescriptorPage}
				disabled={mutation.isPending}
			/>
		</>
	);

	return (
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog
				visible={newResourceDescriptorPageDialog}
				style={{ width: '450px' }}
				header="New Resource Descriptor Page"
				modal
				className="p-fluid"
				footer={dialogFooter}
				onHide={hideDialog}
			>
				<ErrorBoundary>
					<FormField label="Resource Descriptor" required {...fieldProps('resourceDescriptor')}>
						{(binding) => (
							<SingleAutocomplete
								{...binding}
								search={resourceDescriptorSearch}
								subField="prefix"
								valueDisplay={resourceDescriptorSearchConfig.valueDisplay}
							/>
						)}
					</FormField>
					<FormField label="Name" required {...fieldProps('name')}>
						{(binding) => <TextInput {...binding} />}
					</FormField>
					<FormField label="URL Template" required {...fieldProps('urlTemplate')}>
						{(binding) => <TextInput {...binding} />}
					</FormField>
					<FormField label="Page Description" {...fieldProps('pageDescription')}>
						{(binding) => <TextArea {...binding} rows={3} />}
					</FormField>
					<FormField label="Internal" {...fieldProps('internal')}>
						{(binding) => <BooleanSelect {...binding} />}
					</FormField>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
