import React, {useRef, useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {classNames} from "primereact/utils";
import {Button} from "primereact/button";
import {useMutation,useQueryClient} from "react-query";
import { Toast } from 'primereact/toast';
import {VocabularyService} from "../../service/VocabularyService";
import ErrorBoundary from "../../components/Error/ErrorBoundary";

export const NewVocabularyForm = ({ newVocabularyDialog, setNewVocabularyDialog }) => {
		const queryClient = useQueryClient();
		const [vocabulary, setVocabulary] = useState({});
		const [submitted, setSubmitted] = useState(false);
		const toast_success = useRef(null);
		const toast_error = useRef(null);

		let emptyVocabulary = {};
		let vocabularyService = null;

		const mutation = useMutation(newVocabularyName => {
				return getService().createVocabulary(newVocabularyName);
		});

		const onChange = (event, field) => {
				const val = (event.target && event.target.value) || '';
				let _vocabulary = { ...vocabulary };
				_vocabulary[field] = val;
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
								onSuccess: () => {
										toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Vocabulary Added' });
										queryClient.invalidateQueries('vocabularies');
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
										{submitted && !vocabulary.name && <small className="p-error">Vocabulary is required.</small>}
								</div>
							</ErrorBoundary>
						</Dialog>
				</div>
		);
};
