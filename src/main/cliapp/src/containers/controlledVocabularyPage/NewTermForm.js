import {Dialog} from "primereact/dialog";
import React, {useRef} from "react";
import {Button} from "primereact/button";
import {InputText} from "primereact/inputtext";
import {Dropdown} from "primereact/dropdown";
import {Toast} from "primereact/toast";
import {useMutation} from "react-query";

export const NewTermForm = ({ newTermDialog, setNewTermDialog, newTerm, newTermDispatch, vocabularies, obsoleteTerms, vocabularyService }) => {
    const toast_success = useRef(null);
    const toast_error = useRef(null);

    const hideTermDialog = () => {
        newTermDispatch({ type: "RESET" });
        setNewTermDialog(false);
        /*setDisableFormFields(false);*/
    };

    const mutation = useMutation(newTerm => {
        return vocabularyService.createTerm(newTerm);
    });

    const handleTermSubmit = (event) => {
        event.preventDefault();
        mutation.mutate(newTerm, {
            onSuccess: () => {
                toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Term Added' });
                //queryClient.invalidateQueries('bulkloadtable');
                newTermDispatch({ type: "RESET" });
                setNewTermDialog(false);
                /*setDisableFormFields(false);*/
            },
            onError: (error) => {
                toast_error.current.show([
                    { life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
                ]);
            }
        });
    };

    const onChange = (e) => {
        newTermDispatch({
            field: e.target.name,
            value: e.target.value
        });
    };

    const newTermDialogFooter = (
        <>
            <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideTermDialog} />
            <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleTermSubmit} />
        </>
    );

    return(
        <div>
            <Toast ref={toast_error} position="top-left" />
            <Toast ref={toast_success} position="top-right" />
            <Dialog visible={newTermDialog} style={{ width: '450px' }} header="Add Term" modal className="p-fluid" footer={newTermDialogFooter} onHide={hideTermDialog} resizeable >
                <div className='p-justify-center'>
                    <form>
                        <div className="field">
                            <label htmlFor="name">Name</label>
                            <InputText
                                id="name"
                                name="name"
                                //placeholder={"Name"}
                                value={newTerm.name}
                                onChange={onChange}
                            />
                        </div>
                        <div className="field">
                            <label htmlFor="abbreviation">Abbreviation</label>
                            <InputText
                                id="abbreviation"
                                name="abbreviation"
                                //placeholder={"Abbreviation"}
                                value={newTerm.abbreviation}
                                onChange={onChange}
                            />
                        </div>
                        <div className="field">
                            <label htmlFor="vocabulary">Vocabulary</label>
                            <Dropdown
                                id="vocabulary.name"
                                options={vocabularies}
                                value={newTerm.vocab}
                                placeholder={"Select Vocabulary"}
                                className='p-col-12'
                                name='vocabulary.name'
                                optionLabel='name'
                                optionValue='id'
                                onChange={onChange}
                            />
                        </div>
                        <div className="field">
                            <label htmlFor="definition">Definition</label>
                            <InputText
                                id="definition"
                                name="definition"
                                //placeholder={"Definition"}
                                value={newTerm.definition}
                                onChange={onChange}
                            />
                        </div>
                        <div className="field">
                            <label htmlFor="obsolete">Obsolete</label>
                            <Dropdown
                                id="obsolete"
                                options={obsoleteTerms}
                                value={newTerm.obsolete}
                                placeholder={"Select Obsolete"}
                                className='p-col-12'
                                name='obsolete'
                                optionLabel='text'
                                optionValue='id'
                                onChange={onChange}
                            />
                        </div>
                    </form>
                </div>
            </Dialog>
        </div>
    );
};
