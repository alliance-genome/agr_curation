import React,{useState} from 'react';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { classNames } from 'primereact/utils';


export const NewBulkLoadGroupForm = () => {
    const [submitted, setSubmitted] = useState(false);

    let emptyGroup = {

    };

    const onChange = (e) => {
        setSelectedValue(e.value)
    }
    const hideDialog = () => {
        setBulkLoadDialog(false);
    }
    const handleSubmit = (event) => {
        event.preventDefault();
        // const val = (e.target && e.target.value) || '';
        // let _group = { ...group };
        // _group[`${name}`] = val;
        setBulkLoadDialog(false);
        // mutation.mutate(event.target.dropdown.value,{
        //     onSuccess: () =>{
        //         setSelectedValue('');
        //         queryClient.invalidateQueries('bulkloadtable')
        //     }
        // });
    }
    const openNewGroup = () => {
        setGroup(emptyGroup);
        setSubmitted(false);
        setGroupDialog(true);
    };

    const saveGroup = () => {
        setSubmitted(true);
        setGroupDialog(false);
    };

    const groupDialogFooter = (
        <React.Fragment>
            <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
            <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={saveGroup} />
        </React.Fragment>
    );


    return (
        <div>
            <Dialog visible={groupDialog} style={{ width: '450px' }} header="Group Details" modal className="p-fluid" footer={groupDialogFooter} onHide={hideDialog}>
                <div className="p-field">
                    <label htmlFor="name">Name</label>
                    <InputText id="name" value={group.name} onChange={(e) => onInputChange(e, 'name')} required autoFocus className={classNames({ 'p-invalid': submitted && !group.name })} />
                    {submitted && !group.name && <small className="p-error">Name is required.</small>}
                </div>
            </Dialog>
        </div>
    );
}

