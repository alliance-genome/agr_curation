import React, { useEffect, useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { CassetteService } from '../../service/CassetteService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { FormFieldVisibilityMenu, FormSection, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { processErrors } from '../affectedGenomicModelPage/utils';
import { NameSlotAnnotationForm } from '../../components/NameSlotAnnotationForm';
import { SynonymsForm } from '../transgenicToolsPage/detail/SynonymsForm';
import { SecondaryIdsForm } from '../transgenicToolsPage/detail/SecondaryIdsForm';
import { UsesForm } from '../transgenicToolsPage/detail/UsesForm';
import { ReferencesForm } from '../transgenicToolsPage/detail/ReferencesForm';
import {
	FREE_TEXT_COMPONENTS,
	GENOMIC_ENTITY_COMPONENTS,
	TRANSGENIC_TOOL_COMPONENTS,
	STR_COMPONENTS,
} from './components/componentConfigs';
import { useCassetteReducer } from './detail/useCassetteReducer';
import { useCassetteComponentList } from './detail/useCassetteComponentList';
import { CassetteComponentsForm } from './detail/CassetteComponentsForm';
import { buildCassettePayload } from './detail/utils';

const ENTITY_KEY = 'cassette';

// The fields of the page, in the ticket's order; each can be hidden through the field menu (SCRUM-6747)
export const CASSETTE_DETAIL_TOGGLEABLE_FIELDS = [
	'Unique ID',
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Symbol',
	'Name',
	'Synonyms',
	'Secondary IDs',
	FREE_TEXT_COMPONENTS.title,
	GENOMIC_ENTITY_COMPONENTS.title,
	TRANSGENIC_TOOL_COMPONENTS.title,
	STR_COMPONENTS.title,
	'Uses',
	'References',
	'Data Provider',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Placeholder',
	'Internal',
	'Obsolete',
];

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';

const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

// The message of a rejected request: the API's, or its status when it gave none.
const errorDetail = (error) =>
	error?.response?.data?.errorMessage ??
	(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message);

/**
 * The detail page of one cassette (SCRUM-6747): the fields of the Cassettes table, each in a section the curator can
 * hide through the field menu. Save writes the cassette, then each of its component lists that has been edited
 * through the list's own endpoint.
 */
export default function CassetteDetailPage() {
	const { identifier } = useParams();
	const cassetteService = useMemo(() => new CassetteService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const { cassetteState, cassetteDispatch } = useCassetteReducer();
	const cassette = cassetteState.cassette;
	const freeTextComponents = useCassetteComponentList(cassette?.id, FREE_TEXT_COMPONENTS);
	const genomicEntityComponents = useCassetteComponentList(cassette?.id, GENOMIC_ENTITY_COMPONENTS);
	const transgenicToolComponents = useCassetteComponentList(cassette?.id, TRANSGENIC_TOOL_COMPONENTS);
	const strComponents = useCassetteComponentList(cassette?.id, STR_COMPONENTS);
	const componentSections = [
		{ config: FREE_TEXT_COMPONENTS, components: freeTextComponents },
		{ config: GENOMIC_ENTITY_COMPONENTS, components: genomicEntityComponents },
		{ config: TRANSGENIC_TOOL_COMPONENTS, components: transgenicToolComponents },
		{ config: STR_COMPONENTS, components: strComponents },
	];
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'CassetteDetail',
		CASSETTE_DETAIL_TOGGLEABLE_FIELDS
	);

	const { isPending, data } = useQuery({
		queryKey: ['cassette', identifier],
		queryFn: () => cassetteService.getCassette(identifier),
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (data?.data?.entity) cassetteDispatch({ type: 'SET', value: data.data.entity });
	}, [data, cassetteDispatch]);

	const { isPending: isSavingCassette, mutateAsync: saveCassette } = useMutation({
		mutationFn: (payload) => cassetteService.saveCassette(payload),
	});

	const isSavingComponents = componentSections.some(({ components }) => components.isSaving);
	const hasPendingEdits =
		cassetteState.hasPendingEdits || componentSections.some(({ components }) => components.isDirty);

	const handleSubmit = async (event) => {
		event.preventDefault();
		cassetteDispatch({ type: 'SUBMIT' });

		const failures = [];
		if (cassetteState.hasPendingEdits) {
			try {
				const result = await saveCassette(buildCassettePayload(cassette));
				if (result?.data?.entity) cassetteDispatch({ type: 'SET', value: result.data.entity });
			} catch (error) {
				failures.push({ summary: 'Cassette not saved: ', detail: errorDetail(error) });
				try {
					processErrors(error?.response?.data, cassetteDispatch, cassette);
				} catch (processingError) {
					console.error(processingError);
				}
			}
		}

		// The component lists are written whether or not the cassette saved, so the errors of all are reported together.
		for (const { config, components } of componentSections) {
			if (!components.isDirty) continue;
			const outcome = await components.save();
			if (!outcome.isSuccess) failures.push({ summary: `${config.title} not saved: `, detail: outcome.message });
		}

		if (failures.length > 0) {
			toastError.current.show(
				failures.map((failure) => ({ life: 10000, severity: 'error', sticky: false, ...failure }))
			);
			return;
		}
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Cassette Saved' });
	};

	if (isPending)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const onBooleanChange = (field) => (event) => cassetteDispatch({ type: 'EDIT', field, value: event.value });

	const booleanField = (field, label) => (
		<BooleanDetailPageEditor
			value={cassette?.[field]}
			name={field}
			label={label}
			onValueChange={onBooleanChange(field)}
			errorMessages={cassetteState.errorMessages}
			{...columnSizes}
		/>
	);

	const formProps = { state: cassetteState, dispatch: cassetteDispatch, entityKey: ENTITY_KEY };

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={isSavingCassette || isSavingComponents} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:min-h-5rem" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Cassette"
								entity={cassette}
								label={cassette?.cassetteSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={CASSETTE_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={20} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button
								label="Save"
								icon="pi pi-check"
								severity="success"
								onClick={handleSubmit}
								disabled={!hasPendingEdits || isSavingCassette || isSavingComponents}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				{/* Saving is the header's Save; Enter in a table filter or field must not submit and reload the page. */}
				<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
					<FormSection isVisible={isVisible('Unique ID')}>
						<IdentifierDetailPageTemplate
							identifier={cassette?.uniqueId}
							label="Unique ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Curie')}>
						<IdentifierDetailPageTemplate
							identifier={cassette?.curie}
							label="Curie"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Primary External ID')}>
						<IdentifierDetailPageTemplate
							identifier={cassette?.primaryExternalId}
							label="Primary External ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('MOD Internal ID')}>
						<IdentifierDetailPageTemplate
							identifier={cassette?.modInternalId}
							label="MOD Internal ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Symbol')}>
						<NameSlotAnnotationForm
							name={cassetteState[ENTITY_KEY]?.cassetteSymbol}
							entityState={cassetteState.entityStates.cassetteSymbol}
							dispatch={cassetteDispatch}
							entityType="cassetteSymbol"
							isSymbol
							tableName="Symbol"
							addLabel="Add Symbol"
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Name')}>
						<NameSlotAnnotationForm
							name={cassetteState[ENTITY_KEY]?.cassetteFullName}
							entityState={cassetteState.entityStates.cassetteFullName}
							dispatch={cassetteDispatch}
							entityType="cassetteFullName"
							tableName="Name"
							addLabel="Add Name"
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Synonyms')}>
						<SynonymsForm {...formProps} entityType="cassetteSynonyms" />
					</FormSection>
					<FormSection isVisible={isVisible('Secondary IDs')}>
						<SecondaryIdsForm {...formProps} {...columnSizes} />
					</FormSection>
					{componentSections.map(({ config, components }) => (
						<FormSection key={config.list} isVisible={isVisible(config.title)}>
							<CassetteComponentsForm components={components} config={config} />
						</FormSection>
					))}
					<FormSection isVisible={isVisible('Uses')}>
						<UsesForm {...formProps} entityType="cassetteUses" tableName="Uses" />
					</FormSection>
					<FormSection isVisible={isVisible('References')}>
						<ReferencesForm {...formProps} />
					</FormSection>
					<FormSection isVisible={isVisible('Data Provider')}>
						<DataProviderDetailPageTemplate dataProvider={cassette?.dataProvider?.abbreviation} {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Updated By')}>
						<UserDetailPageTemplate user={cassette?.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Date Updated')}>
						<DateDetailPageTemplate date={cassette?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Created By')}>
						<UserDetailPageTemplate user={cassette?.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Date Created')}>
						<DateDetailPageTemplate date={cassette?.dateCreated} fieldName="Date Created" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Placeholder')}>{booleanField('placeholder', 'Placeholder')}</FormSection>
					<FormSection isVisible={isVisible('Internal')}>{booleanField('internal', 'Internal')}</FormSection>
					<FormSection isVisible={isVisible('Obsolete')}>{booleanField('obsolete', 'Obsolete')}</FormSection>
				</form>
			</ErrorBoundary>
		</>
	);
}
