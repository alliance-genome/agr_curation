import React, { useRef } from 'react';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { FormField } from '../../components/Editors/fields/FormField';
import { DIALOG_FIELD_LAYOUT } from '../../components/Editors/fields/fieldLayouts';
import { SingleAutocomplete } from '../../components/Editors/widgets/SingleAutocomplete';
import { TextInput } from '../../components/Editors/widgets/TextInput';
import { TextArea } from '../../components/Editors/widgets/TextArea';
import { BooleanSelect } from '../../components/Editors/widgets/BooleanSelect';
import {
	conditionAnatomySearch,
	conditionChemicalSearch,
	conditionClassSearch,
	conditionGeneOntologySearch,
	conditionIdSearch,
	conditionTaxonSearch,
} from '../../components/Editors/autocomplete/ontology/utils';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

const ONTOLOGY_FIELDS = [
	{ field: 'conditionClass', label: 'Condition Class', search: conditionClassSearch },
	{ field: 'conditionId', label: 'Condition Term', search: conditionIdSearch },
	{ field: 'conditionGeneOntology', label: 'Gene Ontology', search: conditionGeneOntologySearch },
	{ field: 'conditionChemical', label: 'Chemical', search: conditionChemicalSearch },
	{ field: 'conditionAnatomy', label: 'Anatomy', search: conditionAnatomySearch },
	{ field: 'conditionTaxon', label: 'Taxon', search: conditionTaxonSearch },
];

/**
 * The New Condition dialog on the Experimental Conditions table: an ontology term autocomplete for each condition
 * field, quantity, internal and free text. Save creates the condition; a rejected save shows each field's error
 * beside it.
 *
 * @param {object} newConditionState - the form's reducer state: `newCondition`, `errorMessages`,
 *   `newConditionDialog`
 * @param {Function} newConditionDispatch - the form's reducer dispatch
 * @param {object} [experimentalConditionService] - the service that creates the condition
 * @param {(condition: object, queryClient: object) => void} setNewExperimentalCondition - called with the created
 *   condition
 * @returns {JSX.Element}
 */
export const NewConditionForm = ({
	newConditionState,
	newConditionDispatch,
	experimentalConditionService,
	setNewExperimentalCondition,
}) => {
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newCondition, errorMessages, newConditionDialog } = newConditionState;
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: (newCondition) => {
			if (!experimentalConditionService) {
				experimentalConditionService = new ExperimentalConditionService();
			}
			return experimentalConditionService.createExperimentalCondition(newCondition);
		},
	});

	const hideDialog = () => {
		newConditionDispatch({ type: 'RESET' });
	};

	const handleSubmit = (event) => {
		event.preventDefault();
		newConditionDispatch({ type: 'SUBMIT' });
		mutation.mutate(newCondition, {
			onSuccess: (data) => {
				setNewExperimentalCondition(data.data.entity, queryClient);
				toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Condition Added' });
				newConditionDispatch({ type: 'RESET' });
			},
			onError: (error) => {
				const message = error.response.data.errorMessages.uniqueId
					? 'Page Error: New experimental condition is a duplicate of an existing experimental condition'
					: error.response.data.errorMessage;

				toast_error.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
				]);
				newConditionDispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: error.response.data.errorMessages });
			},
		});
	};

	const fieldProps = (field) => ({
		name: field,
		value: newCondition[field],
		onChange: (value) => newConditionDispatch({ type: 'EDIT', field, value }),
		errors: errorMessages,
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
				visible={newConditionDialog}
				style={{ width: '450px' }}
				header="Add Condition"
				modal
				className="p-fluid"
				footer={dialogFooter}
				onHide={hideDialog}
			>
				<ErrorBoundary>
					<form>
						{ONTOLOGY_FIELDS.map(({ field, label, search }) => (
							<FormField key={field} label={label} {...fieldProps(field)}>
								{(binding) => <SingleAutocomplete {...binding} search={search} />}
							</FormField>
						))}
						<FormField label="Quantity" {...fieldProps('conditionQuantity')}>
							{(binding) => <TextInput {...binding} />}
						</FormField>
						<FormField label="Internal" {...fieldProps('internal')}>
							{(binding) => <BooleanSelect {...binding} />}
						</FormField>
						<FormField label="Free Text" {...fieldProps('conditionFreeText')}>
							{(binding) => <TextArea {...binding} rows={3} />}
						</FormField>
					</form>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
