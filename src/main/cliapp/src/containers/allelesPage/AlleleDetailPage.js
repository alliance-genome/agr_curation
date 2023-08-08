import React, { useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Divider } from 'primereact/divider';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonFormEditor } from '../../components/Editors/taxon/TaxonFormEditor';
import { useAlleleReducer } from './useAlleleReducer';
import { ReferencesFormEditor } from '../../components/Editors/references/ReferencesFormEditor';
import { InCollectionFormEditor } from '../../components/Editors/inCollection/InCollectionFormEditor';
import { IsExtinctFormEditor } from '../../components/Editors/isExtinct/IsExtinctFormEditor';
import { InternalFormEditor } from '../../components/Editors/internal/InternalFormEditor';
import { ObsoleteFormEditor } from '../../components/Editors/obsolete/ObsoleteFormEditor';
import { PageFooter } from './PageFooter';

export default function AlleleDetailPage(){
	const { curie } = useParams();
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const labelColumnSize = "col-3";
	const widgetColumnSize = "col-4";
	const fieldDetailsColumnSize = "col-5";

	useQuery([curie],
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

	return(
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<h1>Allele Detail Page</h1>
			<ErrorBoundary>
				<form>

					<TaxonFormEditor 
						taxon={alleleState.allele?.taxon} 
						onTaxonValueChange={onTaxonValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider/>

					<ReferencesFormEditor 
						references={alleleState.allele?.references} 
						onReferencesValueChange={onReferenceValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider/>

					<InCollectionFormEditor
						inCollection={alleleState.allele?.inCollection} 
						onInCollectionValueChange={onInCollectionValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider/>

					<IsExtinctFormEditor
						isExtinct={alleleState.allele?.isExtinct} 
						onIsExtinctValueChange={onIsExtinctValueChange} 
						booleanTerms={booleanTerms}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider/>

					<InternalFormEditor
						internal={alleleState.allele?.internal} 
						onInternalValueChange={onInternalValueChange} 
						booleanTerms={booleanTerms}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>

					<Divider/>

					<ObsoleteFormEditor
						obsolete={alleleState.allele?.obsolete} 
						onObsoleteValueChange={onObsoleteValueChange} 
						booleanTerms={booleanTerms}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={alleleState.errorMessages}
					/>
			</form>
			<PageFooter handleSubmit={handleSubmit}/>
		</ErrorBoundary>
		</>
	)
	
};
