import React, {useRef, useState} from "react";
import {ValidationService} from '../../service/ValidationService';
import {Dialog} from "primereact/dialog";
import {Button} from "primereact/button";
import {Dropdown} from "primereact/dropdown";
import {Toast} from "primereact/toast";
import {useMutation, useQueryClient} from "react-query";
import {AutocompleteFormEditor} from "../../components/Autocomplete/AutocompleteFormEditor";
import {FormErrorMessageComponent} from "../../components/FormErrorMessageComponent";
import {classNames} from "primereact/utils";
import {DiseaseAnnotationService} from "../../service/DiseaseAnnotationService";
import {Splitter, SplitterPanel} from "primereact/splitter";
import {LiteratureAutocompleteTemplate} from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import {SubjectAutocompleteTemplate} from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import {EvidenceAutocompleteTemplate} from '../../components/Autocomplete/EvidenceAutocompleteTemplate';
import {RelatedNotesForm} from "./RelatedNotesForm";
import {ConditionRelationsForm} from "./ConditionRelationsForm";
import {ConditionRelationHandleFormDropdown} from "../../components/ConditionRelationHandleFormSelector";

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
	const withRef = useRef(null);
	const evidenceCodesRef = useRef(null);
	const experimentsRef = useRef(null);
	const {
		newAnnotation,
		errorMessages,
		relatedNotesErrorMessages,
		exConErrorMessages,
		submitted,
		newAnnotationDialog,
		showRelatedNotes,
		showConditionRelations,
	} = newAnnotationState;
	const [isEnabled, setIsEnabled] = useState(false);
	const validationService = new ValidationService();

	const validate = async (entities, endpoint) => {
		const validationResultsArray = [];
		for (const entity of entities) {
			const result = await validationService.validate(endpoint, entity);
			validationResultsArray.push(result);
		}
		return validationResultsArray;
	};

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

	const validateTable = async (endpoint, errorType, row) => {
		const results = await validate(row, endpoint);
		const errors = [];
		let anyErrors = false;
		results.forEach((result, index) => {
			const {isError, data} = result;
			if (isError) {
				errors[index] = {};
				if (!data) return;
				Object.keys(data).forEach((field) => {
					errors[index][field] = {
						severity: "error",
						message: data[field]
					};
				});
				anyErrors = true;
			}
		});
		newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorType: errorType,  errorMessages: errors});
		return anyErrors;
	}

	const handleSubmit = async (event, closeAfterSubmit=true) => {
		event.preventDefault();
		newAnnotationDispatch({type: "SUBMIT"});
		const isRelatedNotesErrors = await validateTable("note", "relatedNotesErrorMessages", newAnnotation.relatedNotes);
		const isExConErrors = await validateTable("condition-relation", "exConErrorMessages", newAnnotation.conditionRelations);

		mutation.mutate(newAnnotation, {
			onSuccess: (data) => {
				if (!(isRelatedNotesErrors || isExConErrors)) {
					setNewDiseaseAnnotation(data.data.entity);
					queryClient.invalidateQueries('DiseaseAnnotationsHandles');
					toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Annotation Added'});
					if (closeAfterSubmit) {
						newAnnotationDispatch({type: "RESET"});
					}
				}
			},
			onError: (error) => {
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				if (!error.response.data) return;
				newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorType: "errorMessages",  errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const handleClear = () => {
		//this manually resets the value of the input text in autocomplete fields with multiple values and the experiments dropdown
		withRef.current.inputRef.current.value = "";
		evidenceCodesRef.current.inputRef.current.value = "";
		experimentsRef.current.clear();
		newAnnotationDispatch({ type: "CLEAR" });
		setIsEnabled(false);
	}

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event, false);
	}

	const onSubjectChange = (event) => {
		if (event.target && event.target.value !== '' && event.target.value != null) {
			setIsEnabled(true);
		} else {
			setIsEnabled(false);
		}
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const onSingleReferenceChange= (event) => {
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

	const onDropdownExperimentsFieldChange = (event) =>{
		newAnnotationDispatch({
			type: "EDIT_EXPERIMENT",
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
	const isExperimentEnabled = () => {
		return (
			//only enabled if a reference is selected from suggestions and condition relation table isn't visible
			typeof newAnnotation.singleReference === "object"
			&& newAnnotation.singleReference.curie !== ""
			&& !showConditionRelations
		)
	}

	const isConditionRelationButtonEnabled = () => {
		return (
			newAnnotation.conditionRelations[0]
			&& newAnnotation.conditionRelations[0].handle
		)
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
								onValueChangeHandler={onSubjectChange}
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
								onValueChangeHandler={onSingleReferenceChange}
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
								customRef={evidenceCodesRef}
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
								customRef={withRef}
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
					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<RelatedNotesForm
								newAnnotationDispatch={newAnnotationDispatch}
								relatedNotes={newAnnotation.relatedNotes}
								showRelatedNotes={showRelatedNotes}
								errorMessages={relatedNotesErrorMessages}
							/>
						</SplitterPanel>
					</Splitter>
					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}} size={70}>
							<ConditionRelationsForm
								newAnnotationDispatch={newAnnotationDispatch}
								conditionRelations={newAnnotation.conditionRelations}
								showConditionRelations={showConditionRelations}
								errorMessages={exConErrorMessages}
								searchService={searchService}
								buttonIsDisabled={isConditionRelationButtonEnabled()}
							/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px', paddingTop: '6vh'}} size={30}>
							<label htmlFor="experiments">Experiments</label>
							<ConditionRelationHandleFormDropdown
								name="experiments"
								customRef={experimentsRef}
								editorChange={onDropdownExperimentsFieldChange}
								referenceCurie={newAnnotation.singleReference.curie}
								value={newAnnotation.conditionRelations[0]?.handle}
								showClear={false}
								placeholderText={newAnnotation.conditionRelations[0]?.handle}
								isEnabled={isExperimentEnabled()}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionRelations[0]?.handle"}/>
						</SplitterPanel>
					</Splitter>
				</form>
			</Dialog>
		</div>
	);
}
