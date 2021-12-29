import React, { useReducer, useRef } from 'react';
import { Dropdown } from "primereact/dropdown"
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button'
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../../service/DataLoadService';
import { InputText } from 'primereact/inputtext';
import { FMSForm } from './FMSForm';


const emptyBulkLoad = {
    name: "",
    group: null,
    backendBulkLoadType: "",
    loadType: "",
    dataType: "",
    dataSubType: ""
}

const bulkLoadReducer = (state, action) => {
    switch (action.type) {
        case 'RESET':
            return emptyBulkLoad;
        default:
            return { ...state, [action.field]: action.value }
    }
}


export const NewBulkLoadForm = ({ bulkLoadDialog, setBulkLoadDialog, groups }) => {
    const dataLoadService = new DataLoadService();
    const [newBulkLoad, bulkLoadDispatch] = useReducer(bulkLoadReducer, emptyBulkLoad)
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
            case 'fms':
                hideFMS.current = false;
                break;
            case 'url':
                hideURL.current = false;
                break;
            case 'manual':
                hideManual.current = false;
                break;
            default:
                break;
        }
    }


    const onChange = (e) => {
        bulkLoadDispatch({
            field: e.target.name,
            value: e.target.value
        });

        if (e.target.name === "loadType") {
            showLoadTypeForm(e.target.value);
        }
    }

    const onGroupChange = (e) => {
        console.log(e)
        bulkLoadDispatch({
            field: e.target.name,
            value: e.value
        });

    }

    const hideDialog = () => {
        bulkLoadDispatch({ type: "RESET" })
        setBulkLoadDialog(false);
        hideFMS.current = true;
        hideURL.current = true;
        hideManual.current = true;
    }
    const handleSubmit = (event) => {
        event.preventDefault();
        bulkLoadDispatch({ type: "RESET" })
        setBulkLoadDialog(false);
        hideFMS.current = true;
        hideURL.current = true;
        hideManual.current = true;
        mutation.mutate(newBulkLoad, {
            onSuccess: () => {
                queryClient.invalidateQueries('bulkloadtable')
            }
        });
    }


    return (
        <Dialog visible={bulkLoadDialog} style={{ width: '450px' }} header="Add Bulk Load" modal className="p-fluid" onHide={hideDialog} resizeable >
            <div className='p-justify-center'>
                <form onSubmit={handleSubmit}>
                    <InputText
                        name="name"
                        value={newBulkLoad.name}
                        onChange={onChange}
                    />
                    <Dropdown
                        options={groups}
                        value={newBulkLoad.group}
                        onChange={onGroupChange}
                        placeholder={"Select Group"}
                        className='p-col-12'
                        name='group'
                        optionLabel='name'
                    />
                    <Dropdown
                        value={newBulkLoad.backendBulkLoadType}
                        options={backendBulkLoadTypes}
                        onChange={onChange}
                        placeholder={"Select Backend Bulk Load Type"}
                        className='p-col-12'
                        name='backendBulkLoadType'
                    />

                    <Dropdown
                        value={newBulkLoad.loadType}
                        options={loadTypes}
                        onChange={onChange}
                        placeholder={"Select Load Type"}
                        className='p-col-12'
                        name='loadType'
                    />
                    <FMSForm
                        hideFMS={hideFMS}
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />
                    <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
                    <Button label="Save" icon="pi pi-check" className="p-button-text" type='submit' />
                </form>
            </div>
        </Dialog>
    );
}