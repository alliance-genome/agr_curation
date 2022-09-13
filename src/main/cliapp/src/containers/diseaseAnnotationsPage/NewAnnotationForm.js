import React, { useRef } from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
import { useMutation, useQueryClient } from "react-query";
import { AutocompleteEditor } from "../../components/Autocomplete/AutocompleteEditor";
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import {DiseaseAnnotationService} from "../../service/DiseaseAnnotationService";
import {Splitter, SplitterPanel} from "primereact/splitter";

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

	const mutation = useMutation(newAnnotation => {
		if (!diseaseAnnotationService) {
			diseaseAnnotationService = new DiseaseAnnotationService();
		}
		return diseaseAnnotationService.createDiseaseAnnotation(newAnnotation);
	});


	const hideDialog = () => {
		newAnnotationDispatch({ type: "RESET" });
	};

	const handleSubmit = (event) => {
		event.preventDefault();
		newAnnotationDispatch({type: "SUBMIT"});
		mutation.mutate(newAnnotation, {
			onSuccess: (data) => {
				setNewDiseaseAnnotation(data.data.entity);
				queryClient.invalidateQueries('DiseaseAnnotationsHandles');
				toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Annotation Added'});
				newAnnotationDispatch({type: "RESET"});
			},
			onError: (error) => {
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const handleClear = (event) => {
		newAnnotationDispatch({ type: "CLEAR" });
	}

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event);
		newAnnotationDispatch({ type: "CLEAR" });
	}

	const onObjectChange = (event, setFieldValue) => {
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
		setFieldValue(event.target.value);
	}

	const onDiseaseChange = (event, setFieldValue) => {
		const curie = event.value.curie;
		const stringValue = event.value;
		const value = typeof event.value === "string" ? {curie: stringValue} : {curie};
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value,
		});
		setFieldValue(event.target.value);
	}

	const onDropdownFieldChange = (event) => {
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	};

	const onArrayFieldChange = (event, setFieldValue) => {
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
		setFieldValue(event.target.value);
	}

	const dialogFooter = (
		<>
			<div className="p-fluid p-formgrid p-grid">
				<div className="p-field p-col">
					<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
					<Button label="Save & Add Another" icon="pi pi-check" className="p-button-text" onClick={handleSubmitAndAdd} />
				</div>
				<div className="p-field p-col">
					<Button label="Clear" icon="pi pi-check" className="p-button-text" onClick={handleClear} />
					<Button label="Save & Close" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
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
							<label htmlFor="subject">Subject</label>
							<AutocompleteEditor
								autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
								searchService={searchService}
								name="subject"
								label="Subject"
								endpoint='biologicalentity'
								filterName='subjectFilter'
								fieldName='subject'
								value={newAnnotation.subject}
								passedOnChange={onObjectChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.subject})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"subject"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="diseaseRelation">Disease Relation</label>
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
							<label htmlFor="annotationNegation">Negation</label>
							<Dropdown
								name="annotationNegation"
								value={newAnnotation.annotationNegation}
								options={negatedTerms}
								optionLabel='text'
								optionValue='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.annotationNegation})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationNegation"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="annotationDisease">Disease</label>
							<AutocompleteEditor
								autocompleteFields={["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
								searchService={searchService}
								name="annotationDisease"
								label="Disease"
								endpoint='doterm'
								filterName='diseaseFilter'
								fieldName='annotationDisease'
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
								value={newAnnotation.annotationDisease}
								passedOnChange={onDiseaseChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.annotationDisease})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationDisease"}/>
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="annotationReference">Reference</label>
							<AutocompleteEditor
								autocompleteFields={["curie", "cross_references.curie"]}
								searchService={searchService}
								name="annotationReference"
								label="Reference"
								endpoint='literature-reference'
								filterName='curieFilter'
								fieldName='annotationReference'
								value={newAnnotation.annotationReference}
								passedOnChange={onObjectChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.annotationReference})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationReference"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="annotationEvidenceCode">Evidence Code</label>
							<AutocompleteEditor
								autocompleteFields={["curie", "name", "abbreviation"]}
								searchService={searchService}
								name="annotationEvidenceCode"
								label="Evidence Code"
								endpoint='ecoterm'
								filterName='evidenceFilter'
								fieldName='annotationEvidenceCode'
								isMultiple={true}
								value={newAnnotation.annotationEvidenceCode}
								passedOnChange={onArrayFieldChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.annotationEvidenceCode})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationEvidenceCode"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="annotationWith">With</label>
							<AutocompleteEditor
								autocompleteFields={["symbol", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"]}
								searchService={searchService}
								name="annotationWith"
								label="With"
								endpoint='gene'
								filterName='withFilter'
								fieldName='annotationWith'
								isMultiple={true}
								value={newAnnotation.annotationWith}
								passedOnChange={onArrayFieldChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.annotationWith})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationWith"}/>
						</SplitterPanel>
					</Splitter>
				</form>
			</Dialog>
		</div>
	);
}
