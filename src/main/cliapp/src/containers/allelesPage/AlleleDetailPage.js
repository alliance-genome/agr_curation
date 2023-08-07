import React, { useState } from 'react';
import { Divider } from 'primereact/divider';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonFormEditor } from '../../components/Editors/taxon/TaxonFormEditor';
import { useAlleleReducer } from './useAlleleReducer';
import { ReferencesFormEditor } from '../../components/Editors/references/ReferencesFormEditor';
import { InCollectionFormEditor } from '../../components/Editors/inCollection/InCollectionFormEditor';
import { IsExtinctFormEditor } from '../../components/Editors/isExtinct/IsExtinctFormEditor';
import { InternalFormEditor } from '../../components/Editors/internal/InternalFormEditor';

export default function AlleleDetailPage(){
	const { curie } = useParams();
	const alleleService = new AlleleService();
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const errorMessages = useState([]);//todo: put in reducer?
	const { alleleState, alleleDispatch } = useAlleleReducer();

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

	const onTaxonValueChange = (event) => {
		alleleDispatch({
			type: 'EDIT',
			field: 'taxon',
			value: event.value,
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
		alleleDispatch({
			type: 'EDIT',
			field: 'inCollection',
			value: event.value,
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

	return(
		<>
			<h1>Allele Detail Page</h1>
			<ErrorBoundary>
				<form>

					<TaxonFormEditor 
						taxon={alleleState.allele?.taxon} 
						onTaxonValueChange={onTaxonValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={errorMessages}
					/>

					<Divider/>

					<ReferencesFormEditor 
						references={alleleState.allele?.references} 
						onReferencesValueChange={onReferenceValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={errorMessages}
					/>

					<Divider/>

					<InCollectionFormEditor
						inCollection={alleleState.allele?.inCollection} 
						onInCollectionValueChange={onInCollectionValueChange} 
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={errorMessages}
					/>

					<Divider/>

					<IsExtinctFormEditor
						isExtinct={alleleState.allele?.isExtinct} 
						onIsExtinctValueChange={onIsExtinctValueChange} 
						booleanTerms={booleanTerms}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={errorMessages}
					/>

					<Divider/>

					<InternalFormEditor
						internal={alleleState.allele?.internal} 
						onInternalValueChange={onInternalValueChange} 
						booleanTerms={booleanTerms}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						errorMessages={errorMessages}
					/>

			</form>
		</ErrorBoundary>
		</>
	)
	
};
