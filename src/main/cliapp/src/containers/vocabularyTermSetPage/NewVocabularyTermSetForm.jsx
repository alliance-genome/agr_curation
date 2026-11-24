import React, { useRef } from 'react';
import { VocabularyTermSetService } from '../../service/VocabularyTermSetService';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { FormField } from '../../components/Editors/fields/FormField';
import { DIALOG_FIELD_LAYOUT } from '../../components/Editors/fields/dialogFieldLayout';
import { TextInput } from '../../components/Editors/widgets/TextInput';
import { SingleAutocomplete } from '../../components/Editors/widgets/SingleAutocomplete';
import { MultiAutocomplete } from '../../components/Editors/widgets/MultiAutocomplete';
import { vocabularySearch, vocabularySearchConfig } from '../../components/Editors/autocomplete/vocabulary/utils';
import {
	buildMemberTermsOtherFilters,
	memberTermsSearchConfig,
} from '../../components/Editors/autocomplete/vocabularyTerm/utils';
import { buildSearchFn } from '../../components/Editors/autocomplete/base/buildSearchFn';
import ErrorBoundary from '../../components/Error/ErrorBoundary';

/**
 * The New Vocabulary Term Set dialog on the Vocabulary Term Sets table: name, label, vocabulary, description and
 * the vocabulary's terms in the set. Vocabulary Terms is enabled once a vocabulary is picked, and offers only that
 * vocabulary's terms. Save creates the term set; a rejected save shows each field's error beside it.
 *
 * @param {object} newVocabularyTermSetState - the form's reducer state: `newVocabularyTermSet`, `errorMessages`,
 *   `newVocabularyTermSetDialog`
 * @param {Function} newVocabularyTermSetDispatch - the form's reducer dispatch
 * @param {object} [vocabularyTermSetService] - the service that creates the term set
 * @param {(termSet: object, queryClient: object) => void} setNewVocabularyTermSet - called with the created term set
 * @returns {JSX.Element}
 */
export const NewVocabularyTermSetForm = ({
	newVocabularyTermSetState,
	newVocabularyTermSetDispatch,
	vocabularyTermSetService,
	setNewVocabularyTermSet,
}) => {
	const toast_success = useRef(null);
	const toast_error = useRef(null);
	const { newVocabularyTermSet, errorMessages, newVocabularyTermSetDialog } = newVocabularyTermSetState;
	const queryClient = useQueryClient();
	const vocabulary = newVocabularyTermSet.vocabularyTermSetVocabulary;

	const hideDialog = () => {
		newVocabularyTermSetDispatch({ type: 'RESET' });
	};

	const mutation = useMutation({
		mutationFn: (newVocabularyTermSet) => {
			if (!vocabularyTermSetService) {
				vocabularyTermSetService = new VocabularyTermSetService();
			}
			return vocabularyTermSetService.createVocabularyTermSet(newVocabularyTermSet);
		},
	});

	const handleSubmit = (event) => {
		event.preventDefault();
		newVocabularyTermSetDispatch({ type: 'SUBMIT' });
		mutation.mutate(newVocabularyTermSet, {
			onSuccess: (data) => {
				setNewVocabularyTermSet(data.data.entity, queryClient);
				toast_success.current.show({
					severity: 'success',
					summary: 'Successful',
					detail: 'New Vocabulary Term Set Added',
				});
				newVocabularyTermSetDispatch({ type: 'RESET' });
			},
			onError: (error) => {
				toast_error.current.show([
					{
						life: 7000,
						severity: 'error',
						summary: 'Page error: ',
						detail: error.response.data.errorMessage,
						sticky: false,
					},
				]);
				newVocabularyTermSetDispatch({
					type: 'UPDATE_ERROR_MESSAGES',
					errorMessages: error.response.data.errorMessages,
				});
			},
		});
	};

	const searchVocabularyTerms = buildSearchFn({
		...memberTermsSearchConfig,
		otherFilters: () => buildMemberTermsOtherFilters(vocabulary?.name),
	});

	const fieldProps = (field) => ({
		name: field,
		value: newVocabularyTermSet[field],
		onChange: (value) => newVocabularyTermSetDispatch({ type: 'EDIT', field, value }),
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
				visible={newVocabularyTermSetDialog}
				style={{ width: '450px' }}
				header="Add Vocabulary Term Set"
				modal
				className="p-fluid"
				footer={dialogFooter}
				onHide={hideDialog}
			>
				<ErrorBoundary>
					<form>
						<FormField label="Name" required {...fieldProps('name')}>
							{(binding) => <TextInput {...binding} />}
						</FormField>
						<FormField label="Label" required {...fieldProps('vocabularyLabel')}>
							{(binding) => <TextInput {...binding} />}
						</FormField>
						<FormField label="Vocabulary" required {...fieldProps('vocabularyTermSetVocabulary')}>
							{(binding) => (
								<SingleAutocomplete
									{...binding}
									search={vocabularySearch}
									subField="name"
									valueDisplay={vocabularySearchConfig.valueDisplay}
								/>
							)}
						</FormField>
						<FormField label="Description" {...fieldProps('vocabularyTermSetDescription')}>
							{(binding) => <TextInput {...binding} />}
						</FormField>
						<FormField label="Vocabulary Terms" {...fieldProps('memberTerms')}>
							{(binding) => (
								<MultiAutocomplete
									{...binding}
									search={searchVocabularyTerms}
									subField="name"
									valueDisplay={memberTermsSearchConfig.valueDisplay}
									disabled={!vocabulary?.id}
								/>
							)}
						</FormField>
					</form>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
