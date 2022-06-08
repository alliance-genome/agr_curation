import React, { useRef, useState, useEffect } from 'react';
import { Dropdown } from "primereact/dropdown";
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../service/DataLoadService';
import { InputText } from 'primereact/inputtext';
import { FMSForm } from './FMSForm';
import { URLForm } from './URLForm';
import { ManualForm } from './ManualForm';
import { useOktaAuth } from '@okta/okta-react';

export const NewBulkLoadForm = ({ bulkLoadDialog, setBulkLoadDialog, groups, newBulkLoad, bulkLoadDispatch, disableFormFields, setDisableFormFields, dataLoadService }) => {

	const { authState } = useOktaAuth();

	const queryClient = useQueryClient();
	const hideFMS = useRef(true);
	const hideURL = useRef(true);
	const hideManual = useRef(true);
	const hideOntology = useRef(true);

	const [backendBulkLoadTypes, setBackendLoadTypes] = useState();

	const mutation = useMutation(bulkLoad => {
		if (bulkLoad.id) {
			return getService().updateLoad(bulkLoad);
		} else {
			return getService().createLoad(bulkLoad);
		}
	});

	useEffect(() => {
		hideFMS.current = true;
		hideURL.current = true;
		hideManual.current = true;
		showLoadTypeForm(newBulkLoad.type);
		hideOntology.current = true;
		showLoadTypeForm(newBulkLoad.backendBulkLoadType);
		setBackendLoadTypes(dataLoadService.getBackendBulkLoadTypes(newBulkLoad.type));
	}, [newBulkLoad, dataLoadService]);

	const showLoadTypeForm = (value) => {
		switch (value) {
			case 'BulkFMSLoad':
				hideFMS.current = false;
				break;
			case 'BulkURLLoad':
				hideURL.current = false;
				break;
			case 'BulkManualLoad':
				hideManual.current = false;
				break;
			case 'ONTOLOGY':
				hideOntology.current = false;
				break;
			default:
				break;
		}
	};


	const onChange = (e) => {
		if (e.target.name === "scheduleActive" || e.target.name === "group") {
			bulkLoadDispatch({
				field: e.target.name,
				value: e.value
			});
		} else {
			bulkLoadDispatch({
				field: e.target.name,
				value: e.target.value
			});
		}
	};

	const getService = () => {
		if(!dataLoadService) {
			dataLoadService = new DataLoadService(authState);
		}
		return dataLoadService;
	}

	const hideDialog = () => {
		bulkLoadDispatch({ type: "RESET" });
		setBulkLoadDialog(false);
		hideFMS.current = true;
		hideURL.current = true;
		hideManual.current = true;
		setDisableFormFields(false);
	};

	const handleSubmit = (event) => {
		event.preventDefault();

		mutation.mutate(newBulkLoad, {
			onSuccess: () => {
				queryClient.invalidateQueries('bulkloadtable');
				bulkLoadDispatch({ type: "RESET" });
				hideFMS.current = true;
				hideURL.current = true;
				hideManual.current = true;
				setBulkLoadDialog(false);
				setDisableFormFields(false);
			},
			onError: () => {
				// lookup group and set 
			}
		});
	};

	const newBulkLoadDialogFooter = (
		<>
			<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
			<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
		</>
	);


	return (
		<Dialog visible={bulkLoadDialog} style={{ width: '450px' }} header="Add Bulk Load" modal className="p-fluid" footer={newBulkLoadDialogFooter} onHide={hideDialog} resizeable >
			<div className='p-justify-center'>
				<form>

					<div className="field">
						<label htmlFor="name">Name</label>
						<InputText
							id="name"
							name="name"
							placeholder={"Name"}
							value={newBulkLoad.name}
							onChange={onChange}
						/>
					</div>

					<div className="field">
						<label htmlFor="group">Group Name</label>
						<Dropdown
							id="group"
							options={groups}
							value={newBulkLoad.group}
							onChange={onChange}
							placeholder={"Select Group"}
							className='p-col-12'
							name='group'
							optionLabel='name'
							optionValue='id'
						/>
					</div>

					<div className="field">
						<label htmlFor="type">Load Type</label>
						<Dropdown
							id="type"
							value={newBulkLoad.type}
							options={getService().getLoadTypes()}
							onChange={onChange}
							placeholder={"Select Load Type"}
							className='p-col-12'
							name='type'
							disabled={disableFormFields}
						/>
					</div>

					<div className="field">
						<label htmlFor="fileExtension">File Extension</label>
						<InputText
							id="bulkLoadFileExtension"
							value={newBulkLoad.fileExtension}
							onChange={onChange}
							placeholder={"Select Bulk Load File Extension"}
							name='fileExtension'
							disabled={disableFormFields}
						/>
					</div>
					
					<div className="field">
						<label htmlFor="backendBulkLoadType">Backend Bulk Load Type</label>
						<Dropdown
							id="backendBulkLoadType"
							value={newBulkLoad.backendBulkLoadType}
							options={backendBulkLoadTypes}
							onChange={onChange}
							placeholder={"Select Backend Bulk Load Type"}
							className='p-col-12'
							name='backendBulkLoadType'
							disabled={disableFormFields}
						/>
					</div>
			
					<FMSForm
						hideFMS={hideFMS}
						newBulkLoad={newBulkLoad}
						onChange={onChange}
						disableFormFields={disableFormFields}
					/>

					<URLForm
						hideURL={hideURL}
						hideOntology={hideOntology}
						newBulkLoad={newBulkLoad}
						onChange={onChange}
						disableFormFields={disableFormFields}
					/>


					<ManualForm
						hideManual={hideManual}
						newBulkLoad={newBulkLoad}
						onChange={onChange}
						disableFormFields={disableFormFields}
					/>

				</form>
			</div>
		</Dialog>
	);
};
