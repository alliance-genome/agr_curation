import React, {useRef, useState} from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
import { useMutation, useQueryClient } from "react-query";
import { AutocompleteFormEditor } from "../../components/Autocomplete/AutocompleteFormEditor";
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import {DiseaseAnnotationService} from "../../service/DiseaseAnnotationService";
import {Splitter, SplitterPanel} from "primereact/splitter";
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { SubjectAutocompleteTemplate } from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import { EvidenceAutocompleteTemplate } from '../../components/Autocomplete/EvidenceAutocompleteTemplate';

export const NewAnnotationForm = ({
									 newAnnotationState,
									 newAnnotationDispatch,
									 searchService,
									 diseaseAnnotationService,
									  diseaseRelationsTerms,
									  negatedTerms,
									  setNewDiseaseAnnotation
								 }) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newAnnotation, errorMessages, submitted, newAnnotationDialog } = newAnnotationState;
	const [isEnabled, setIsEnabled] = useState(false);
	const [isValid, setIsValid] = useState(false);

	const mutation = useMutation(newAnnotation => {
		if (!diseaseAnnotationService) {
			diseaseAnnotationService = new DiseaseAnnotationService();
		}
		return diseaseAnnotationService.createDiseaseAnnotation(newAnnotation);
	});


	const hideDialog = () => {
		newAnnotationDispatch({ type: "RESET" });
		setIsEnabled(false);
	};

	const handleSubmit = (event, closeAfterSubmit=true) => {
		event.preventDefault();
		newAnnotationDispatch({type: "SUBMIT"});
		mutation.mutate(newAnnotation, {
			onSuccess: (data) => {
				setNewDiseaseAnnotation(data.data.entity);
				queryClient.invalidateQueries('DiseaseAnnotationsHandles');
				toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Annotation Added'});
				setIsValid(true);
				if (closeAfterSubmit) {
					newAnnotationDispatch({type: "RESET"});
				} else {
					newAnnotationDispatch({type: "CLEAR"});
				}
			},
			onError: (error) => {
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				setIsValid(false);
				newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const handleClear = (event) => {
		newAnnotationDispatch({ type: "CLEAR" });
		setIsEnabled(false);
	}

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event, false);
		if (isValid) {
			newAnnotationDispatch({ type: "CLEAR" });
			setIsEnabled(false);
		}
	}

	const onObjectChange = (event) => {
		if(event.target.name === "subject") { //Save button should be enabled on subject value selection only
			if (event.target && event.target.value !== '' & event.target.value != null) {
				setIsEnabled(true);
			} else {
				setIsEnabled(false);
			}
		}
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const onDiseaseChange = (event) => {
		const curie = event.value.curie;
		const stringValue = event.value;
		const value = typeof event.value === "string" ? {curie: stringValue} : {curie};
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value,
		});
	}

	const onDropdownFieldChange = (event) => {
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	};

	const onArrayFieldChange = (event) => {
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const dialogFooter = (
		<>
			<div className="p-fluid p-formgrid p-grid">
				<div className="p-field p-col">
					<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
					<Button label="Save & Add Another" icon="pi pi-check" className="p-button-text" disabled={!isEnabled} onClick={handleSubmitAndAdd} />
				</div>
				<div className="p-field p-col">
					<Button label="Clear" icon="pi pi-check" className="p-button-text" onClick={handleClear} />
					<Button label="Save & Close" icon="pi pi-check" className="p-button-text" disabled={!isEnabled} onClick={handleSubmit} />
				</div>
			</div>
		</>
	);

	return(
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog visible={newAnnotationDialog} style={{ width: '850px' }} header="Add Annotation" modal className="p-fluid" footer={dialogFooter} onHide={hideDialog} resizeable >
				<form>
					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="subject"><font color={'red'}>*</font>Subject</label>
							<AutocompleteFormEditor
								autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
								searchService={searchService}
								name="subject"
								label="Subject"
								endpoint='biologicalentity'
								filterName='subjectFilter'
								fieldName='subject'
								value={newAnnotation.subject}
								onValueChangeHandler={onObjectChange}
								isSubject={true}
								valueDisplayHandler={(item, setAutocompleteSelectedItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.subject})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"subject"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="diseaseRelation"><font color={'red'}>*</font>Disease Relation</label>
							<Dropdown
								options={diseaseRelationsTerms}
								value={newAnnotation.diseaseRelation}
								name="diseaseRelation"
								optionLabel='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.diseaseRelation})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseRelation"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="negated">Negation</label>
							<Dropdown
								name="negated"
								value={newAnnotation.negated}
								options={negatedTerms}
								optionLabel='text'
								optionValue='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.negated})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"negated"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="object"><font color={'red'}>*</font>Disease</label>
							<AutocompleteFormEditor
								autocompleteFields={["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
								searchService={searchService}
								name="object"
								label="Disease"
								endpoint='doterm'
								filterName='diseaseFilter'
								fieldName='object'
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
								value={newAnnotation.object}
								onValueChangeHandler={onDiseaseChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.object})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"object"}/>
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="singleReference"><font color={'red'}>*</font>Reference</label>
							<AutocompleteFormEditor
								autocompleteFields={["curie", "cross_references.curie"]}
								searchService={searchService}
								name="singleReference"
								label="Reference"
								endpoint='literature-reference'
								filterName='curieFilter'
								fieldName='singleReference'
								isReference={true}
								value={newAnnotation.singleReference}
								onValueChangeHandler={onObjectChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.singleReference})}
								valueDisplayHandler={(item, setAutocompleteSelectedItem, op, query) =>
									<LiteratureAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"singleReference"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="evidenceCodes"><font color={'red'}>*</font>Evidence Code</label>
							<AutocompleteFormEditor
								autocompleteFields={["curie", "name", "abbreviation"]}
								searchService={searchService}
								name="evidenceCodes"
								label="Evidence Code"
								endpoint='ecoterm'
								filterName='evidenceFilter'
								fieldName='evidenceCodes'
								isMultiple={true}
								value={newAnnotation.evidenceCodes}
								onValueChangeHandler={onArrayFieldChange}
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									},
									subsetFilter: {
										"subsets": {
											queryString: "agr_eco_terms"
										}
									}
								}}
								valueDisplayHandler={(item, setAutocompleteSelectedItem, op, query) =>
									<EvidenceAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.evidenceCodes})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"evidenceCodes"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="with">With</label>
							<AutocompleteFormEditor
								autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
								searchService={searchService}
								name="with"
								label="With"
								endpoint='gene'
								filterName='withFilter'
								fieldName='with'
								isMultiple={true}
								isWith={true}
								value={newAnnotation.with}
								onValueChangeHandler={onArrayFieldChange}
								valueDisplayHandler={(item, setAutocompleteSelectedItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.with})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"with"}/>
						</SplitterPanel>
					</Splitter>
				</form>
			</Dialog>
		</div>
	);
}
