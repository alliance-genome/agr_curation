import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { MultiSelect } from 'primereact/multiselect';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { classNames } from 'primereact/utils';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { RelatedNotesForm } from './RelatedNotesForm';
import { ConditionRelationsForm } from './ConditionRelationsForm';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { validateTable } from '../../utils/utils';
import { FormField } from '../../components/Editors/fields/FormField';
import { SingleAutocomplete } from '../../components/Editors/widgets/SingleAutocomplete';
import { MultiAutocomplete } from '../../components/Editors/widgets/MultiAutocomplete';
import { VocabularySelect } from '../../components/Editors/widgets/VocabularySelect';
import { VocabularyMultiSelect } from '../../components/Editors/widgets/VocabularyMultiSelect';
import { BooleanSelect } from '../../components/Editors/widgets/BooleanSelect';
import { FieldErrors } from '../../components/Editors/fields/FieldErrors';
import { normalizeError } from '../../components/Editors/fields/normalizeError';
import { ExperimentsSearchService } from '../../service/ExperimentsSearchService';
import {
	diseaseAnnotationSubjectSearch,
	diseaseAnnotationSubjectSearchConfig,
} from '../../components/Editors/autocomplete/biologicalEntity/utils';
import {
	diseaseSearch,
	evidenceCodesSearch,
	evidenceCodesSearchConfig,
} from '../../components/Editors/autocomplete/ontology/utils';
import {
	isPickedReference,
	singleReferenceSearch,
	singleReferenceSearchConfig,
} from '../../components/Editors/autocomplete/references/utils';
import {
	assertedGenesSearch,
	assertedGenesSearchConfig,
	diseaseGeneticModifierGenesSearch,
	diseaseGeneticModifierGenesSearchConfig,
	withSearch,
	withSearchConfig,
} from '../../components/Editors/autocomplete/gene/utils';
import {
	assertedAllelesSearch,
	assertedAllelesSearchConfig,
	diseaseGeneticModifierAllelesSearch,
	diseaseGeneticModifierAllelesSearchConfig,
} from '../../components/Editors/autocomplete/allele/utils';
import {
	diseaseGeneticModifierAgmsSearch,
	diseaseGeneticModifierAgmsSearchConfig,
	sgdStrainBackgroundSearch,
	sgdStrainBackgroundSearchConfig,
} from '../../components/Editors/autocomplete/agm/utils';
import { findMissingRequiredFields, findUnpickedFields } from './newAnnotationValidation';
import { SubjectAdditionalFieldData } from '../../components/FieldData/SubjectAdditionalFieldData';
import { AssertedAllelesAdditionalFieldData } from '../../components/FieldData/AssertedAllelesAdditionalFieldData';
import { DiseaseAdditionalFieldData } from '../../components/FieldData/DiseaseAdditionalFieldData';
import { SingleReferenceAdditionalFieldData } from '../../components/FieldData/SingleReferenceAdditionalFieldData';
import { SGDStrainBackgroundAdditionalFieldData } from '../../components/FieldData/SGDStrainBackgroundAdditionalFieldData';
import { AssertedGenesAdditionalFieldData } from '../../components/FieldData/AssertedGenesAdditionalFieldData';
import { EvidenceCodesAdditionalFieldData } from '../../components/FieldData/EvidenceCodesAdditionalFieldData';
import { WithAdditionalFieldData } from '../../components/FieldData/WithAdditionalFieldData';
import { GeneticModifierAgmsAdditionalFieldData } from '../../components/FieldData/GeneticModifierAgmsAdditionalFieldData';
import { GeneticModifierAllelesAdditionalFieldData } from '../../components/FieldData/GeneticModifierAllelesAdditionalFieldData';
import { GeneticModifierGenesAdditionalFieldData } from '../../components/FieldData/GeneticModifierGenesAdditionalFieldData';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { ConfirmButton } from '../../components/ConfirmButton';
import { getDefaultFormState, getModFormFields } from '../../service/TableStateService';
import { getEffectiveStaffGroups } from '../../utils/affiliation';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { Endpoints } from '../../constants/Endpoints';

const labelColumnSize = 'col-2';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';

// NOT's one option; clearing it leaves the annotation un-negated
const NOT_OPTIONS = [{ name: 'NOT', value: true }];

// FormField layout for this dialog's fields: label, widget, then the field's additional data.
const ANNOTATION_FIELD_LAYOUT = {
	labelColumnSize,
	widgetColumnSize,
	fieldDetailsColumnSize,
	labelClassName: 'text-base font-semibold m-0',
};

/**
 * The New Annotation dialog on the Disease Annotations table, also opened pre-filled by Duplicate. The subject's
 * type decides which asserted fields are open and which disease relations are offered. Save checks the required
 * fields, ZFIN's required experiment and that the subject, SGD strain background and reference were picked from the
 * suggestions, then creates the annotation; Save & Add Another keeps the form filled. Optional fields show per the
 * curator's saved form settings.
 *
 * @param {object} newAnnotationState - the form's reducer state
 * @param {Function} newAnnotationDispatch - the form's reducer dispatch
 * @param {object} [diseaseAnnotationService] - the service that creates the annotation
 * @param {object[]} relationsTerms - every disease relation, offered until a subject is picked
 * @param {(annotation: object, queryClient: object) => void} setNewDiseaseAnnotation - called with the created
 *   annotation
 * @returns {JSX.Element}
 */
export const NewAnnotationForm = ({
	newAnnotationState,
	newAnnotationDispatch,
	diseaseAnnotationService,
	relationsTerms,
	setNewDiseaseAnnotation,
}) => {
	const queryClient = useQueryClient();
	const toast_success = useRef(null);
	const toast_error = useRef(null);
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
	const geneticSexTerms = useControlledVocabularyService('genetic_sex');
	const diseaseQualifiersTerms = useControlledVocabularyService('disease_qualifier');
	const annotationTypeTerms = useControlledVocabularyService('annotation_type');
	const geneticModifierRelationTerms = useControlledVocabularyService('disease_genetic_modifier_relation');
	const agmRelationTerms = useVocabularyTermSetService('agm_disease_relation');
	const alleleRelationTerms = useVocabularyTermSetService('allele_disease_relation');
	const geneRelationTerms = useVocabularyTermSetService('gene_disease_relation');
	const [uiErrorMessages, setUiErrorMessages] = useState({});
	// Changes on Clear, so the fields start again with no typed text left in them
	const [formKey, setFormKey] = useState(0);
	const [experimentOptions, setExperimentOptions] = useState([]);
	let newAnnotationOptionalFields = [
		'Asserted Genes',
		'Asserted Alleles',
		'NOT',
		'With',
		'Related Notes',
		'Experimental Conditions',
		'Experiments',
		'Genetic Sex',
		'Disease Qualifiers',
		'SGD Strain Background',
		'Annotation Type',
		'Genetic Modifier Relation',
		'Genetic Modifier AGMs',
		'Genetic Modifier Alleles',
		'Genetic Modifier Genes',
		'Internal',
	];
	// Effective MOD honors a tester's client-side affiliation override (SCRUM-2831).
	const mod = getEffectiveStaffGroups();
	let defaultUserSettings = getDefaultFormState('DiseaseAnnotations', newAnnotationOptionalFields, undefined);
	const { settings: settingsKey, mutate: setSettingsKey } = useGetUserSettings(
		'DiseaseAnnotationsFormSettings',
		defaultUserSettings,
		false
	);
	const { selectedFormFields } = settingsKey;
	const mutation = useMutation({
		mutationFn: (newAnnotation) => {
			if (!diseaseAnnotationService) {
				diseaseAnnotationService = new DiseaseAnnotationService();
			}
			return diseaseAnnotationService.createDiseaseAnnotation(newAnnotation);
		},
	});

	const relationTermsBySubjectType = {
		Gene: geneRelationTerms,
		Allele: alleleRelationTerms,
		AffectedGenomicModel: agmRelationTerms,
	};
	const relationTerms = relationTermsBySubjectType[newAnnotation.diseaseAnnotationSubject?.type] ?? relationsTerms;
	const fieldErrors = { ...errorMessages, ...uiErrorMessages };

	const hideDialog = () => {
		newAnnotationDispatch({ type: 'RESET' });
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: false });
		setUiErrorMessages({});
		setExperimentOptions([]);
	};

	const handleSubmit = async (event, closeAfterSubmit = true) => {
		event.preventDefault();
		newAnnotationDispatch({ type: 'SUBMIT' });
		const isRelatedNotesErrors = await validateTable(
			Endpoints.Entity.NOTE,
			'relatedNotesErrorMessages',
			newAnnotation.relatedNotes,
			newAnnotationDispatch
		);
		const isExConErrors = await validateTable(
			Endpoints.Annotation.CONDITION_RELATION,
			'exConErrorMessages',
			newAnnotation.conditionRelations,
			newAnnotationDispatch
		);

		const uiErrors = { ...findMissingRequiredFields(newAnnotation, mod), ...findUnpickedFields(newAnnotation) };
		setUiErrorMessages(uiErrors);
		if (Object.keys(uiErrors).length > 0) {
			newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: true });

			return;
		}

		mutation.mutate(newAnnotation, {
			onSuccess: (data) => {
				if (!(isRelatedNotesErrors || isExConErrors)) {
					queryClient.invalidateQueries({
						queryKey: ['DiseaseAnnotationsHandles'],
					});
					toast_success.current.show({ severity: 'success', summary: 'Successful', detail: 'New Annotation Added' });
					if (closeAfterSubmit) {
						newAnnotationDispatch({ type: 'RESET' });
					} else {
						setUiErrorMessages({});
					}

					setNewDiseaseAnnotation(data.data.entity, queryClient);
				}
			},
			onError: (error) => {
				let message;
				if (error?.response?.data?.errorMessages?.uniqueId) {
					message = 'Page Error: New annotation is a duplicate of an existing annotation';
				} else if (error?.response?.data?.errorMessage) {
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
				}

				toast_error.current.show({ severity: 'error', summary: 'Page error: ', detail: message });

				newAnnotationDispatch({
					type: 'UPDATE_ERROR_MESSAGES',
					errorType: 'errorMessages',
					errorMessages: error.response?.data?.errorMessages || {},
				});
			},
		});
	};

	const handleClear = () => {
		newAnnotationDispatch({ type: 'CLEAR' });
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: false });
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: false });

		setUiErrorMessages({});
		setExperimentOptions([]);
		setFormKey((key) => key + 1);
	};

	const handleSubmitAndAdd = (event) => {
		handleSubmit(event, false);
	};

	const editField = (field) => (value) => newAnnotationDispatch({ type: 'EDIT', field, value });

	// Editing the subject, SGD strain background or reference clears the messages from the last Save
	const editFieldClearingUiErrors = (field) => (value) => {
		setUiErrorMessages({});
		editField(field)(value);
	};

	// A new reference has its own experiments, loaded when Experiments is opened
	const onReferenceChange = (reference) => {
		setExperimentOptions([]);
		editFieldClearingUiErrors('evidenceItem')(reference);
	};

	const onSubjectChange = (subject) => {
		const subjectType = subject?.type;
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: subject != null });
		newAnnotationDispatch({
			type: 'SET_IS_ASSERTED_GENE_ENABLED',
			value: subjectType === 'Allele' || subjectType === 'AffectedGenomicModel',
		});
		newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: subjectType === 'AffectedGenomicModel' });
		editFieldClearingUiErrors('diseaseAnnotationSubject')(subject);
	};

	const isExperimentRequired = mod?.includes('ZFINStaff');

	// The reference whose experiments were last asked for, so a late answer for another reference is ignored
	const experimentsReferenceCurie = useRef(null);

	const loadExperimentOptions = () => {
		const referenceCurie = newAnnotation.evidenceItem?.curie;
		experimentsReferenceCurie.current = referenceCurie;
		setExperimentOptions([]);
		new ExperimentsSearchService()
			.findExperiments(Endpoints.Annotation.CONDITION_RELATION, 15, 0, { 'singleReference.curie': referenceCurie })
			.then((data) => {
				if (experimentsReferenceCurie.current === referenceCurie) setExperimentOptions(data.results ?? []);
			})
			.catch(() => {
				if (experimentsReferenceCurie.current === referenceCurie) setExperimentOptions([]);
			});
	};

	const isExperimentEnabled = () => {
		return (
			//only enabled if a reference is selected from suggestions and condition relation table isn't visible
			isPickedReference(newAnnotation.evidenceItem) && !showConditionRelations
		);
	};

	const isConditionRelationButtonEnabled = () => {
		return newAnnotation.conditionRelations?.[0] && newAnnotation.conditionRelations?.[0].handle;
	};

	const fieldProps = (field, onChange = editField(field)) => ({
		name: field,
		value: newAnnotation[field],
		onChange,
		errors: fieldErrors,
		...ANNOTATION_FIELD_LAYOUT,
	});

	const dialogFooter = (
		<>
			<Splitter style={{ border: 'none', paddingTop: '12px', height: '10%' }} gutterSize="0">
				<SplitterPanel style={{ textAlign: 'left', flexGrow: 0 }} size={10}>
					<Button label="Clear" icon="pi pi-check" className="p-button-text" onClick={handleClear} />
					<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
				</SplitterPanel>
				<SplitterPanel style={{ flexGrow: 0 }} size={60}></SplitterPanel>
				<SplitterPanel style={{ textAlign: 'right', flexGrow: 0 }} size={30}>
					<Button
						label="Save & Close"
						icon="pi pi-check"
						className="p-button-text"
						disabled={!isEnabled}
						onClick={handleSubmit}
					/>
					<Button
						label="Save & Add Another"
						icon="pi pi-check"
						className="p-button-text"
						disabled={!isEnabled}
						onClick={handleSubmitAndAdd}
					/>
				</SplitterPanel>
			</Splitter>
		</>
	);

	const updateFormFields = (updatedFields) => {
		let updatedSettings = { ...defaultUserSettings, selectedFormFields: updatedFields };
		setSettingsKey(updatedSettings);
	};

	const handleShowAllFields = () => {
		updateFormFields(newAnnotationOptionalFields);
	};

	const setToModDefault = () => {
		const modFormFields = getModFormFields('DiseaseAnnotations');
		updateFormFields(modFormFields);
	};

	const dialogHeader = (
		<>
			<Splitter style={{ border: 'none', height: '5%' }} gutterSize="0">
				<SplitterPanel size={25} style={{ textAlign: 'left' }}>
					<h4>Add Disease Annotation</h4>
				</SplitterPanel>
				<SplitterPanel size={10} style={{ textAlign: 'right', padding: '5px' }}>
					<MultiSelect
						aria-label="columnToggle"
						options={newAnnotationOptionalFields}
						value={selectedFormFields}
						filter
						resetFilterOnHide
						onChange={(e) => updateFormFields(e.value)}
						className="w-20rem text-center"
						maxSelectedLabels={4}
					/>
				</SplitterPanel>
				<SplitterPanel size={10} style={{ textAlign: 'right', padding: '5px' }}>
					<Button label="Show all fields" onClick={handleShowAllFields} />
				</SplitterPanel>
				<SplitterPanel size={10} style={{ textAlign: 'right', padding: '5px' }}>
					<ConfirmButton
						buttonText="Set to MOD Defaults"
						messageText={`Are you sure? This will reset to MOD default settings.`}
						acceptHandler={setToModDefault}
					/>
				</SplitterPanel>
			</Splitter>
		</>
	);

	return (
		<div>
			<Toast ref={toast_error} position="top-left" />
			<Toast ref={toast_success} position="top-right" />
			<Dialog
				visible={newAnnotationDialog}
				header={dialogHeader}
				position={'top'}
				modal
				className="p-fluid w-9"
				footer={dialogFooter}
				onHide={hideDialog}
				maximizable
			>
				<ErrorBoundary>
					<form key={formKey}>
						<FormField
							label="Subject"
							required
							{...fieldProps('diseaseAnnotationSubject', onSubjectChange)}
							additionalDataField={<SubjectAdditionalFieldData fieldData={newAnnotation.diseaseAnnotationSubject} />}
						>
							{(binding) => (
								<SingleAutocomplete
									{...binding}
									search={diseaseAnnotationSubjectSearch}
									subField="primaryExternalId"
									valueDisplay={diseaseAnnotationSubjectSearchConfig.valueDisplay}
								/>
							)}
						</FormField>

						{selectedFormFields?.includes('Asserted Genes') && (
							<FormField
								label="Asserted Genes"
								{...fieldProps('assertedGenes')}
								additionalDataField={<AssertedGenesAdditionalFieldData fieldData={newAnnotation.assertedGenes} />}
							>
								{(binding) => (
									<MultiAutocomplete
										{...binding}
										search={assertedGenesSearch}
										subField="primaryExternalId"
										valueDisplay={assertedGenesSearchConfig.valueDisplay}
										disabled={!isAssertedGeneEnabled}
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Asserted Alleles') && (
							<FormField
								label="Asserted Alleles"
								{...fieldProps('assertedAlleles')}
								additionalDataField={<AssertedAllelesAdditionalFieldData fieldData={newAnnotation.assertedAlleles} />}
							>
								{(binding) => (
									<MultiAutocomplete
										{...binding}
										search={assertedAllelesSearch}
										subField="primaryExternalId"
										valueDisplay={assertedAllelesSearchConfig.valueDisplay}
										disabled={!isAssertedAlleleEnabled}
									/>
								)}
							</FormField>
						)}

						<FormField label="Disease Relation" required {...fieldProps('relation')}>
							{(binding) => <VocabularySelect {...binding} options={relationTerms} dataKey="name" />}
						</FormField>

						{selectedFormFields?.includes('NOT') && (
							<FormField label="NOT" {...fieldProps('negated', (value) => editField('negated')(value ?? false))}>
								{(binding) => (
									<VocabularySelect
										{...binding}
										value={binding.value || null}
										options={NOT_OPTIONS}
										optionValue="value"
										showClear
									/>
								)}
							</FormField>
						)}

						<FormField
							label="Disease"
							required
							{...fieldProps('diseaseAnnotationObject')}
							additionalDataField={<DiseaseAdditionalFieldData fieldData={newAnnotation.diseaseAnnotationObject} />}
						>
							{(binding) => <SingleAutocomplete {...binding} search={diseaseSearch} />}
						</FormField>

						<FormField
							label="Reference"
							required
							{...fieldProps('evidenceItem', onReferenceChange)}
							additionalDataField={<SingleReferenceAdditionalFieldData fieldData={newAnnotation.evidenceItem} />}
						>
							{(binding) => (
								<SingleAutocomplete
									{...binding}
									search={singleReferenceSearch}
									valueDisplay={singleReferenceSearchConfig.valueDisplay}
								/>
							)}
						</FormField>

						<FormField
							label="Evidence Code"
							required
							{...fieldProps('evidenceCodes')}
							additionalDataField={<EvidenceCodesAdditionalFieldData fieldData={newAnnotation.evidenceCodes} />}
						>
							{(binding) => (
								<MultiAutocomplete
									{...binding}
									search={evidenceCodesSearch}
									valueDisplay={evidenceCodesSearchConfig.valueDisplay}
								/>
							)}
						</FormField>

						{selectedFormFields?.includes('With') && (
							<FormField
								label="With"
								{...fieldProps('with')}
								additionalDataField={<WithAdditionalFieldData fieldData={newAnnotation.with} />}
							>
								{(binding) => (
									<MultiAutocomplete
										{...binding}
										search={withSearch}
										subField="primaryExternalId"
										valueDisplay={withSearchConfig.valueDisplay}
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Related Notes') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<h2 className={ANNOTATION_FIELD_LAYOUT.labelClassName}>Related Notes</h2>
									</div>
									<div
										className={classNames('col-9', {
											'border-2 border-red-300':
												submitted && errorMessages.relatedNotes && Object.keys(relatedNotesErrorMessages).length === 0,
										})}
									>
										<RelatedNotesForm
											dispatch={newAnnotationDispatch}
											relatedNotes={newAnnotation.relatedNotes}
											showRelatedNotes={showRelatedNotes}
											errorMessages={relatedNotesErrorMessages}
											editingRows={relatedNotesEditingRows}
										/>
										<FieldErrors messages={[normalizeError(fieldErrors.relatedNotes)]} layout="text" />
									</div>
								</div>
							</>
						)}

						{selectedFormFields?.includes('Experimental Conditions') && (
							<>
								<div className="grid">
									<div className={labelColumnSize}>
										<h2 className={ANNOTATION_FIELD_LAYOUT.labelClassName}>Experimental Conditions</h2>
									</div>
									<div className={classNames('col-9')}>
										<ConditionRelationsForm
											dispatch={newAnnotationDispatch}
											conditionRelations={newAnnotation.conditionRelations}
											showConditionRelations={showConditionRelations}
											errorMessages={exConErrorMessages}
											buttonIsDisabled={isConditionRelationButtonEnabled()}
											editingRows={conditionRelationsEditingRows}
										/>
									</div>
								</div>
							</>
						)}

						{(isExperimentRequired || selectedFormFields?.includes('Experiments')) && (
							<FormField
								label="Experiments"
								required={isExperimentRequired}
								name="experiments"
								value={newAnnotation.conditionRelations?.[0] ?? null}
								onChange={(experiment) => newAnnotationDispatch({ type: 'EDIT_EXPERIMENT', value: experiment })}
								errors={fieldErrors}
								errorField="conditionRelations"
								{...ANNOTATION_FIELD_LAYOUT}
							>
								{(binding) => (
									<VocabularySelect
										{...binding}
										options={experimentOptions}
										optionLabel="handle"
										dataKey="handle"
										placeholder={newAnnotation.conditionRelations?.[0]?.handle}
										onShow={loadExperimentOptions}
										disabled={!isExperimentEnabled()}
										showClear
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Genetic Sex') && (
							<FormField label="Genetic Sex" {...fieldProps('geneticSex')}>
								{(binding) => <VocabularySelect {...binding} options={geneticSexTerms} dataKey="name" showClear />}
							</FormField>
						)}

						{selectedFormFields?.includes('Disease Qualifiers') && (
							<FormField label="Disease Qualifiers" {...fieldProps('diseaseQualifiers')}>
								{(binding) => <VocabularyMultiSelect {...binding} options={diseaseQualifiersTerms} dataKey="name" />}
							</FormField>
						)}

						{selectedFormFields?.includes('SGD Strain Background') && (
							<FormField
								label="SGD Strain Background"
								{...fieldProps('sgdStrainBackground', editFieldClearingUiErrors('sgdStrainBackground'))}
								additionalDataField={
									<SGDStrainBackgroundAdditionalFieldData fieldData={newAnnotation.sgdStrainBackground} />
								}
							>
								{(binding) => (
									<SingleAutocomplete
										{...binding}
										search={sgdStrainBackgroundSearch}
										subField="primaryExternalId"
										valueDisplay={sgdStrainBackgroundSearchConfig.valueDisplay}
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Annotation Type') && (
							<FormField label="Annotation Type" {...fieldProps('annotationType')}>
								{(binding) => <VocabularySelect {...binding} options={annotationTypeTerms} dataKey="name" showClear />}
							</FormField>
						)}

						{selectedFormFields?.includes('Genetic Modifier Relation') && (
							<FormField label="Genetic Modifier Relation" {...fieldProps('diseaseGeneticModifierRelation')}>
								{(binding) => (
									<VocabularySelect {...binding} options={geneticModifierRelationTerms} dataKey="name" showClear />
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Genetic Modifier AGMs') && (
							<FormField
								label="Genetic Modifier AGMs"
								{...fieldProps('diseaseGeneticModifierAgms')}
								additionalDataField={
									<GeneticModifierAgmsAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifierAgms} />
								}
							>
								{(binding) => (
									<MultiAutocomplete
										{...binding}
										search={diseaseGeneticModifierAgmsSearch}
										subField="primaryExternalId"
										valueDisplay={diseaseGeneticModifierAgmsSearchConfig.valueDisplay}
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Genetic Modifier Alleles') && (
							<FormField
								label="Genetic Modifier Alleles"
								{...fieldProps('diseaseGeneticModifierAlleles')}
								additionalDataField={
									<GeneticModifierAllelesAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifierAlleles} />
								}
							>
								{(binding) => (
									<MultiAutocomplete
										{...binding}
										search={diseaseGeneticModifierAllelesSearch}
										subField="primaryExternalId"
										valueDisplay={diseaseGeneticModifierAllelesSearchConfig.valueDisplay}
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Genetic Modifier Genes') && (
							<FormField
								label="Genetic Modifier Genes"
								{...fieldProps('diseaseGeneticModifierGenes')}
								additionalDataField={
									<GeneticModifierGenesAdditionalFieldData fieldData={newAnnotation.diseaseGeneticModifierGenes} />
								}
							>
								{(binding) => (
									<MultiAutocomplete
										{...binding}
										search={diseaseGeneticModifierGenesSearch}
										subField="primaryExternalId"
										valueDisplay={diseaseGeneticModifierGenesSearchConfig.valueDisplay}
									/>
								)}
							</FormField>
						)}

						{selectedFormFields?.includes('Internal') && (
							<FormField label="Internal" required {...fieldProps('internal')}>
								{(binding) => <BooleanSelect {...binding} />}
							</FormField>
						)}
					</form>
				</ErrorBoundary>
			</Dialog>
		</div>
	);
};
