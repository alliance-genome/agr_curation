import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button'
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../../service/DataLoadService';

export const NewBulkLoadForm =({ bulkLoadDialog, setBulkLoadDialog }) => {
    const [selectedValue, setSelectedValue] = useState();
    const dataLoadService = new DataLoadService();
    const mutation = useMutation(newGroupName => {
        return dataLoadService.createGroup(newGroupName);
    });

    const queryClient = useQueryClient();

    const loadTypes = dataLoadService.getLoadTypes();
    const onChange = (e) => {
        setSelectedValue(e.value)
    }
    const hideDialog = () => {
        setBulkLoadDialog(false);
    }
    const handleSubmit = (event) => {
        event.preventDefault();
        setBulkLoadDialog(false);
        mutation.mutate(event.target.dropdown.value,{
            onSuccess: () =>{
                setSelectedValue('');
                queryClient.invalidateQueries('bulkloadtable')
            }
        });
    }

    return (
        <Dialog visible={bulkLoadDialog} style={{ width: '450px' }} header="Add Bulk Load" modal className="p-fluid" onHide={hideDialog} resizeable >
            <div className='p-justify-center'>
                <form onSubmit={handleSubmit}>
                    <Dropdown
                        value={selectedValue}
                        options={loadTypes}
                        onChange={(e) => onChange(e)}
                        placeholder={"Select Group"}
                        className='p-col-12'
                        name='dropdown'
                    />
                    <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
                    <Button label="Save" icon="pi pi-check" className="p-button-text" type='submit' />
                </form>
            </div>
        </Dialog>
    );
}