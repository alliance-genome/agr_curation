import React, { useRef } from "react";
import { ExperimentalConditionService } from "../../service/ExperimentalConditionService";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
import { InputTextarea } from "primereact/inputtextarea";
import { useMutation, useQueryClient } from "react-query";
import { AutocompleteEditor } from "../../components/Autocomplete/AutocompleteEditor";
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import {autocompleteSearch, buildAutocompleteFilter} from "../../utils/utils";
import ErrorBoundary from "../../components/Error/ErrorBoundary";


export const NewConditionForm = ({
	newConditionState,
	newConditionDispatch,
	searchService,
	experimentalConditionService,
	setNewExperimentalCondition,
	curieAutocompleteFields,
}) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newCondition, errorMessages, submitted, newConditionDialog } = newConditionState;

	const mutation = useMutation(newCondition => {
		if (!experimentalConditionService) {
			experimentalConditionService = new ExperimentalConditionService();
		}
		return experimentalConditionService.createExperimentalCondition(newCondition);
	});


	const hideDialog = () => {
		newConditionDispatch({ type: "RESET" });
	};

	const handleSubmit = (event) => {
		event.preventDefault();
		newConditionDispatch({type: "SUBMIT"});
		mutation.mutate(newCondition, {
			onSuccess: (data) => {
				setNewExperimentalCondition(data.data.entity);
				queryClient.invalidateQueries('ConditionRelationHandles');
				toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Relation Added'});
				newConditionDispatch({type: "RESET"});
			},
			onError: (error) => {

				const message =
					error.response.data.errorMessages.uniqueId ?
					"Page Error: New experimental condition is a duplicate of an existing experimental condition" :
					error.response.data.errorMessage;

				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false}
				]);
				newConditionDispatch({type: "UPDATE_ERROR_MESSAGES", errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const onCurieFieldChange = (event, setFieldValue) => {
		const curie = event.value.curie;
		const stringValue = event.value;
		const value = typeof event.value === "string" ? {curie: stringValue} : {curie};
		setFieldValue(event.target.value);
		newConditionDispatch({
			type: "EDIT",
			field: event.target.name,
			value,
		});
	}

	const conditionClassSearch = (event, setFiltered, setQuery) => {
		const endpoint = "zecoterm";
		const filterName = "conditionClassEditorFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		const otherFilters = {
			"subsetFilter": {
				"subsets": {
					queryString: 'ZECO_0000267'
				}
			}
		}
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const conditionIdSearch = (event, setFiltered, setQuery) => {
		const endpoint = "experimentalconditionontologyterm";
		const filterName = "singleOntologyFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionGeneOntologySearch = (event, setFiltered, setQuery) => {
		const endpoint = "goterm";
		const filterName = "singleOntologyFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionChemicalSearch = (event, setFiltered, setQuery) => {
		const endpoint = "chemicalterm";
		const filterName = "singleOntologyFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		setQuery(event.query)
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionAnatomySearch = (event, setFiltered, setQuery) => {
		const endpoint = "anatomicalterm";
		const filterName = "singleOntologyFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionTaxonSearch = (event, setFiltered, setQuery) => {
		const endpoint = "ncbitaxonterm";
		const filterName = "singleOntologyFilter";
		const filter = buildAutocompleteFilter(event, curieAutocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const onInternalChange = (event) => {
		newConditionDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.value,
		});
	}

	const onTextChange = (event) => {
		newConditionDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	};


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
			<Dialog visible={newConditionDialog} style={{ width: '450px' }} header="Add Relation" modal className="p-fluid" footer={dialogFooter} onHide={hideDialog} resizeable >
				<ErrorBoundary>
					<div className='p-justify-center'>
						<form>
							<div className="field">
								<label htmlFor="conditionClass">Condition Class</label>
								<AutocompleteEditor
									name="conditionClass"
									search={conditionClassSearch}
									initialValue={newCondition.conditionClass.curie}
									fieldName="conditionClass"
									onValueChangeHandler={onCurieFieldChange}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionClass})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionClass"}/>
							</div>
							<div className="field">
								<label htmlFor="conditionId">Condition Term</label>
								<AutocompleteEditor
									name="conditionId"
									search={conditionIdSearch}
									initialValue={newCondition.conditionId.curie}
									fieldname={"conditionId"}
									onValueChangeHandler={onCurieFieldChange}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionId})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionId"}/>
							</div>
							<div className="field">
								<label htmlFor="conditionGeneOntology">Gene Ontology</label>
								<AutocompleteEditor
									name="conditionGeneOntology"
									search={conditionGeneOntologySearch}
									initialValue={newCondition.conditionGeneOntology.curie}
									onValueChangeHandler={onCurieFieldChange}
									fieldname={"conditionGeneOntology"}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionGeneOntology})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionGeneOntology"}/>
							</div>
							<div className="field">
								<label htmlFor="conditionChemical">Chemical</label>
								<AutocompleteEditor
									name="conditionChemical"
									search={conditionChemicalSearch}
									initialValue={newCondition.conditionChemical.curie}
									fieldname={"conditionChemical"}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionChemical})}
									onValueChangeHandler={onCurieFieldChange}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionChemical"}/>
							</div>
							<div className="field">
								<label htmlFor="conditionAnatomy">Anatomy</label>
								<AutocompleteEditor
									name="conditionAnatomy"
									initialValue={newCondition.conditionAnatomy.curie}
									search={conditionAnatomySearch}
									onValueChangeHandler={onCurieFieldChange}
									fieldname={"conditionAnatomy"}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionAnatomy})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionAnatomy"}/>
							</div>

							<div className="field">
								<label htmlFor="conditionTaxon">Taxon</label>
								<AutocompleteEditor
									name="conditionTaxon"
									initialValue={newCondition.conditionTaxon.curie}
									search={conditionTaxonSearch}
									onValueChangeHandler={onCurieFieldChange}
									fieldname={"conditionTaxon"}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionTaxon})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionTaxon"}/>
							</div>

							<div className="field">
								<label htmlFor="conditionQuantity">Quantity</label>
								<InputText
									id="conditionQuantity"
									name="conditionQuantity"
									value={newCondition.conditionQuantity}
									onChange={onTextChange}
									className={classNames({'p-invalid': submitted && errorMessages.conditionQuantity})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionQuantity"}/>
							</div>


							<div className="field">
								<label htmlFor="internal">Internal</label>
								<Dropdown
									id="internal"
									name="internal"
									value={newCondition.internal}
									options={[
										{ label: 'true', value: true },
										{ label: 'false', value: false }
									]}
									onChange={onInternalChange}
									className={classNames({'p-invalid': submitted && errorMessages.internal})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"internal"}/>
							</div>

							<div className="field">
								<label htmlFor="conditionFreeText">Free Text</label>
								<InputTextarea
									id="conditionFreeText"
									name="conditionFreeText"
									value={newCondition.conditionFreeText}
									onChange={onTextChange}
									className={classNames({'p-invalid': submitted && errorMessages.conditionFreeText})}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionFreeText"}/>
							</div>
						</form>
					</div>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
}
