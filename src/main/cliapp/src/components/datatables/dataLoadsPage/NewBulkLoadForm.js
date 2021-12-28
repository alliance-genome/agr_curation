import React, { useReducer, useRef, useState } from 'react';
import { Dropdown } from "primereact/dropdown"
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button'
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../../service/DataLoadService';
import { InputText } from 'primereact/inputtext';
import { FMSForm } from './FMSForm';


const initialFormState = {
    name: "",
    group: "",
    backendBulkLoadType: "",
    loadType: "",
    dataType: "",
    subType: ""
}

const formReducer = (state, action) => {
    switch (action.type) {
        case 'RESET':
            return initialFormState;
        default:
            return { ...state, [action.field]: action.value }
    }
}


export const NewBulkLoadForm = ({ bulkLoadDialog, setBulkLoadDialog, groupNames }) => {
    const dataLoadService = new DataLoadService();
    const [formState, dispatch] = useReducer(formReducer, initialFormState)
    const queryClient = useQueryClient();
    const hideFMS= useRef(true);
    const [hideURL, setHideURL] = useState(true);
    const [hideManual, setHideManual] = useState(true);

    const showLoadTypeForm = (value) => {
        switch (value) {
            case 'fms':
                hideFMS.current = false;
                break;
            case 'URL':
                setHideURL(false);
                break;
            case 'MANUAL':
                setHideManual(false);
                break;
            default:
                break;
        }
    }

    const mutation = useMutation(formState => {
        return dataLoadService.createLoad(formState);
    });

    const backendBulkLoadTypes = dataLoadService.getBackendBulkLoadTypes();
    const loadTypes = dataLoadService.getLoadTypes();

    const onChange = (e) => {
        dispatch({
            field: e.target.name,
            value: e.target.value
        });

        if(e.target.name === "loadType" ){
            showLoadTypeForm(e.target.value);
        }
    }

    const hideDialog = () => {
        dispatch({ type: "RESET" })
        setBulkLoadDialog(false);
    }
    const handleSubmit = (event) => {
        event.preventDefault();
        dispatch({ type: "RESET" })
        setBulkLoadDialog(false);
        mutation.mutate(formState, {
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
                        value={formState.name}
                        onChange={onChange}
                    />
                    <Dropdown
                        value={formState.group}
                        options={groupNames}
                        onChange={onChange}
                        placeholder={"Select Group"}
                        className='p-col-12'
                        name='group'
                    />
                    <Dropdown
                        value={formState.backendBulkLoadType}
                        options={backendBulkLoadTypes}
                        onChange={onChange}
                        placeholder={"Select Backend Bulk Load Type"}
                        className='p-col-12'
                        name='backendBulkLoadType'
                    />

                    <Dropdown
                        value={formState.loadType}
                        options={loadTypes}
                        onChange={onChange}
                        placeholder={"Select Load Type"}
                        className='p-col-12'
                        name='loadType'
                    />
                    <FMSForm
                        hideFMS={hideFMS}
                        formState={formState}
                        onChange={onChange}
                    />
                    <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
                    <Button label="Save" icon="pi pi-check" className="p-button-text" type='submit' />
                </form>
            </div>
        </Dialog>
    );
}