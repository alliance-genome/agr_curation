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
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";


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
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				newConditionDispatch({type: "UPDATE_ERROR_MESSAGES", errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const onCurieFieldChange = (event, setFieldValue) => {
		const curie = event.value.curie;
		const stringValue = event.value;
		const value = typeof event.value === "string" ? {curie: stringValue} : {curie};
		newConditionDispatch({
			type: "EDIT",
			field: event.target.name,
			value,
		});
		setFieldValue(event.target.value);
	}

	const onStatementChange = (event, setFieldValue) => {
		const value = typeof event.value === "object" ? event.value.conditionStatement : event.value;
		newConditionDispatch({
			type: "EDIT",
			field: event.target.name,
			value,
		});
		setFieldValue(value);
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
				<div className='p-justify-center'>
					<form>
						<div className="field">
							<label htmlFor="conditionClass">Condition Class</label>
							<AutocompleteEditor
								autocompleteFields={curieAutocompleteFields}
								searchService={searchService}
								name="conditionClass"
								label="Condition Class"
								endpoint='zecoterm'
								filterName='conditionClassEditorFilter'
								fieldName='conditionClass'
								value={newCondition.conditionClass}
								passedOnChange={onCurieFieldChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.conditionClass})}
								otherFilters={{
									"obsoleteFilter": {
										"obsolete": {
											queryString: false
										}
									},
									"subsetFilter": {
										"subsets": {
											queryString: 'ZECO_0000267'
										}
									}
								}}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionClass"}/>
						</div>
						<div className="field">
							<label htmlFor="conditionStatement">Statement</label>
								<AutocompleteEditor
									autocompleteFields={["conditionStatement"]}
									searchService={searchService}
									name="conditionStatement"
									endpoint='experimental-condition'
									filterName='conditionStatementFilter'
									fieldName='conditions'
									subField='conditionStatement'
									passedOnChange={onStatementChange}
									classNames={classNames({'p-invalid': submitted && errorMessages.conditionStatement})}
									valueDisplay={(item, setAutocompleteSelectedItem, op, query) => 
										<ExConAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
								/>
								<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionStatement"}/>
							</div>

		
						<div className="field">
							<label htmlFor="conditionId">Condition Term</label>
							<AutocompleteEditor
								name="conditionId"
								autocompleteFields={curieAutocompleteFields}
								searchService={searchService}
								passedOnChange={onCurieFieldChange}
								fieldname={"conditionId"}
								endpoint={"experimentalconditionontologyterm"}
								filterName='singleOntologyFilter'
								classNames={classNames({'p-invalid': submitted && errorMessages.conditionId})}
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionId"}/>
						</div>
						<div className="field">
							<label htmlFor="conditionGeneOntology">Gene Ontology</label>
							<AutocompleteEditor
								name="conditionGeneOntology"
								autocompleteFields={curieAutocompleteFields}
								searchService={searchService}
								passedOnChange={onCurieFieldChange}
								fieldname={"conditionGeneOntology"}
								endpoint={"goterm"}
								filterName='singleOntologyFilter'
								classNames={classNames({'p-invalid': submitted && errorMessages.conditionGeneOntology})}
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionGeneOntology"}/>
						</div>
						<div className="field">
							<label htmlFor="conditionChemical">Chemical</label>
							<AutocompleteEditor
								name="conditionChemical"
								autocompleteFields={curieAutocompleteFields}
								searchService={searchService}
								passedOnChange={onCurieFieldChange}
								fieldname={"conditionChemical"}
								endpoint={"chemicalterm"}
								filterName='singleOntologyFilter'
								classNames={classNames({'p-invalid': submitted && errorMessages.conditionChemical})}
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionChemical"}/>
						</div>
						<div className="field">
							<label htmlFor="conditionAnatomy">Anatomy</label>
							<AutocompleteEditor
								name="conditionAnatomy"
								autocompleteFields={curieAutocompleteFields}
								searchService={searchService}
								passedOnChange={onCurieFieldChange}
								fieldname={"conditionAnatomy"}
								endpoint={"anatomicalterm"}
								filterName='singleOntologyFilter'
								classNames={classNames({'p-invalid': submitted && errorMessages.conditionAnatomy})}
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionAnatomy"}/>
						</div>
		
						<div className="field">
							<label htmlFor="conditionTaxon">Taxon</label>
							<AutocompleteEditor
								name="conditionTaxon"
								autocompleteFields={curieAutocompleteFields}
								searchService={searchService}
								passedOnChange={onCurieFieldChange}
								fieldname={"conditionTaxon"}
								endpoint={"ncbitaxonterm"}
								filterName='singleOntologyFilter'
								classNames={classNames({'p-invalid': submitted && errorMessages.conditionTaxon})}
								otherFilters={{
									obsoleteFilter: {
										"obsolete": {
											queryString: false
										}
									}
								}}
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
			</Dialog>
		</div>
	);
}
