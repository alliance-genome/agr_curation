import React, { useRef } from "react";
import { VocabularyTermSetService } from "../../service/VocabularyTermSetService";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Toast } from "primereact/toast";
import { useMutation, useQueryClient } from "react-query";
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { AutocompleteEditor } from "../../components/Autocomplete/AutocompleteEditor";
import {autocompleteSearch, buildAutocompleteFilter} from "../../utils/utils";
import ErrorBoundary from "../../components/Error/ErrorBoundary";


export const NewVocabularyTermSetForm = ({
	newVocabularyTermSetState,
	newVocabularyTermSetDispatch,
	searchService,
	vocabularyTermSetService,
	setNewVocabularyTermSet,
}) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newVocabularyTermSet, errorMessages, submitted, newVocabularyTermSetDialog } = newVocabularyTermSetState;


	const hideDialog = () => {
		newVocabularyTermSetDispatch({ type: "RESET" });
	};

	const mutation = useMutation(newVocabularyTermSet => {
		if (!vocabularyTermSetService) {
			vocabularyTermSetService = new VocabularyTermSetService();
		}
		return vocabularyTermSetService.createVocabularyTermSet(newVocabularyTermSet);
	});

	const handleSubmit = (event) => {
		event.preventDefault();
		newVocabularyTermSetDispatch({type: "SUBMIT"});
		mutation.mutate(newVocabularyTermSet, {
			onSuccess: (data) => {
				setNewVocabularyTermSet(data.data.entity);
				queryClient.invalidateQueries('VocabularyTermSetHandles');
				toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Vocabulary Term Set Added'});
				newVocabularyTermSetDispatch({type: "RESET"});
			},
			onError: (error) => {
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				newVocabularyTermSetDispatch({type: "UPDATE_ERROR_MESSAGES", errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const onNameChange = (event) => {
		newVocabularyTermSetDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	};

	const onVocabularyChange = (event, setFieldValue) => {
		setFieldValue(event.target.value);
		newVocabularyTermSetDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.value,
		});
	}
	const vocabularySearch = (event, setFiltered, setQuery) => {
		const autocompleteFields =["name"];
		const endpoint="vocabulary";
		const filterName="vocabularyFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const onMemberTermsChange = (event, setFieldValue) => {
		setFieldValue(event.value);
		newVocabularyTermSetDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.value
		});
	}

	const memberTermSearch = (event, setFiltered, setInputValue, props) => {
		const autocompleteFields =["name"];
		const endpoint = "vocabularyterm";
		const filterName = "memberTermsFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			vocabularyFilter: {
				"vocabulary.name": {
					queryString: newVocabularyTermSet?.vocabularyTermSetVocabulary?.name
				}
			}
		}

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const onDescriptionChange = (event) => {
		newVocabularyTermSetDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const isMemberTermsEnabled = () => {
		return (
			//only enabled if a vocabulary is selected
			newVocabularyTermSet.vocabularyTermSetVocabulary != null
		)
	}

	const dialogFooter = (
		<>
			<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
			<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
		</>
	);

	return(
		<div>
				<Toast ref={toast_error} position="top-left" />
				<Toast ref={toast_success} position="top-right" />
				<Dialog visible={newVocabularyTermSetDialog} style={{ width: '450px' }} header="Add Vocabulary Term Set" modal className="p-fluid" footer={dialogFooter} onHide={hideDialog} resizeable >
					<ErrorBoundary>
						<div className='p-justify-center'>
							<form>
								<div className="field">
									<label htmlFor="name"><font color={'red'}>*</font>Name</label>
									<InputText
										id="name"
										name="name"
										value={newVocabularyTermSet.name}
										onChange={onNameChange}
										required
										className={classNames({ 'p-invalid': submitted && !newVocabularyTermSet.name })}
									/>
									<FormErrorMessageComponent errorMessages={errorMessages} errorField={"name"}/>
								</div>
								<div className="field">
									<label htmlFor="vocabularyTermSetVocabulary"><font color={'red'}>*</font>Vocabulary</label>
									<AutocompleteEditor
										name="vocabularyTermSetVocabulary"
										label="Vocabulary"
										fieldName='vocabularyTermSetVocabulary'
										subField={"name"}
										initialValue={newVocabularyTermSet?.vocabularyTermSetVocabulary?.name}
										search={vocabularySearch}
										onValueChangeHandler={onVocabularyChange}
										classNames={classNames({'p-invalid': submitted && errorMessages?.vocabularyTermSetVocabulary})}
										valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
											<VocabTermAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
									/>
									<FormErrorMessageComponent errorMessages={errorMessages} errorField={"vocabularyTermSetVocabulary"}/>
								</div>
								<div className="field">
									<label htmlFor="vocabularyTermSetDescription">Description</label>
									<InputText
										id="vocabularyTermSetDescription"
										name="vocabularyTermSetDescription"
										//placeholder={"Description"}
										value={newVocabularyTermSet.vocabularyTermSetDescription}
										onChange={onDescriptionChange}
									/>
									<FormErrorMessageComponent errorMessages={errorMessages} errorField={"vocabularyTermSetDescription"}/>
								</div>
								<div className="field">
									<label htmlFor="memberTerms">Vocabulary Terms</label>
									<AutocompleteMultiEditor
										name="memberTerms"
										fieldName='memberTerms'
										subField='name'
										isEnabled={isMemberTermsEnabled()}
										initialValue={newVocabularyTermSet.memberTerms}
										search={memberTermSearch}
										onValueChangeHandler={onMemberTermsChange}
										valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
											<VocabTermAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
										classNames={classNames({'p-invalid': submitted && errorMessages?.memberTerms})}
									/>
									<FormErrorMessageComponent errorMessages={errorMessages} errorField={"memberTerms"}/>
								</div>
							</form>
						</div>
					</ErrorBoundary>
				</Dialog>
			</div>

	);
}
