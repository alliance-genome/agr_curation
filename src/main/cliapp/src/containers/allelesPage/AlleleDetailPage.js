import React, { useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Divider } from 'primereact/divider';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonFormEditor } from '../../components/Editors/taxon/TaxonFormEditor';
import { useAlleleReducer } from './useAlleleReducer';
import { ReferencesFormEditor } from '../../components/Editors/references/ReferencesFormEditor';
import { InCollectionFormEditor } from '../../components/Editors/inCollection/InCollectionFormEditor';
import { PageFooter } from './PageFooter';
import { BooleanFormEditor } from '../../components/Editors/boolean/BooleanFormEditor';
import { CurieFormTemplate } from '../../components/Templates/CurieFormTemplate';
import { DataProviderFormTemplate } from '../../components/Templates/DataProviderFormTemplate';
import { DateFormTemplate } from '../../components/Templates/DateFormTemplate';
import { UserFormTemplate } from '../../components/Templates/UserFormTemplate';
import { SynonymsForm } from './SynonymsForm';

export default function AlleleDetailPage(){
	const { curie } = useParams();
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const labelColumnSize = "col-3";
	const widgetColumnSize = "col-4";
	const fieldDetailsColumnSize = "col-5";

const { isLoading } =	useQuery([curie],
		() => alleleService.getAllele(curie), 
		{
			onSuccess: (result) => {
				alleleDispatch({type: 'SET', value: result?.data?.entity});
			},
			onError: (error) => {
				console.warn(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
		}
	);

	const mutation = useMutation(allele => {
		return alleleService.saveAllele(allele);
	});

	const handleSubmit = (event) => {
		alleleDispatch({
			type: "SUBMIT" 
		})
		event.preventDefault();
		mutation.mutate(alleleState.allele, {
			onSuccess: (data) => {
				toastSuccess.current.show({severity: 'success', summary: 'Successful', detail: 'Allele Saved'});
				console.log(alleleState.errorMessages);
			},
			onError: (error) => {
				let message;
				if(error?.response?.data?.errorMessage){
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`
				}
				toastError.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false}
				]);

				alleleDispatch(
					{
						type: "UPDATE_ERROR_MESSAGES", 
						errorType: "errorMessages", 
						errorMessages: error.response?.data?.errorMessages || {}
					}
				);
			}
		})
	}

	const onTaxonValueChange = (event) => {
		let value = {};
		if(typeof event.value === "object"){
			value = event.value;
		} else {
			value.curie = event.value;
		}
		alleleDispatch({
			type: 'EDIT',
			field: 'taxon',
			value,
		})
	}

	const onReferenceValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'references',
			value: event.value,
		})
	}

	const onInCollectionValueChange = (event) => {
		let value = {};
		if(typeof event.value === "object"){
			value = event.value;
		} else if(event.value === "") {
			value = undefined;
		} else {
			value.name = event.value;
		}
		alleleDispatch({
			type: 'EDIT',
			field: 'inCollection',
			value,
		})
	}

	const onIsExtinctValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'isExtinct',
			value: event.value,
		})
	}

	const onInternalValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'internal',
			value: event.value,
		})
	}

	const onObsoleteValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'obsolete',
			value: event.value,
		})
	}
	
	if(isLoading) return (
		<div className='flex align-items-center justify-content-center h-screen'>
			<ProgressSpinner/>
		</div>
	)

	const headerText = (allele) => {
		let prefix = "Allele: "
		if (allele.alleleSymbol?.displayText && allele?.curie) {
			return `${prefix} ${allele.alleleSymbol.displayText} (${allele.curie})`;
		}
		if (allele?.curie) {
			return `${prefix} ${allele.curie}`;
		}
		return "Allele Detail Page";
	}

	return(
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<h1 dangerouslySetInnerHTML={{ __html: headerText(alleleState.allele) }}/>
			<ErrorBoundary>
				<form>

					<CurieFormTemplate
						curie={alleleState.allele?.curie}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<SynonymsForm
						state={alleleState}
						dispatch={alleleDispatch}
						labelColumnSize={labelColumnSize}
					/>

					<Divider />

					<TaxonFormEditor 
						taxon={alleleState.allele?.taxon} 
						onTaxonValueChange={onTaxonValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<ReferencesFormEditor 
						references={alleleState.allele?.references} 
						onReferencesValueChange={onReferenceValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<InCollectionFormEditor
						inCollection={alleleState.allele?.inCollection} 
						onInCollectionValueChange={onInCollectionValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<BooleanFormEditor
						value={alleleState.allele?.isExtinct} 
						name={"isExtinct"}
						label={"Is Extinct"}
						onValueChange={onIsExtinctValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<DataProviderFormTemplate
						dataProvider={alleleState.allele?.dataProvider?.sourceOrganization?.abbreviation}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserFormTemplate
						user={alleleState.allele?.updatedBy?.uniqueId}
						fieldName="Updated By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateFormTemplate
						date={alleleState.allele?.dateUpdated}
						fieldName="Date Updated"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserFormTemplate
						user={alleleState.allele?.createdBy?.uniqueId}
						fieldName="Created By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateFormTemplate
						date={alleleState.allele?.dateCreated}
						fieldName="Date Created"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<BooleanFormEditor
						value={alleleState.allele?.internal} 
						name={"internal"}
						label={"Internal"}
						onValueChange={onInternalValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

					<BooleanFormEditor
						value={alleleState.allele?.obsolete} 
						name={"obsolete"}
						label={"Obsolete"}
						onValueChange={onObsoleteValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider />

			</form>
			<PageFooter handleSubmit={handleSubmit}/>
		</ErrorBoundary>
		</>
	)
	
};
