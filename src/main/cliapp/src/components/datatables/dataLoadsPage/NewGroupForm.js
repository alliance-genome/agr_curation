import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button'
import { useMutation, useQueryClient } from 'react-query';
import { DataLoadService } from '../../../service/DataLoadService';

function NewGroupForm({ groupDialog, setGroupDialog }) {
    const [selectedValue, setSelectedValue] = useState();
    const dataLoadService = new DataLoadService();
    const mutation = useMutation(newGroupName => {
        return dataLoadService.createGroup(newGroupName);
    });

    const queryClient = useQueryClient();

    const options = [
        "ECO Ontology Load",
        "Allele Bulk Loads",
        "Gene Bulk Loads",
        "AGM Bulk Loads",
        "Disease Annotations Bulk Loads",
        "ZECO Ontology Load"
    ];
    const onChange = (e) => {
        setSelectedValue(e.value)
    }
    const hideDialog = () => {
        setGroupDialog(false);
    }
    const handleSubmit = (event) => {
        event.preventDefault();
        setGroupDialog(false);
        mutation.mutate(event.target.dropdown.value,{
            onSuccess: () =>{
                setSelectedValue('');
                queryClient.invalidateQueries('bulkloadgroup')
            }
        });
    }

    return (
        <Dialog visible={groupDialog} style={{ width: '450px' }} header="Add Group" modal className="p-fluid" onHide={hideDialog} resizeable >
            <div className='p-justify-center'>
                <form onSubmit={handleSubmit}>
                    <Dropdown
                        value={selectedValue}
                        options={options}
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

export default NewGroupForm;