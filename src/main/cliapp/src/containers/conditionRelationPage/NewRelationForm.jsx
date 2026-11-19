import React, { useRef } from 'react';
import { ConditionRelationService } from '../../service/ConditionRelationService';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { FormField } from '../../components/Editors/fields/FormField';
import { DIALOG_FIELD_LAYOUT } from '../../components/Editors/fields/dialogFieldLayout';
import { TextInput } from '../../components/Editors/widgets/TextInput';
import { SingleAutocomplete } from '../../components/Editors/widgets/SingleAutocomplete';
import { MultiAutocomplete } from '../../components/Editors/widgets/MultiAutocomplete';
import { VocabularySelect } from '../../components/Editors/widgets/VocabularySelect';
import {
	singleReferenceSearch,
	singleReferenceSearchConfig,
} from '../../components/Editors/autocomplete/references/utils';
import {
	conditionsSearch,
	conditionsSearchConfig,
} from '../../components/Editors/autocomplete/experimentalCondition/utils';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

/**
 * The New Relation dialog on the Condition Relations table: handle, reference, relation type and conditions. Save
 * with no reference says beside it that it is required and sends nothing; otherwise it creates the relation, and a
 * rejected save shows each field's error beside it.
 *
 * @param {object} newRelationState - the form's reducer state: `newRelation`, `errorMessages`, `submitted`,
 *   `newRelationDialog`
 * @param {Function} newRelationDispatch - the form's reducer dispatch
 * @param {object} [conditionRelationService] - the service that creates the relation
 * @param {object[]} conditionRelationTypeTerms - the `condition_relation` terms offered as the relation type
 * @param {(relation: object, queryClient: object) => void} setNewConditionRelation - called with the created
 *   relation
 * @returns {JSX.Element}
 */
export const NewRelationForm = ({
	newRelationState,
	newRelationDispatch,
	conditionRelationService,
	conditionRelationTypeTerms,
	setNewConditionRelation,
}) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newRelation, errorMessages, submitted, newRelationDialog } = newRelationState;
	const isReferenceMissing = !newRelation.singleReference?.curie?.trim();
	const errors =
		submitted && isReferenceMissing ? { ...errorMessages, singleReference: 'Reference is required.' } : errorMessages;

	const hideDialog = () => {
		newRelationDispatch({ type: 'RESET' });
	};

	const mutation = useMutation({
		mutationFn: (newRelation) => {
			if (!conditionRelationService) {
				conditionRelationService = new ConditionRelationService();
			}
			return conditionRelationService.createConditionRelation(newRelation);
		},
	});

	const handleSubmit = (event) => {
		event.preventDefault();
		newRelationDispatch({ type: 'SUBMIT' });
		if (isReferenceMissing) return;
		mutation.mutate(newRelation, {
			onSuccess: (data) => {
				setNewConditionRelation(data.data.entity, queryClient);
				toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Relation Added' });
				newRelationDispatch({ type: 'RESET' });
			},
			onError: (error) => {
				const message = error.response.data.errorMessages.uniqueId
					? 'Page Error: New relation is a duplicate of an existing relation'
					: error.response.data.errorMessage;

				toast_error.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
				]);
				newRelationDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: error.response.data.errorMessages });
			},
		});
	};

	const fieldProps = (field) => ({
		name: field,
		value: newRelation[field],
		onChange: (value) => newRelationDispatch({ type: 'EDIT', field, value }),
		errors,
		...DIALOG_FIELD_LAYOUT,
	});

	const dialogFooter = (
		<>
			<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
			<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
		</>
	);

	return (
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog
				visible={newRelationDialog}
				style={{ width: '450px' }}
				header="Add Relation"
				modal
				className="p-fluid"
				footer={dialogFooter}
				onHide={hideDialog}
			>
				<ErrorBoundary>
					<form>
						<FormField label="Handle" {...fieldProps('handle')}>
							{(binding) => <TextInput {...binding} />}
						</FormField>
						<FormField label="Reference" required {...fieldProps('singleReference')}>
							{(binding) => (
								<SingleAutocomplete
									{...binding}
									search={singleReferenceSearch}
									valueDisplay={singleReferenceSearchConfig.valueDisplay}
								/>
							)}
						</FormField>
						<FormField label="Relation" {...fieldProps('conditionRelationType')}>
							{(binding) => (
								<VocabularySelect
									{...binding}
									options={conditionRelationTypeTerms}
									dataKey="name"
									placeholder="Select Relation"
								/>
							)}
						</FormField>
						<FormField label="Conditions" {...fieldProps('conditions')}>
							{(binding) => (
								<MultiAutocomplete
									{...binding}
									search={conditionsSearch}
									subField="conditionSummary"
									valueDisplay={conditionsSearchConfig.valueDisplay}
								/>
							)}
						</FormField>
					</form>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
