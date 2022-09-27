import React, { useRef } from "react";
import { ConditionRelationService } from "../../service/ConditionRelationService";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Toast } from "primereact/toast";
import { useMutation, useQueryClient } from "react-query";
import { AutocompleteRowEditor } from "../../components/Autocomplete/AutocompleteRowEditor";
import { LiteratureAutocompleteTemplate } from "../../components/Autocomplete/LiteratureAutocompleteTemplate";
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import { classNames } from "primereact/utils";
import {AutocompleteFormEditor} from "../../components/Autocomplete/AutocompleteFormEditor";


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

	const onReferenceChange = (event) => {
		newRelationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
	}

	const onConditionsChange = (event, setFieldValue) => {
		newRelationDispatch({
			type: "EDIT",
			field: event.target.name,
			value: event.target.value
		});
		setFieldValue(event.target.value);
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
							<AutocompleteFormEditor
								autocompleteFields={["curie", "cross_references.curie"]}
								searchService={searchService}
								name="singleReference"
								label="Reference"
								endpoint='literature-reference'
								filterName='curieFilter'
								fieldName='singleReference'
								isReference={true}
								value={newRelation.singleReference}
								onValueChangeHandler={onReferenceChange}
								classNames={classNames({'p-invalid': submitted && errorMessages.singleReference})}
								valueDisplayHandler={(item, setAutocompleteSelectedItem, op, query) =>
									<LiteratureAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
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
							<AutocompleteRowEditor
								name="conditions"
								autocompleteFields={["conditionSummary"]}
								searchService={searchService}
								endpoint='experimental-condition'
								filterName='experimentalConditionFilter'
								fieldName='conditions'
								subField='conditionSummary'
								isMultiple={true}
								classNames={classNames({'p-invalid': submitted && errorMessages.conditions})}
								passedOnChange={onConditionsChange}
								valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
									<ExConAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
							/>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"conditions"}/>
						</div>
					</form>
				</div>
			</Dialog>
		</div>
	);
}
