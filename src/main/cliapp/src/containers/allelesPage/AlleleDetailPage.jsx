import { useRef, useEffect } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { useAlleleReducer } from './useAlleleReducer';
import { InCollectionDetailPageEditor } from '../../components/Editors/autocomplete/inCollection/InCollectionDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { FullNameForm } from './fullName/FullNameForm';
import { MutationTypesForm } from './mutationTypes/MutationTypesForm';
import { InheritanceModesForm } from './inheritanceModes/InheritanceModesForm';
import { SecondaryIdsForm } from './secondaryIds/SecondaryIdsForm';
import { FunctionalImpactsForm } from './functionalImpacts/FunctionalImpactsForm';
import { DatabaseStatusForm } from './databaseStatus/DatabaseStatusForm';
import { RelatedNotesForm } from './relatedNotes/RelatedNotesForm';
import { SymbolForm } from './symbol/SymbolForm';
import { GermilineTransmissionStatusForm } from './germlineTransmissionStatus/GermlineTransmissionStatusForm';
import { ReferencesForm } from './referencesTable/ReferencesForm';
import { NomenclatureEventsForm } from './nomenclatureEvents/NomenclatureEventsForm';
import { StickyHeader } from '../../components/StickyHeader';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { AlleleGeneAssociationsForm } from './alleleGeneAssociations/AlleleGeneAssociationsForm';
import { validateRequiredAutosuggestField, processErrors } from './utils';
import { FormFieldVisibilityMenu, FormSection, useFormFieldVisibility } from '../../components/FormFieldVisibility';

// Curie, Primary External ID, MOD Internal ID and Taxon are deliberately absent: they identify
// the allele and Taxon is required on save, so they always render.
const ALLELE_TOGGLEABLE_FIELDS = [
	'Name',
	'Symbol',
	'Synonyms',
	'Secondary IDs',
	'Nomenclature Events',
	'Mutation Types',
	'Functional Impacts',
	'Germline Transmission Status',
	'Database Status',
	'Inheritance Modes',
	'References',
	'In Collection',
	'Is Extinct',
	'Related Notes',
	'Allele Gene Associations',
	'Data Provider',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Internal',
	'Obsolete',
];

export default function AlleleDetailPage() {
	const { identifier } = useParams();
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'Allele',
		ALLELE_TOGGLEABLE_FIELDS
	);
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const labelColumnSize = 'col-3';
	const widgetColumnSize = 'col-4';
	const fieldDetailsColumnSize = 'col-5';

	const { isPending: getRequestIsLoading, data: alleleQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => alleleService.getAllele(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (alleleQueryData) {
			alleleDispatch({ type: 'SET', value: alleleQueryData?.data?.entity });
		}
	}, [alleleQueryData, alleleDispatch]);

	const { isPending: allelePutRequestIsLoading, mutate: alleleMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.saveAlleleDetail(allele);
		},
	});

	const handleSubmit = async (event) => {
		event.preventDefault();
		alleleDispatch({
			type: 'SUBMIT',
		});

		const areUiErrors = validateRequiredAutosuggestField(
			alleleState.allele.alleleGeneAssociations,
			alleleState.entityStates.alleleGeneAssociations.errorMessages,
			alleleDispatch,
			'alleleGeneAssociations',
			'alleleGeneAssociationObject'
		);

		if (areUiErrors) return;

		alleleMutate(alleleState.allele, {
			onSuccess: (result) => {
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Saved' });
				alleleDispatch({ type: 'SET', value: result?.data?.entity });
			},
			onError: (error) => {
				let message;
				const data = error?.response?.data;

				if (data.errorMessage) {
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
				}
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
				]);

				try {
					processErrors(data, alleleDispatch, alleleState.allele);
				} catch (e) {
					console.error(e);
				}
			},
		});
	};

	const onTaxonValueChange = (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else {
			value.curie = event.value;
		}
		alleleDispatch({
			type: 'EDIT',
			field: 'taxon',
			value,
		});
	};

	const onInCollectionValueChange = (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else if (event.value === '') {
			value = undefined;
		} else {
			value.name = event.value;
		}
		alleleDispatch({
			type: 'EDIT',
			field: 'inCollection',
			value,
		});
	};

	const onIsExtinctValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'isExtinct',
			value: event.value,
		});
	};

	const onInternalValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'internal',
			value: event.value,
		});
	};

	const onObsoleteValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'obsolete',
			value: event.value,
		});
	};

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const headerText = () => {
		let prefix = 'Allele: ';
		if (alleleState.allele?.alleleSymbol?.displayText && alleleState.allele?.primaryExternalId) {
			return `${prefix} ${alleleState.allele.alleleSymbol.displayText} (${alleleState.allele.primaryExternalId})`;
		}
		if (alleleState.allele?.primaryExternalId) {
			return `${prefix} ${alleleState.allele.primaryExternalId}`;
		}
		return 'Allele Detail Page';
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!allelePutRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1 dangerouslySetInnerHTML={{ __html: headerText() }} />
						</SplitterPanel>
						<SplitterPanel size={35} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={ALLELE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={20} className="flex justify-content-start py-3">
							<Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} />
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<form className="mt-8">
					<FormSection isVisible={isVisible('Curie')}>
						<IdentifierDetailPageTemplate
							identifier={alleleState.allele?.curie}
							label="Curie"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Primary External ID')}>
						<IdentifierDetailPageTemplate
							identifier={alleleState.allele?.primaryExternalId}
							label="Primary External ID"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('MOD Internal ID')}>
						<IdentifierDetailPageTemplate
							identifier={alleleState.allele?.modInternalId}
							label="MOD Internal ID"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Name')}>
						<FullNameForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Symbol')}>
						<SymbolForm state={alleleState} dispatch={alleleDispatch} labelColumnSize={labelColumnSize} />
					</FormSection>

					<FormSection isVisible={isVisible('Synonyms')}>
						<SynonymsForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Secondary IDs')}>
						<SecondaryIdsForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Nomenclature Events')}>
						<NomenclatureEventsForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Taxon')}>
						<TaxonDetailPageEditor
							taxon={alleleState.allele?.taxon}
							onTaxonValueChange={onTaxonValueChange}
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
							errorMessages={alleleState.errorMessages}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Mutation Types')}>
						<MutationTypesForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Functional Impacts')}>
						<FunctionalImpactsForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Germline Transmission Status')}>
						<GermilineTransmissionStatusForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Database Status')}>
						<DatabaseStatusForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Inheritance Modes')}>
						<InheritanceModesForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('References')}>
						<ReferencesForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('In Collection')}>
						<InCollectionDetailPageEditor
							inCollection={alleleState.allele?.inCollection}
							onInCollectionValueChange={onInCollectionValueChange}
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
							errorMessages={alleleState.errorMessages}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Is Extinct')}>
						<BooleanDetailPageEditor
							value={alleleState.allele?.isExtinct}
							name={'isExtinct'}
							label={'Is Extinct'}
							onValueChange={onIsExtinctValueChange}
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
							errorMessages={alleleState.errorMessages}
							showClear={true}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Related Notes')}>
						<RelatedNotesForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Allele Gene Associations')}>
						<AlleleGeneAssociationsForm state={alleleState} dispatch={alleleDispatch} />
					</FormSection>

					<FormSection isVisible={isVisible('Data Provider')}>
						<DataProviderDetailPageTemplate
							dataProvider={alleleState.allele?.dataProvider?.abbreviation}
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Updated By')}>
						<UserDetailPageTemplate
							user={alleleState.allele?.updatedBy?.uniqueId}
							fieldName="Updated By"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Date Updated')}>
						<DateDetailPageTemplate
							date={alleleState.allele?.dateUpdated}
							fieldName="Date Updated"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Created By')}>
						<UserDetailPageTemplate
							user={alleleState.allele?.createdBy?.uniqueId}
							fieldName="Created By"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Date Created')}>
						<DateDetailPageTemplate
							date={alleleState.allele?.dateCreated}
							fieldName="Date Created"
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Internal')}>
						<BooleanDetailPageEditor
							value={alleleState.allele?.internal}
							name={'internal'}
							label={'Internal'}
							onValueChange={onInternalValueChange}
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
							errorMessages={alleleState.errorMessages}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Obsolete')}>
						<BooleanDetailPageEditor
							value={alleleState.allele?.obsolete}
							name={'obsolete'}
							label={'Obsolete'}
							onValueChange={onObsoleteValueChange}
							widgetColumnSize={widgetColumnSize}
							labelColumnSize={labelColumnSize}
							fieldDetailsColumnSize={fieldDetailsColumnSize}
							errorMessages={alleleState.errorMessages}
						/>
					</FormSection>
				</form>
			</ErrorBoundary>
		</>
	);
}
