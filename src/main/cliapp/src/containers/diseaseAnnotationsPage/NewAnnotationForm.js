import React, { useRef, useState } from "react";
import { ValidationService } from '../../service/ValidationService';
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
import { useMutation, useQueryClient } from "react-query";
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import { DiseaseAnnotationService } from "../../service/DiseaseAnnotationService";
import { Splitter, SplitterPanel } from "primereact/splitter";
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { SubjectAutocompleteTemplate } from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import { EvidenceAutocompleteTemplate } from '../../components/Autocomplete/EvidenceAutocompleteTemplate';
import { RelatedNotesForm } from "./RelatedNotesForm";
import { ConditionRelationsForm } from "./ConditionRelationsForm";
import { ConditionRelationHandleFormDropdown } from "../../components/ConditionRelationHandleFormSelector";
import { ControlledVocabularyFormDropdown } from '../../components/ControlledVocabularyFormSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyFormMultiSelectDropdown } from '../../components/ControlledVocabularyFormMultiSelector';
import { AutocompleteFormEditor } from "../../components/Autocomplete/AutocompleteFormEditor";
import { autocompleteSearch, buildAutocompleteFilter } from "../../utils/utils";
import { AutocompleteFormMultiEditor } from "../../components/Autocomplete/AutocompleteFormMultiEditor";
import { SubjectAdditionalFieldData } from "../../components/SubjectAdditionalFieldData";
import { AssertedAlleleAdditionalFieldData } from "../../components/AssertedAlleleAdditionalFieldData";
import { DiseaseAdditionalFieldData } from "../../components/DiseaseAdditionalFieldData";
import { SingleReferenceAdditionalFieldData } from "../../components/SingleReferenceAdditionalFieldData";
import { SGDStrainBackgroundAdditionalFieldData } from "../../components/SGDStrainBackgroundAdditionalFieldData";
import { AssertedGenesAdditionalFieldData } from "../../components/AssertedGenesAdditionalFieldData";
import { EvidenceCodesAdditionalFieldData } from "../../components/EvidenceCodesAdditionalFieldData";
import { WithAdditionalFieldData } from "../../components/WithAdditionalFieldData";
import { GeneticModifierAdditionalFieldData } from "../../components/GeneticModifierAdditionalFieldData";

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
	const assertedGenesRef = useRef(null);
	const assertedAlleleRef = useRef(null);
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
	const [isAsssertedGeneEnabled, setAsssertedGeneEnabled] = useState(false);
	const [isAsssertedAlleleEnabled, setAsssertedAlleleEnabled] = useState(false);
	const validationService = new ValidationService();
	const geneticSexTerms = useControlledVocabularyService('Genetic sexes');
	const diseaseQualifiersTerms = useControlledVocabularyService('Disease qualifiers');
	const annotationTypeTerms = useControlledVocabularyService('Annotation types');
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('Disease genetic modifier relations');

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
		setAsssertedAlleleEnabled(false);
		setAsssertedGeneEnabled(false);
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
		newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorType: errorType, errorMessages: errors});
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
					queryClient.invalidateQueries('DiseaseAnnotationsHandles');
					toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Annotation Added'});
					if (closeAfterSubmit) {
						newAnnotationDispatch({type: "RESET"});
					}
					//Invalidating the query immediately after success leads to api results that don't always include the new annotation
					setTimeout(() => {
						queryClient.invalidateQueries("DiseaseAnnotations").then(() => {
							//needs to be set after api call otherwise the newly appended DA would be removed when there are no filters
							setNewDiseaseAnnotation(data.data.entity);
						});
					}, 1000);
				}
			},
			onError: (error) => {
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				if (!error.response.data) return;
				newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorType: "errorMessages", errorMessages: error.response.data.errorMessages});
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
		setAsssertedAlleleEnabled(false);
		setAsssertedGeneEnabled(false);
	}

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event, false);
	}

	const onSingleReferenceChange = (event) => {
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const referenceSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "cross_references.curie"];
		const endpoint = "literature-reference";
		const filterName = "curieFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const sgdStrainBackgroundSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["name", "curie", "crossReferences.curie", "secondaryIdentifiers"];
		const endpoint = "agm";
		const filterName = "sgdStrainBackgroundFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			taxonFilter: {
				"taxon.name": {
					queryString: "Saccharomyces cerevisiae"
				}
			},
		}
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const geneticModifierSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "alleleSymbol.displayText", "alleleFullName.displayText", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "geneSynonyms.displayText", "alleleSynonyms.displayText"];
		const endpoint = "biologicalentity";
		const filterName = "geneticModifierFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const onSubjectChange = (event) => {
		if (event.target && event.target.value !== '' && event.target.value != null) {
			setIsEnabled(true);
		} else {
			setIsEnabled(false);
		}
		if(event.target.value && event.target.value.type && (event.target.value.type === "Allele" || event.target.value.type === "AffectedGenomicModel" )){
			setAsssertedGeneEnabled(true);
		}else{
			setAsssertedGeneEnabled(false);
		}
		if(event.target.value && event.target.value.type && (event.target.value.type === "AffectedGenomicModel")){
			setAsssertedAlleleEnabled(true);
		}else{
			setAsssertedAlleleEnabled(false);
		}
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const subjectSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["geneSymbol.displayText", "alleleSymbol.displayText", "geneFullName.displayText", "alleleFullName.displayText", "name", "curie", "crossReferences.curie", "secondaryIdentifiers", "alleleSynonyms.displayText", "geneSynonyms.displayText"];
		const endpoint = "biologicalentity";
		const filterName = "subjectFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const onDiseaseChange = (event) => {
		/*const curie = event.value.curie;
		const stringValue = event.value;
		const value = typeof event.value === "string" ? {curie: stringValue} : {curie};*/

		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.value
		});
	}

	const diseaseSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"];
		const endpoint = "doterm";
		const filterName = "diseaseFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
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

	const onControlledVocabChange = (event) => {
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
	const evidenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["curie", "name", "abbreviation"];
		const endpoint = "ecoterm";
		const filterName = "evidenceFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			subsetFilter: {
				"subsets": {
					queryString: "agr_eco_terms"
				}
			}
		}

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const withSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "curie", "crossReferences.curie", "secondaryIdentifiers", "geneSynonyms.displayText"];
		const endpoint = "gene";
		const filterName = "withFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			taxonFilter: {
				"taxon.curie": {
					queryString: "NCBITaxon:9606"
				}
			},
		}

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const dialogFooter = (
		<>
			<Splitter style={{border:'none', paddingTop:'12px', height:'10%'}} gutterSize="0">
				<SplitterPanel style={{textAlign: 'left', flexGrow: 0}} size={10}>
						<Button label="Clear" icon="pi pi-check" className="p-button-text" onClick={handleClear} />
						<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
				</SplitterPanel>
				<SplitterPanel style={{flexGrow: 0}} size={60}>
				</SplitterPanel>
				<SplitterPanel style={{textAlign: 'right', flexGrow: 0}} size={30}>
						<Button label="Save & Close" icon="pi pi-check" className="p-button-text" disabled={!isEnabled} onClick={handleSubmit} />
						<Button label="Save & Add Another" icon="pi pi-check" className="p-button-text" disabled={!isEnabled} onClick={handleSubmitAndAdd} />
				</SplitterPanel>
			</Splitter>
		</>
	);

	const assertedGenesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "curie", "crossReferences.curie", "secondaryIdentifiers", "geneSynonyms.displayText"];
		const endpoint = "gene";
		const filterName = "assertedGenesFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const assertedAlleleSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["alleleSymbol.displayText", "alleleFullName.displayText", "curie", "crossReferences.curie", "secondaryIdentifiers", "alleleSynonyms.displayText"];
		const endpoint = "allele";
		const filterName = "assertedAlleleFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

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
								initialValue={newAnnotation.subject}
								search={subjectSearch}
								fieldName='subject'
								name="subject"
								searchService={searchService}
								onValueChangeHandler={onSubjectChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.subject})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"subject"}/>
							<SubjectAdditionalFieldData fieldData={newAnnotation.subject}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="assertedGenes">Asserted Genes</label>
							<AutocompleteFormMultiEditor
								customRef={assertedGenesRef}
								search={assertedGenesSearch}
								name="assertedGenes"
								label="Asserted Genes"
								fieldName='assertedGenes'
								disabled = {!isAsssertedGeneEnabled}
								initialValue={newAnnotation.assertedGenes}
								onValueChangeHandler={onArrayFieldChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.assertedGenes})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"assertedGenes"}/>
							<AssertedGenesAdditionalFieldData fieldData={newAnnotation.assertedGenes}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="assertedAllele">Asserted Allele</label>
							<AutocompleteFormEditor
								customRef={assertedAlleleRef}
								search={assertedAlleleSearch}
								name="assertedAllele"
								label="Asserted Allele"
								fieldName='assertedAllele'
								disabled = {!isAsssertedAlleleEnabled}
								initialValue={newAnnotation.assertedAllele}
								onValueChangeHandler={onSingleReferenceChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.assertedAllele})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"assertedAllele"}/>
							<AssertedAlleleAdditionalFieldData fieldData={newAnnotation.assertedAllele}/>
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
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="object"><font color={'red'}>*</font>Disease</label>
							<AutocompleteFormEditor
								name="object"
								search={diseaseSearch}
								label="Disease"
								fieldName='object'
								initialValue={newAnnotation.object}
								onValueChangeHandler={onDiseaseChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.object})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"object"}/>
							<DiseaseAdditionalFieldData fieldData={newAnnotation.object}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="singleReference"><font color={'red'}>*</font>Reference</label>
							<AutocompleteFormEditor
								search={referenceSearch}
								name="singleReference"
								label="Reference"
								fieldName='singleReference'
								initialValue={newAnnotation.singleReference}
								onValueChangeHandler={onSingleReferenceChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.singleReference})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"singleReference"}/>
							<SingleReferenceAdditionalFieldData fieldData={newAnnotation.singleReference}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="evidenceCodes"><font color={'red'}>*</font>Evidence Code</label>
							<AutocompleteFormMultiEditor
								customRef={evidenceCodesRef}
								search={evidenceSearch}
								initialValue={newAnnotation.evidenceCodes}
								name="evidenceCodes"
								label="Evidence Code"
								fieldName='evidenceCodes'
								onValueChangeHandler={onArrayFieldChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<EvidenceAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.evidenceCodes})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"evidenceCodes"}/>
							<EvidenceCodesAdditionalFieldData fieldData={newAnnotation.evidenceCodes}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="with">With</label>
							<AutocompleteFormMultiEditor
								customRef={withRef}
								search={withSearch}
								name="with"
								label="With"
								fieldName='with'
								initialValue={newAnnotation.with}
								onValueChangeHandler={onArrayFieldChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.with})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"with"}/>
							<WithAdditionalFieldData fieldData={newAnnotation.with}/>
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
					<Splitter style={{border:'none', height:'10%', padding:'10px', width: '40%'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="geneticSex">Genetic Sex</label>
							<ControlledVocabularyFormDropdown
								name="geneticSex"
								options={geneticSexTerms}
								editorChange={onDropdownFieldChange}
								value={newAnnotation.geneticSex}
								className={classNames({'p-invalid': submitted && errorMessages.geneticSex})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"geneticSex"} />
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px', width: '40%'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="diseaseQualifiers">Disease Qualifiers</label>
							<ControlledVocabularyFormMultiSelectDropdown
								name="diseaseQualifiers"
								options={diseaseQualifiersTerms}
								editorChange={onControlledVocabChange}
								placeholderText="Select"
								value={newAnnotation.diseaseQualifiers}
								className={classNames({'p-invalid': submitted && errorMessages.diseaseQualifiers})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseQualifiers"} />
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px', width: '40%'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="sgdStrainBackground">SGD Strain Background</label>
							<AutocompleteFormEditor
								initialValue={newAnnotation.sgdStrainBackground}
								search={sgdStrainBackgroundSearch}
								searchService={searchService}
								fieldName='sgdStrainBackground'
								name="sgdStrainBackground"
								label="SGD Strain Background"
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								onValueChangeHandler={onSingleReferenceChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.sgdStrainBackground})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"sgdStrainBackground"}/>
							<SGDStrainBackgroundAdditionalFieldData fieldData={newAnnotation.sgdStrainBackground}/>
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px', width: '40%'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="annotationType">Annotation Type</label>
								<ControlledVocabularyFormDropdown
									name="annotationType"
									field="annotationType"
									options={annotationTypeTerms}
									editorChange={onDropdownFieldChange}
									showClear={true}
									value={newAnnotation.annotationType}
									className={classNames({'p-invalid': submitted && errorMessages.annotationType})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationType"} />
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}} size={40}>
							<label htmlFor="diseaseGeneticModifierRelation">Genetic Modifier Relation</label>
							<ControlledVocabularyFormDropdown
								name="diseaseGeneticModifierRelation"
								field="diseaseGeneticModifierRelation"
								options={geneticModifierRelationTerms}
								editorChange={onDropdownFieldChange}
								showClear={true}
								value={newAnnotation.diseaseGeneticModifierRelation}
								className={classNames({'p-invalid': submitted && errorMessages.diseaseGeneticModifierRelation})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseGeneticModifierRelation"}/>
						</SplitterPanel>
						<SplitterPanel style={{paddingRight: '10px'}} size={60}>
							<label htmlFor="diseaseGeneticModifier">Genetic Modifier</label>
							<AutocompleteFormEditor
								search={geneticModifierSearch}
								initialValue={newAnnotation.diseaseGeneticModifier}
								fieldName='diseaseGeneticModifier'
								name="diseaseGeneticModifier"
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								onValueChangeHandler={onSingleReferenceChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.diseaseGeneticModifier})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseGeneticModifier"}/>
							<GeneticModifierAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifier}/>
						</SplitterPanel>
					</Splitter>

					<Splitter style={{border:'none', height:'10%', padding:'10px', width: '40%'}} gutterSize="0">
						<SplitterPanel style={{paddingRight: '10px'}}>
							<label htmlFor="internal"><font color={'red'}>*</font>Internal</label>
							<Dropdown
								name="internal"
								value={newAnnotation.internal}
								options={booleanTerms}
								optionLabel='text'
								optionValue='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.internal})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"internal"}/>
						</SplitterPanel>
					</Splitter>

				</form>
			</Dialog>
		</div>
	);
}
