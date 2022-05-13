import {Dialog} from "primereact/dialog";
import React, {useRef, useState} from "react";
import {Button} from "primereact/button";
import {InputText} from "primereact/inputtext";
import {Dropdown} from "primereact/dropdown";
import {Toast} from "primereact/toast";
import {useMutation, useQueryClient} from "react-query";
import {classNames} from "primereact/utils";

export const NewTermForm = ({ newTermDialog, setNewTermDialog, newTerm, newTermDispatch, vocabularies, obsoleteTerms, vocabularyService}) => {
    const queryClient = useQueryClient();
    const toast_success = useRef(null);
    const toast_error = useRef(null);
    const [submitted, setSubmitted] = useState(false);

    const hideTermDialog = () => {
        newTermDispatch({ type: "RESET" });
        setNewTermDialog(false);
        setSubmitted(false);
    };

    const mutation = useMutation(newTerm => {
        return vocabularyService.createTerm(newTerm);
    });

    const handleTermSubmit = (event) => {
        event.preventDefault();
        setSubmitted(true);
        if ((newTerm.name && newTerm.name.trim()) && newTerm.vocabulary && newTerm.obsolete!==undefined)
        {
            mutation.mutate(newTerm, {
                onSuccess: (data) => {
                    queryClient.invalidateQueries('vocabterms');
                    toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Term Added'});
                    setSubmitted(false);
                    newTermDispatch({type: "RESET"});
                    setNewTermDialog(false);
                },
                onError: (error) => {
                    toast_error.current.show([
                        {life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false}
                    ]);
                }
            });
        }
    };

    const onChange = (e) => {
      if (e.target.name === "vocabulary") {
        newTermDispatch({
          field: e.target.name,
          value: e.value
        });
      } else {
        newTermDispatch({
          field: e.target.name,
          value: e.target.value
        });
      }
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
                                required
                                className={classNames({ 'p-invalid': submitted && !newTerm.name })}
                            />{submitted && !newTerm.name && <small className="p-error">Name is required.</small>}
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
                                id="vocabulary"
                                options={vocabularies}
                                value={newTerm.vocabulary}
                                placeholder={"Select Vocabulary"}
                                name='vocabulary'
                                optionLabel='name'
                                onChange={onChange}
                                required
                                className={classNames({ 'p-invalid': submitted && !newTerm.vocabulary }, 'p-col-12')}
                            />{submitted && !newTerm.vocabulary && <small className="p-error">Vocabulary is required.</small>}
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
                                name='obsolete'
                                optionLabel='text'
                                optionValue='name'
                                onChange={onChange}
                                required
                                className={classNames({ 'p-invalid': submitted && newTerm.obsolete === undefined }, 'p-col-12')}
                            />{submitted && newTerm.obsolete === undefined && <small className="p-error">Obsolete is required.</small>}
                        </div>
                    </form>
                </div>
            </Dialog>
        </div>
    );
};
