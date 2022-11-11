import React, { useRef } from "react";
import { ConditionRelationService } from "../../service/ConditionRelationService";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
import { useMutation, useQueryClient } from "react-query";
import { AutocompleteEditor } from "../../components/Autocomplete/AutocompleteEditor";
import { LiteratureAutocompleteTemplate } from "../../components/Autocomplete/LiteratureAutocompleteTemplate";
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import {autocompleteSearch, buildAutocompleteFilter} from "../../utils/utils";
import {AutocompleteMultiEditor} from "../../components/Autocomplete/AutocompleteMultiEditor";


export const NewRelationForm = ({
	newRelationState,
	newRelationDispatch,
	searchService,
	conditionRelationService,
	conditionRelationTypeTerms,
	setNewConditionRelation,
}) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newRelation, errorMessages, submitted, newRelationDialog } = newRelationState;


	const hideDialog = () => {
		newRelationDispatch({ type: "RESET" });
	};

	const mutation = useMutation(newRelation => {
		if (!conditionRelationService) {
			conditionRelationService = new ConditionRelationService();
		}
		return conditionRelationService.createConditionRelation(newRelation);
	});

	const handleSubmit = (event) => {
		event.preventDefault();
		newRelationDispatch({type: "SUBMIT"});
		mutation.mutate(newRelation, {
			onSuccess: (data) => {
				setNewConditionRelation(data.data.entity);
				queryClient.invalidateQueries('ConditionRelationHandles');
				toast_success.current.show({severity: 'success', summary: 'Successful', detail: 'New Relation Added'});
				newRelationDispatch({type: "RESET"});
			},
			onError: (error) => {
				toast_error.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.response.data.errorMessage, sticky: false}
				]);
				newRelationDispatch({type: "UPDATE_ERROR_MESSAGES", errorMessages: error.response.data.errorMessages});
			}
		});
	};

	const onHandleChange = (event) => {
		newRelationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	};

	const onRelationChange = (event) => {
		const name = event.value;
		newRelationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: {name},
		});
	}

	const onReferenceChange = (event, setFieldValue) => {
		setFieldValue(event.target.value);
		newRelationDispatch({
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

	const onConditionsChange = (event, setFieldValue) => {
		setFieldValue(event.target.value);
		newRelationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const conditionSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["conditionSummary"];
		const endpoint = "experimental-condition";
		const filterName = "experimentalConditionFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
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
			<Dialog visible={newRelationDialog} style={{ width: '450px' }} header="Add Relation" modal className="p-fluid" footer={dialogFooter} onHide={hideDialog} resizeable >
				<div className='p-justify-center'>
					<form>
						<div className="field">
							<label htmlFor="handle">Handle</label>
							<InputText
								id="handle"
								name="handle"
								value={newRelation.handle}
								onChange={onHandleChange}
								className={classNames({'p-invalid': submitted && errorMessages.handle})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"handle"}/>
						</div>
						<div className="field">
							<label htmlFor="singleReference"><font color={'red'}>*</font>Reference</label>
							<AutocompleteEditor
								search={referenceSearch}
								name="singleReference"
								label="Reference"
								fieldName='singleReference'
								initialValue={newRelation.singleReference}
								onValueChangeHandler={onReferenceChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.singleReference})}
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"singleReference"}/>
						</div>
						<div className="field">
							<label htmlFor="relation">Relation</label>
							<Dropdown
									options={conditionRelationTypeTerms}
									value={newRelation.conditionRelationType.name}
									placeholder={"Select Relation"}
									name="conditionRelationType"
									optionLabel='name'
									optionValue='name'
									onChange={onRelationChange}
									required
									className={classNames({'p-invalid': submitted && errorMessages.conditionRelationType})}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditionRelationType"}/>
						</div>
						<div className="field">
							<label htmlFor="conditions">Conditions</label>
							<AutocompleteMultiEditor
								search={conditionSearch}
								initialValue={newRelation.conditions}
								fieldName='conditions'
								subField='conditionSummary'
								name="conditions"
								valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
									<ExConAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
								onValueChangeHandler={onConditionsChange}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditions"}/>
						</div>
					</form>
				</div>
			</Dialog>
		</div>
	);
}
