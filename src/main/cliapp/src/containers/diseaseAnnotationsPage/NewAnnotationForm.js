import React, { useRef, useState } from "react";
import { ValidationService } from '../../service/ValidationService';
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
// import { MultiSelect } from 'primereact/multiselect';
import { useMutation, useQueryClient } from "react-query";
import { FormErrorMessageComponent } from "../../components/Error/FormErrorMessageComponent";
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
import { autocompleteSearch, buildAutocompleteFilter, validateFormBioEntityFields } from "../../utils/utils";
import { AutocompleteFormMultiEditor } from "../../components/Autocomplete/AutocompleteFormMultiEditor";
import { SubjectAdditionalFieldData } from "../../components/FieldData/SubjectAdditionalFieldData";
import { AssertedAlleleAdditionalFieldData } from "../../components/FieldData/AssertedAlleleAdditionalFieldData";
import { DiseaseAdditionalFieldData } from "../../components/FieldData/DiseaseAdditionalFieldData";
import { SingleReferenceAdditionalFieldData } from "../../components/FieldData/SingleReferenceAdditionalFieldData";
import { SGDStrainBackgroundAdditionalFieldData } from "../../components/FieldData/SGDStrainBackgroundAdditionalFieldData";
import { AssertedGenesAdditionalFieldData } from "../../components/FieldData/AssertedGenesAdditionalFieldData";
import { EvidenceCodesAdditionalFieldData } from "../../components/FieldData/EvidenceCodesAdditionalFieldData";
import { WithAdditionalFieldData } from "../../components/FieldData/WithAdditionalFieldData";
import { GeneticModifiersAdditionalFieldData } from "../../components/FieldData/GeneticModifiersAdditionalFieldData";
import ErrorBoundary from "../../components/Error/ErrorBoundary";
// import { ConfirmButton } from "../../components/ConfirmButton";

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
	// const [selectedFields, setSelectedFields] = useState([]);
	const {
		newAnnotation,
		errorMessages,
		relatedNotesErrorMessages,
		relatedNotesEditingRows,
		exConErrorMessages,
		conditionRelationsEditingRows,
		submitted,
		newAnnotationDialog,
		showRelatedNotes,
		showConditionRelations,
		isEnabled,
		isAssertedGeneEnabled,
		isAssertedAlleleEnabled,
	} = newAnnotationState;
	const validationService = new ValidationService();
	const geneticSexTerms = useControlledVocabularyService('genetic_sex');
	const diseaseQualifiersTerms = useControlledVocabularyService('disease_qualifier');
	const annotationTypeTerms = useControlledVocabularyService('annotation_type');
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('disease_genetic_modifier_relation');
	const [uiErrorMessages, setUiErrorMessages] = useState({});
	const areUiErrors = useRef(false);

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
		newAnnotationDispatch({ type: "SET_IS_ENABLED", value: false });
		newAnnotationDispatch({ type: "SET_IS_ASSERTED_ALLELE_ENABLED", value: false });
		newAnnotationDispatch({ type: "SET_IS_ASSERTED_GENE_ENABLED", value: false });
		setUiErrorMessages({});
	};

	const validateTable = async (endpoint, errorType, table) => {
		if(!table) return false;
		const results = await validate(table, endpoint);
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
		const isRelatedNotesErrors = await validateTable(
			"note", 
			"relatedNotesErrorMessages", 
			newAnnotation.relatedNotes, 
			newAnnotationDispatch
		);
		const isExConErrors = await validateTable(
			"condition-relation", 
			"exConErrorMessages", 
			newAnnotation.conditionRelations, 
			newAnnotationDispatch
		);

		areUiErrors.current = false;
		validateFormBioEntityFields(newAnnotation, uiErrorMessages, setUiErrorMessages, areUiErrors);
		if (areUiErrors.current) {
			newAnnotationDispatch({type: "UPDATE_ERROR_MESSAGES", errorType: "errorMessages", errorMessages: uiErrorMessages});
			newAnnotationDispatch({ type: "SET_IS_ENABLED", value: false });

			return;
		}

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

				let message; 
				if(error?.response?.data?.errorMessages?.uniqueId){
					message = "Page Error: New annotation is a duplicate of an existing annotation";
				} else if(error?.response?.data?.errorMessage){
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`
				}
					

				toast_error.current.show({severity: 'error', summary: 'Page error: ', detail: message });

				newAnnotationDispatch(
					{
						type: "UPDATE_ERROR_MESSAGES", 
						errorType: "errorMessages", 
						errorMessages: error.response?.data?.errorMessages || {}
					}
				);
			}
		});
	};

	const handleClear = () => {
		//this manually resets the value of the input text in autocomplete fields with multiple values and the experiments dropdown
		if(withRef.current.getInput()) withRef.current.getInput().value = "";
		if(evidenceCodesRef.current.getInput().value) evidenceCodesRef.current.getInput().value = "";
		newAnnotationDispatch({ type: "CLEAR" });
		newAnnotationDispatch({ type: "SET_IS_ENABLED", value: false });
		newAnnotationDispatch({ type: "SET_IS_ASSERTED_GENE_ENABLED", value: false });
		newAnnotationDispatch({ type: "SET_IS_ASSERTED_ALLELE_ENABLED", value: false });

		setUiErrorMessages({});
	}

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event, false);
	}

	const onSingleReferenceChange = (event) => {
		setUiErrorMessages({});
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
		const autocompleteFields = ["name", "curie", "crossReferences.referencedCurie"];
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

	const geneticModifiersSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "alleleSymbol.displayText", "alleleFullName.displayText", "name", "curie", "crossReferences.referencedCurie", "alleleSecondaryIds.secondaryId", "geneSynonyms.displayText", "alleleSynonyms.displayText"];
		const endpoint = "biologicalentity";
		const filterName = "geneticModifiersFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const onSubjectChange = (event) => {
		setUiErrorMessages({});
		if (event.target && event.target.value !== '' && event.target.value != null) {
			newAnnotationDispatch({ type: "SET_IS_ENABLED", value: true });
		} else {
			newAnnotationDispatch({ type: "SET_IS_ENABLED", value: false });
		}
		if(event.target.value && event.target.value.type && (event.target.value.type === "Allele" || event.target.value.type === "AffectedGenomicModel" )){
			newAnnotationDispatch({ type: "SET_IS_ASSERTED_GENE_ENABLED", value: true });
		}else{
			newAnnotationDispatch({ type: "SET_IS_ASSERTED_GENE_ENABLED", value: false });
		}
		if(event.target.value && event.target.value.type && (event.target.value.type === "AffectedGenomicModel")){
			newAnnotationDispatch({ type: "SET_IS_ASSERTED_ALLELE_ENABLED", value: true });
		}else{
			newAnnotationDispatch({ type: "SET_IS_ASSERTED_ALLELE_ENABLED", value: false });
		}
		newAnnotationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const subjectSearch = (event, setFiltered, setQuery) => {
		//The order of the below fields are as per the Autocomplete search result
		const autocompleteFields = [
			"geneSymbol.displayText",
			"alleleSymbol.displayText",
			"name",
			"geneFullName.displayText",
			"alleleFullName.displayText",
			"alleleSynonyms.displayText",
			"geneSynonyms.displayText",
			"curie",
			"crossReferences.referencedCurie",
			"alleleSecondaryIds.secondaryId",
		];
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
		const autocompleteFields = ["curie", "name", "crossReferences.referencedCurie", "secondaryIdentifiers", "synonyms.name"];
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
			newAnnotation.conditionRelations?.[0]
			&& newAnnotation.conditionRelations?.[0].handle
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
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "curie", "crossReferences.referencedCurie", "geneSynonyms.displayText"];
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
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "curie", "crossReferences.referencedCurie", "geneSynonyms.displayText"];
		const endpoint = "gene";
		const filterName = "assertedGenesFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const assertedAlleleSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["alleleSymbol.displayText", "alleleFullName.displayText", "curie", "crossReferences.referencedCurie", "alleleSecondaryIds.secondaryId", "alleleSynonyms.displayText"];
		const endpoint = "allele";
		const filterName = "assertedAlleleFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	// const defaultFields = (["Subject", "Asserted Genes", "Asserted Allele", "Disease Relation", "Negated", "Disease", "Reference", "Evidence Code", "With", "Related Notes",
	// 	"Experimental Conditions", "Experiments", "Genetic Sex", "Disease Qualifiers", "SGD Strain Background", "Annotation Type", "Genetic Modifier Relation",
	// 	"Genetic Modifiers", "Internal"]);

	const dialogHeader = (
	<>
		<Splitter style={{border:'none', height:'5%'}} gutterSize="0">
			<SplitterPanel size={25} style={{textAlign: 'left'}}>
				<h4>Add Annotation</h4>
			</SplitterPanel>
			{/*<SplitterPanel size={10} style={{textAlign: 'right', padding: '5px'}}>
				<MultiSelect
					aria-label='columnToggle'
					options={defaultFields}
					value={selectedFields}
					display="chip"
					onChange={(e) => setSelectedFields(e.value)}
					style={{ width: '20em', textAlign: 'center' }}
				/>
			</SplitterPanel>*/}
			{/*<SplitterPanel size={20} style={{textAlign: 'right', padding: '5px'}}>
				<Button label="Show all fields"/>
			</SplitterPanel>*/}
			{/*<SplitterPanel size={20} style={{textAlign: 'right', padding: '5px'}}>
				<ConfirmButton
					buttonText="Set MOD Defaults"
					messageText= {`Are you sure? This will reset to MOD default settings.`}
				/>
			</SplitterPanel>*/}
		</Splitter>
	</>
	);

	const labelColumnSize = "col-3";
	const widgetColumnSize = "col-4";
	const fieldDetailsColumnSize = "col-5";

	return(
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog visible={newAnnotationDialog} style={{ width: '900px' }} header={dialogHeader} modal className="p-fluid" footer={dialogFooter} onHide={hideDialog} resizeable>
				<ErrorBoundary>
				<form>
					<div className="grid">
						<div className={labelColumnSize}>
					 		<label htmlFor="subject"><font color={'red'}>*</font>Subject</label>
						</div>
						<div className={widgetColumnSize}>
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
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"subject"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"subject"}/>
							<SubjectAdditionalFieldData fieldData={newAnnotation.subject}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="assertedGenes">Asserted Genes</label>
						</div>
						<div className={widgetColumnSize}>
							<AutocompleteFormMultiEditor
								customRef={assertedGenesRef}
								search={assertedGenesSearch}
								name="assertedGenes"
								label="Asserted Genes"
								fieldName='assertedGenes'
								disabled = {!isAssertedGeneEnabled}
								initialValue={newAnnotation.assertedGenes}
								onValueChangeHandler={onArrayFieldChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.assertedGenes})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"assertedGenes"}/>
							<AssertedGenesAdditionalFieldData fieldData={newAnnotation.assertedGenes}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="assertedAllele">Asserted Allele</label>
						</div>
						<div className={widgetColumnSize}>
							<AutocompleteFormEditor
								customRef={assertedAlleleRef}
								search={assertedAlleleSearch}
								name="assertedAllele"
								label="Asserted Allele"
								fieldName='assertedAllele'
								disabled = {!isAssertedAlleleEnabled}
								initialValue={newAnnotation.assertedAllele}
								onValueChangeHandler={onSingleReferenceChange}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								classNames={classNames({'p-invalid': submitted && errorMessages.assertedAllele})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"assertedAllele"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"assertedAllele"}/>
							<AssertedAlleleAdditionalFieldData fieldData={newAnnotation.assertedAllele}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="diseaseRelation"><font color={'red'}>*</font>Disease Relation</label>
						</div>
						<div className={widgetColumnSize}>
							<Dropdown
								options={diseaseRelationsTerms}
								value={newAnnotation.diseaseRelation}
								name="diseaseRelation"
								optionLabel='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.diseaseRelation})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseRelation"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="negated">Negated</label>
						</div>
						<div className={widgetColumnSize}>
							<Dropdown
								name="negated"
								value={newAnnotation.negated}
								options={negatedTerms}
								optionLabel='text'
								optionValue='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.negated})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"negated"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="object"><font color={'red'}>*</font>Disease</label>
						</div>
						<div className={widgetColumnSize}>
							<AutocompleteFormEditor
								name="object"
								search={diseaseSearch}
								label="Disease"
								fieldName='object'
								initialValue={newAnnotation.object}
								onValueChangeHandler={onDiseaseChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.object})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"object"}/>
							<DiseaseAdditionalFieldData fieldData={newAnnotation.object}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="singleReference"><font color={'red'}>*</font>Reference</label>
						</div>
						<div className={widgetColumnSize}>
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
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"singleReference"}/>
							<SingleReferenceAdditionalFieldData fieldData={newAnnotation.singleReference}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="evidenceCodes"><font color={'red'}>*</font>Evidence Code</label>
						</div>
						<div className={widgetColumnSize}>
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
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"evidenceCodes"}/>
							<EvidenceCodesAdditionalFieldData fieldData={newAnnotation.evidenceCodes}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="with">With</label>
						</div>
						<div className={widgetColumnSize}>
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
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"with"}/>
							<WithAdditionalFieldData fieldData={newAnnotation.with}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label>Related Notes</label>
						</div>
						<div className={classNames('col-9', {'border-2 border-red-300': submitted && errorMessages.relatedNotes && Object.keys(relatedNotesErrorMessages).length === 0})}>
							<RelatedNotesForm
								dispatch={newAnnotationDispatch}
								relatedNotes={newAnnotation.relatedNotes}
								showRelatedNotes={showRelatedNotes}
								errorMessages={relatedNotesErrorMessages}
								editingRows={relatedNotesEditingRows}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"relatedNotes"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"relatedNotes"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label>Experimental Conditions</label>
						</div>
						<div className="col-6">
							<ConditionRelationsForm
								dispatch={newAnnotationDispatch}
								conditionRelations={newAnnotation.conditionRelations}
								showConditionRelations={showConditionRelations}
								errorMessages={exConErrorMessages}
								searchService={searchService}
								buttonIsDisabled={isConditionRelationButtonEnabled()}
								editingRows={conditionRelationsEditingRows}
							/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="experiments">Experiments</label>
						</div>
						<div className={widgetColumnSize}>
							<ConditionRelationHandleFormDropdown
								name="experiments"
								customRef={experimentsRef}
								editorChange={onDropdownExperimentsFieldChange}
								referenceCurie={newAnnotation.singleReference?.curie}
								value={newAnnotation.conditionRelations?.[0]?.handle}
								showClear={true}
								placeholderText={newAnnotation.conditionRelations?.[0]?.handle}
								isEnabled={isExperimentEnabled()}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionRelations[0]?.handle"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="geneticSex">Genetic Sex</label>
						</div>
						<div className={widgetColumnSize}>
							<ControlledVocabularyFormDropdown
								name="geneticSex"
								options={geneticSexTerms}
								editorChange={onDropdownFieldChange}
								value={newAnnotation.geneticSex}
								showClear={true}
								className={classNames({'p-invalid': submitted && errorMessages.geneticSex})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"geneticSex"} />
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="diseaseQualifiers">Disease Qualifiers</label>
						</div>
						<div className={widgetColumnSize}>
							<ControlledVocabularyFormMultiSelectDropdown
								name="diseaseQualifiers"
								options={diseaseQualifiersTerms}
								editorChange={onControlledVocabChange}
								value={newAnnotation.diseaseQualifiers}
								className={classNames({'p-invalid': submitted && errorMessages.diseaseQualifiers})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseQualifiers"} />
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="sgdStrainBackground">SGD Strain Background</label>
						</div>
						<div className={widgetColumnSize}>
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
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"sgdStrainBackground"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"sgdStrainBackground"}/>
							<SGDStrainBackgroundAdditionalFieldData fieldData={newAnnotation.sgdStrainBackground}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="annotationType">Annotation Type</label>
						</div>
						<div className={widgetColumnSize}>
							<ControlledVocabularyFormDropdown
								name="annotationType"
								field="annotationType"
								options={annotationTypeTerms}
								editorChange={onDropdownFieldChange}
								showClear={true}
								value={newAnnotation.annotationType}
								className={classNames({'p-invalid': submitted && errorMessages.annotationType})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"annotationType"} />
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="diseaseGeneticModifierRelation">Genetic Modifier Relation</label>
						</div>
						<div className={widgetColumnSize}>
							<ControlledVocabularyFormDropdown
								name="diseaseGeneticModifierRelation"
								field="diseaseGeneticModifierRelation"
								options={geneticModifierRelationTerms}
								editorChange={onDropdownFieldChange}
								showClear={true}
								value={newAnnotation.diseaseGeneticModifierRelation}
								className={classNames({'p-invalid': submitted && errorMessages.diseaseGeneticModifierRelation})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseGeneticModifierRelation"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"diseaseGeneticModifierRelation"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="diseaseGeneticModifier">Genetic Modifiers</label>
						</div>
						<div className={widgetColumnSize}>
							<AutocompleteFormMultiEditor
								search={geneticModifiersSearch}
								initialValue={newAnnotation.diseaseGeneticModifiers}
								fieldName='diseaseGeneticModifiers'
								name="diseaseGeneticModifiers"
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								onValueChangeHandler={onArrayFieldChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.diseaseGeneticModifiers})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseGeneticModifiers"}/>
							<GeneticModifiersAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifiers}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="internal"><font color={'red'}>*</font>Internal</label>
						</div>
						<div className={widgetColumnSize}>
							<Dropdown
								name="internal"
								value={newAnnotation.internal}
								options={booleanTerms}
								optionLabel='text'
								optionValue='name'
								onChange={onDropdownFieldChange}
								className={classNames({'p-invalid': submitted && errorMessages.internal})}
							/>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"internal"}/>
						</div>
					</div>
				</form>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
}
