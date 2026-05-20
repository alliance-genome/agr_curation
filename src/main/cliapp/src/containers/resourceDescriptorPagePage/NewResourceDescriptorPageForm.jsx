import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { classNames } from 'primereact/utils';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ResourceDescriptorPageService } from '../../service/ResourceDescriptorPageService';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { AutocompleteFormEditor } from '../../components/Editors/autocomplete/base/AutocompleteFormEditor';
import { resourceDescriptorSearch } from '../../components/Editors/autocomplete/resourceDescriptor/utils';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

const emptyResourceDescriptorPage = {
	resourceDescriptor: null,
	name: '',
	urlTemplate: '',
	pageDescription: '',
	internal: false,
};

export const NewResourceDescriptorPageForm = ({
	newResourceDescriptorPageDialog,
	setNewResourceDescriptorPageDialog,
	setNewResourceDescriptorPage,
}) => {
	const queryClient = useQueryClient();
	const [resourceDescriptorPage, setResourceDescriptorPage] = useState({ ...emptyResourceDescriptorPage });
	const [submitted, setSubmitted] = useState(false);
	const toast_success = useRef(null);
	const toast_error = useRef(null);

	let resourceDescriptorPageService = null;

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

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

	const onTextChange = (event, field) => {
		const value = event.target?.value ?? '';
		setResourceDescriptorPage((prev) => ({ ...prev, [field]: value }));
	};

	const onResourceDescriptorChange = (event) => {
		const value = event.target?.value;
		const resolved = value && typeof value === 'object' ? value : null;
		setResourceDescriptorPage((prev) => ({ ...prev, resourceDescriptor: resolved }));
	};

	const onInternalChange = (value) => {
		if (value === undefined || value === null || value === '') return;
		const parsed = typeof value === 'string' ? JSON.parse(value) : value;
		setResourceDescriptorPage((prev) => ({ ...prev, internal: parsed }));
	};

	const hideDialog = () => {
		setNewResourceDescriptorPageDialog(false);
		setSubmitted(false);
		setResourceDescriptorPage({ ...emptyResourceDescriptorPage });
	};

	const saveResourceDescriptorPage = (event) => {
		event.preventDefault();
		setSubmitted(true);
		if (
			resourceDescriptorPage.resourceDescriptor?.id &&
			resourceDescriptorPage.name &&
			resourceDescriptorPage.name.trim() &&
			resourceDescriptorPage.urlTemplate &&
			resourceDescriptorPage.urlTemplate.trim()
		) {
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
					setSubmitted(false);
					setNewResourceDescriptorPageDialog(false);
					setResourceDescriptorPage({ ...emptyResourceDescriptorPage });
				},
				onError: (error) => {
					const fieldErrors = error.response?.data?.errorMessages;
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
					<div className="field">
						<label htmlFor="resourceDescriptor">
							<font color="red">*</font>Resource Descriptor
						</label>
						<AutocompleteFormEditor
							name="resourceDescriptor"
							fieldName="resourceDescriptor"
							subField="prefix"
							initialValue={resourceDescriptorPage.resourceDescriptor}
							search={resourceDescriptorSearch}
							onValueChangeHandler={onResourceDescriptorChange}
							valueDisplay={(item) => (
								<div>
									{item.prefix} ({item.name})
								</div>
							)}
							classNames={classNames({
								'p-invalid': submitted && !resourceDescriptorPage.resourceDescriptor?.id,
							})}
						/>
						{submitted && !resourceDescriptorPage.resourceDescriptor?.id && (
							<small className="p-error">Resource Descriptor is required.</small>
						)}
					</div>
					<div className="field">
						<label htmlFor="name">
							<font color="red">*</font>Name
						</label>
						<InputText
							id="name"
							value={resourceDescriptorPage.name}
							onChange={(e) => onTextChange(e, 'name')}
							required
							className={classNames({ 'p-invalid': submitted && !resourceDescriptorPage.name })}
						/>
						{submitted && !resourceDescriptorPage.name && <small className="p-error">Name is required.</small>}
					</div>
					<div className="field">
						<label htmlFor="urlTemplate">
							<font color="red">*</font>URL Template
						</label>
						<InputText
							id="urlTemplate"
							value={resourceDescriptorPage.urlTemplate}
							onChange={(e) => onTextChange(e, 'urlTemplate')}
							required
							className={classNames({ 'p-invalid': submitted && !resourceDescriptorPage.urlTemplate })}
						/>
						{submitted && !resourceDescriptorPage.urlTemplate && (
							<small className="p-error">URL Template is required.</small>
						)}
					</div>
					<div className="field">
						<label htmlFor="pageDescription">Page Description</label>
						<InputTextarea
							id="pageDescription"
							value={resourceDescriptorPage.pageDescription}
							onChange={(e) => onTextChange(e, 'pageDescription')}
							rows={3}
						/>
					</div>
					<div className="field">
						<label htmlFor="internal">Internal</label>
						<Dropdown
							id="internal"
							value={
								resourceDescriptorPage.internal === null || resourceDescriptorPage.internal === undefined
									? null
									: String(resourceDescriptorPage.internal)
							}
							options={booleanTerms?.terms || []}
							optionLabel="text"
							optionValue="name"
							onChange={(e) => onInternalChange(e.target?.value)}
						/>
					</div>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
