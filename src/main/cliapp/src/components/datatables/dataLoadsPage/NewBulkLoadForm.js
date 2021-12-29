import React, { useReducer, useRef } from 'react';
import { Dropdown } from "primereact/dropdown";
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../../service/DataLoadService';
import { InputText } from 'primereact/inputtext';
import { FMSForm } from './FMSForm';
import { URLForm } from './URLForm';


const emptyBulkLoad = {
    name: "",
    group: null,
    backendBulkLoadType: "",
    type: "",
    dataType: "",
    dataSubType: "",
    url: "",
    scheduled: null,
    cronSchedule: ""
};

const bulkLoadReducer = (state, action) => {
    switch (action.type) {
        case 'RESET':
            return emptyBulkLoad;
        default:
            return { ...state, [action.field]: action.value };
    }
};


export const NewBulkLoadForm = ({ bulkLoadDialog, setBulkLoadDialog, groups }) => {
    const dataLoadService = new DataLoadService();
    const [newBulkLoad, bulkLoadDispatch] = useReducer(bulkLoadReducer, emptyBulkLoad);
    const queryClient = useQueryClient();
    const hideFMS = useRef(true);
    const hideURL = useRef(true);
    const hideManual = useRef(true);

    const backendBulkLoadTypes = dataLoadService.getBackendBulkLoadTypes();
    const loadTypes = dataLoadService.getLoadTypes();

    const mutation = useMutation(newBulkLoad => {
        return dataLoadService.createLoad(newBulkLoad);
    });

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
            default:
                break;
        }
    };


    const onChange = (e) => {
        bulkLoadDispatch({
            field: e.target.name,
            value: e.target.value
        });

        if (e.target.name === "type") {
            hideFMS.current = true;
            hideURL.current = true;
            hideManual.current = true;
            showLoadTypeForm(e.target.value);
        }

        if(e.target.name === "scheduled"){
            bulkLoadDispatch({
                field: e.target.name,
                value: e.target.value.name
            });
        }
    };

    const onGroupChange = (e) => {
        bulkLoadDispatch({
            field: e.target.name,
            value: e.value
        });
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
        <React.Fragment>
            <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
            <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
        </React.Fragment>
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
                            onChange={onGroupChange}
                            placeholder={"Select Group"}
                            className='p-col-12'
                            name='group'
                            optionLabel='name'
                        />
                    </div>

                    <div className="p-field">
                        <label htmlFor="backendBulkLoadType">Backend Bulk Load Type</label>
                        <Dropdown
                            value={newBulkLoad.backendBulkLoadType}
                            options={backendBulkLoadTypes}
                            onChange={onChange}
                            placeholder={"Select Backend Bulk Load Type"}
                            className='p-col-12'
                            name='backendBulkLoadType'
                        />
                    </div>

                    <div className="p-field">
                        <label htmlFor="type">Load Type</label>
                        <Dropdown
                            value={newBulkLoad.type}
                            options={loadTypes}
                            onChange={onChange}
                            placeholder={"Select Load Type"}
                            className='p-col-12'
                            name='type'
                        />
                    </div>

                    <FMSForm
                        hideFMS={hideFMS}
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />

                    <URLForm
                        hideURL={hideURL}
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />

                </form>
            </div>
        </Dialog>
    );
};