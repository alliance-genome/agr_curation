import React, {useRef, useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {classNames} from "primereact/utils";
import {Button} from "primereact/button";
import {useMutation,useQueryClient} from '@tanstack/react-query';
import { Toast } from 'primereact/toast';
import { Dropdown } from "primereact/dropdown";
import {VocabularyService} from "../../service/VocabularyService";
import ErrorBoundary from "../../components/Error/ErrorBoundary";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";

export const NewVocabularyForm = ({ newVocabularyDialog, setNewVocabularyDialog, setNewVocabulary }) => {
		const queryClient = useQueryClient();
		const [vocabulary, setVocabulary] = useState(
			{
				obsolete: false,
				name: "",
				vocabularyLabel: "",
				vocabularyDescription: ""
			}
		);
		const [submitted, setSubmitted] = useState(false);
		const toast_success = useRef(null);
		const toast_error = useRef(null);

		let emptyVocabulary = {};
		let vocabularyService = null;

		const booleanTerms = useControlledVocabularyService("generic_boolean_terms");

		const mutation = useMutation(newVocabularyName => {
				return getService().createVocabulary(newVocabularyName);
		});

		const onChange = (event, field) => {
				const val = (event.target && event.target.value) || '';
				let _vocabulary = { ...vocabulary };
				_vocabulary[field] = val;
				setVocabulary(_vocabulary);
		};
		const onObsoleteChange = (value) => {
				if(value === undefined || value === undefined) return;
				let _vocabulary = { ...vocabulary };
				_vocabulary.obsolete = value;
				setVocabulary(_vocabulary);
		};

		const getService = () => {
				if(!vocabularyService) {
						vocabularyService = new VocabularyService();
				}
				return vocabularyService;
		};

		const hideVocabularyDialog = () => {
				setNewVocabularyDialog(false);
				setSubmitted(false);
				setVocabulary(emptyVocabulary);
		};

		const saveVocabulary = (event) => {
				event.preventDefault();
				setSubmitted(true);
				if (vocabulary.name && vocabulary.name.trim()) {
						console.log(event);
						mutation.mutate(vocabulary, {
								onSuccess: (data) => {
									if(setNewVocabulary) {
										//Invalidating the query immediately after success leads to api results that don't always include the new entity
										setTimeout(() => {
											queryClient.invalidateQueries(['Vocabularies']).then(() => {
												//needs to be set after api call otherwise the newly appended entity would be removed when there are no filters
												setNewVocabulary(data.data.entity)
											});
										}, 1000);
									} else {
											queryClient.invalidateQueries(['vocabularies']);
										};
										toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Vocabulary Added' });
										setSubmitted(false);
										setNewVocabularyDialog(false);
										setVocabulary(emptyVocabulary);
								},
								onError: (error) => {
										toast_error.current.show([
												{ life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
										]);
								}
						});
				}

		};

		const newVocabularyDialogFooter = (
				<React.Fragment>
						<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideVocabularyDialog} />
						<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={saveVocabulary} />
				</React.Fragment>
		);

		return (
			<div>
						<Toast ref={toast_error} position="top-left" />
						<Toast ref={toast_success} position="top-right" />
						<Dialog visible={newVocabularyDialog} style={{ width: '450px' }} header="Add Vocabulary" modal className="p-fluid" footer={newVocabularyDialogFooter} onHide={hideVocabularyDialog}>
							<ErrorBoundary>
								<div className="field">
										<label htmlFor="name">Vocabulary Name</label>

										<InputText id="name" value={vocabulary.name} onChange={(e) => onChange(e, 'name')} required autoFocus className={classNames({ 'p-invalid': submitted && !vocabulary.name })} />
										{submitted && !vocabulary.name && <small className="p-error">Vocabulary name is required.</small>}
								</div>
								<div className="field">
										<label htmlFor="vocabularyLabel">Vocabulary Label</label>

										<InputText id="vocabularyLabel" value={vocabulary.vocabularyLabel} onChange={(e) => onChange(e, 'vocabularyLabel')} required autoFocus className={classNames({ 'p-invalid': submitted && !vocabulary.vocabularyLabel })} />
										{submitted && !vocabulary.vocabularyLabel && <small className="p-error">Vocabulary label is required.</small>}
								</div>
								<div className="field">
										<label htmlFor="vocabularyDescription">Vocabulary Description</label>

										<InputText id="vocabularyDescription" value={vocabulary.vocabularyDescription} onChange={(e) => onChange(e, 'vocabularyDescription')} required autoFocus className={classNames({ 'p-invalid': submitted && !vocabulary.vocabularyDescription })} />
										{submitted && !vocabulary.vocabularyDescription && <small className="p-error">Vocabulary description is required.</small>}
								</div>
								<div className="field">
										<label htmlFor="obsolete">Obsolete</label>
										<Dropdown
											id="obsolete"
											value={vocabulary.obsolete}
											options={booleanTerms}
											optionLabel='text'
											optionValue='name'
											onChange={(e) => onObsoleteChange(e.target?.value)}
											className={classNames({ 'p-invalid': submitted && (vocabulary.obsolete === null || vocabulary.obsolete === undefined) })}
										/>
										{submitted && (vocabulary.obsolete === null || vocabulary.obsolete === undefined) && <small className="p-error">Obsolete is required.</small>}
								</div>

							</ErrorBoundary>
						</Dialog>
				</div>
		);
};
