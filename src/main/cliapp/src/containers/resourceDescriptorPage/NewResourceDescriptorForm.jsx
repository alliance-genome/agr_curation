import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { classNames } from 'primereact/utils';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ResourceDescriptorService } from '../../service/ResourceDescriptorService';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

const emptyResourceDescriptor = {
	prefix: '',
	name: '',
	synonyms: [],
	idPattern: '',
	idExample: '',
	defaultUrlTemplate: '',
	internal: false,
};

export const NewResourceDescriptorForm = ({
	newResourceDescriptorDialog,
	setNewResourceDescriptorDialog,
	setNewResourceDescriptor,
}) => {
	const queryClient = useQueryClient();
	const [resourceDescriptor, setResourceDescriptor] = useState({ ...emptyResourceDescriptor });
	const [synonymsInput, setSynonymsInput] = useState('');
	const [submitted, setSubmitted] = useState(false);
	const toast_success = useRef(null);
	const toast_error = useRef(null);

	let resourceDescriptorService = null;

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	const getService = () => {
		if (!resourceDescriptorService) {
			resourceDescriptorService = new ResourceDescriptorService();
		}
		return resourceDescriptorService;
	};

	const mutation = useMutation({
		mutationFn: (newResourceDescriptorPayload) => {
			return getService().createResourceDescriptor(newResourceDescriptorPayload);
		},
	});

	const onTextChange = (event, field) => {
		const value = event.target?.value ?? '';
		setResourceDescriptor((prev) => ({ ...prev, [field]: value }));
	};

	const onSynonymsChange = (event) => {
		const raw = event.target?.value ?? '';
		setSynonymsInput(raw);
		const parsed = raw
			? raw
					.split(',')
					.map((entry) => entry.trim())
					.filter((entry) => entry.length > 0)
			: [];
		setResourceDescriptor((prev) => ({ ...prev, synonyms: parsed }));
	};

	const onInternalChange = (value) => {
		if (value === undefined || value === null || value === '') return;
		const parsed = typeof value === 'string' ? JSON.parse(value) : value;
		setResourceDescriptor((prev) => ({ ...prev, internal: parsed }));
	};

	const hideDialog = () => {
		setNewResourceDescriptorDialog(false);
		setSubmitted(false);
		setResourceDescriptor({ ...emptyResourceDescriptor });
		setSynonymsInput('');
	};

	const saveResourceDescriptor = (event) => {
		event.preventDefault();
		setSubmitted(true);
		if (
			resourceDescriptor.prefix &&
			resourceDescriptor.prefix.trim() &&
			resourceDescriptor.name &&
			resourceDescriptor.name.trim() &&
			resourceDescriptor.defaultUrlTemplate &&
			resourceDescriptor.defaultUrlTemplate.trim()
		) {
			mutation.mutate(resourceDescriptor, {
				onSuccess: (data) => {
					if (setNewResourceDescriptor) {
						setNewResourceDescriptor(data.data.entity, queryClient);
					} else {
						queryClient.invalidateQueries({ queryKey: ['resourceDescriptors'] });
					}
					toast_success.current.show({
						severity: 'success',
						summary: 'Successful',
						detail: 'New Resource Descriptor Added',
					});
					setSubmitted(false);
					setNewResourceDescriptorDialog(false);
					setResourceDescriptor({ ...emptyResourceDescriptor });
					setSynonymsInput('');
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
				onClick={saveResourceDescriptor}
				disabled={mutation.isPending}
			/>
		</>
	);

	return (
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog
				visible={newResourceDescriptorDialog}
				style={{ width: '450px' }}
				header="New Resource Descriptor"
				modal
				className="p-fluid"
				footer={dialogFooter}
				onHide={hideDialog}
			>
				<ErrorBoundary>
					<div className="field">
						<label htmlFor="prefix">
							<font color="red">*</font>Prefix
						</label>
						<InputText
							id="prefix"
							value={resourceDescriptor.prefix}
							onChange={(e) => onTextChange(e, 'prefix')}
							required
							autoFocus
							className={classNames({ 'p-invalid': submitted && !resourceDescriptor.prefix })}
						/>
						{submitted && !resourceDescriptor.prefix && <small className="p-error">Prefix is required.</small>}
					</div>
					<div className="field">
						<label htmlFor="name">
							<font color="red">*</font>Name
						</label>
						<InputText
							id="name"
							value={resourceDescriptor.name}
							onChange={(e) => onTextChange(e, 'name')}
							required
							className={classNames({ 'p-invalid': submitted && !resourceDescriptor.name })}
						/>
						{submitted && !resourceDescriptor.name && <small className="p-error">Name is required.</small>}
					</div>
					<div className="field">
						<label htmlFor="synonyms">Synonyms (comma-separated)</label>
						<InputTextarea id="synonyms" value={synonymsInput} onChange={onSynonymsChange} rows={3} />
					</div>
					<div className="field">
						<label htmlFor="idPattern">ID Pattern</label>
						<InputText
							id="idPattern"
							value={resourceDescriptor.idPattern}
							onChange={(e) => onTextChange(e, 'idPattern')}
						/>
					</div>
					<div className="field">
						<label htmlFor="idExample">ID Example</label>
						<InputText
							id="idExample"
							value={resourceDescriptor.idExample}
							onChange={(e) => onTextChange(e, 'idExample')}
						/>
					</div>
					<div className="field">
						<label htmlFor="defaultUrlTemplate">
							<font color="red">*</font>Default URL Template
						</label>
						<InputText
							id="defaultUrlTemplate"
							value={resourceDescriptor.defaultUrlTemplate}
							onChange={(e) => onTextChange(e, 'defaultUrlTemplate')}
							required
							className={classNames({ 'p-invalid': submitted && !resourceDescriptor.defaultUrlTemplate })}
						/>
						{submitted && !resourceDescriptor.defaultUrlTemplate && (
							<small className="p-error">Default URL Template is required.</small>
						)}
					</div>
					<div className="field">
						<label htmlFor="internal">Internal</label>
						<Dropdown
							id="internal"
							value={
								resourceDescriptor.internal === null || resourceDescriptor.internal === undefined
									? null
									: String(resourceDescriptor.internal)
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
