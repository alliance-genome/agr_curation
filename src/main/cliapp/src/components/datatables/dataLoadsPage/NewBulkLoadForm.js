import React, { useRef, useState, useEffect } from 'react';
import { Dropdown } from "primereact/dropdown";
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../../service/DataLoadService';
import { InputText } from 'primereact/inputtext';
import { FMSForm } from './FMSForm';
import { URLForm } from './URLForm';
import { ManualForm } from './ManualForm';


const dataLoadService = new DataLoadService();


export const NewBulkLoadForm = ({ bulkLoadDialog, setBulkLoadDialog, groups, newBulkLoad, bulkLoadDispatch }) => {

    const queryClient = useQueryClient();
    const hideFMS = useRef(true);
    const hideURL = useRef(true);
    const hideManual = useRef(true);
    const hideOntology = useRef(true);

    const [backendBulkLoadTypes, setBackendLoadTypes] = useState();
    const loadTypes = dataLoadService.getLoadTypes();

    const mutation = useMutation(bulkLoad => {
        if (bulkLoad.id) {
            return dataLoadService.updateLoad(bulkLoad);
        } else {
            return dataLoadService.createLoad(bulkLoad);
        }
    });

    useEffect(() => {
        setBackendLoadTypes(dataLoadService.getBackendBulkLoadTypes(newBulkLoad.type));
        hideFMS.current = true;
        hideURL.current = true;
        hideManual.current = true;
        showLoadTypeForm(newBulkLoad.type);
        hideOntology.current = true;
        showLoadTypeForm(newBulkLoad.backendBulkLoadType);
    }, [newBulkLoad]);

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

    const hideDialog = () => {
        bulkLoadDispatch({ type: "RESET" });
        setBulkLoadDialog(false);
        hideFMS.current = true;
        hideURL.current = true;
        hideManual.current = true;
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

                    <div className="p-field">
                        <label htmlFor="name">Name</label>
                        <InputText
                            id="name"
                            name="name"
                            placeholder={"Name"}
                            value={newBulkLoad.name}
                            onChange={onChange}
                        />
                    </div>

                    <div className="p-field">
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

                    <div className="p-field">
                        <label htmlFor="type">Load Type</label>
                        <Dropdown
                            id="type"
                            value={newBulkLoad.type}
                            options={loadTypes}
                            onChange={onChange}
                            placeholder={"Select Load Type"}
                            className='p-col-12'
                            name='type'
                        />
                    </div>

                    <div className="p-field">
                        <label htmlFor="backendBulkLoadType">Backend Bulk Load Type</label>
                        <Dropdown
                            id="backendBulkLoadType"
                            value={newBulkLoad.backendBulkLoadType}
                            options={backendBulkLoadTypes}
                            onChange={onChange}
                            placeholder={"Select Backend Bulk Load Type"}
                            className='p-col-12'
                            name='backendBulkLoadType'
                        />
                    </div>

                    <FMSForm
                        hideFMS={hideFMS}
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />

                    <URLForm
                        hideURL={hideURL}
                        hideOntology={hideOntology}
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />


                    <ManualForm
                        hideManual={hideManual}
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />

                </form>
            </div>
        </Dialog>
    );
};