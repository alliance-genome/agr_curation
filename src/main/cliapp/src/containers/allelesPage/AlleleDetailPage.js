import React, { useState } from 'react';
import { Divider } from 'primereact/divider';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonFormEditor } from '../../components/Editors/taxon/TaxonFormEditor';
import { useAlleleReducer } from './useAlleleReducer';
import { ReferencesFormEditor } from '../../components/Editors/references/ReferencesFormEditor';
import { InCollectionFormEditor } from '../../components/Editors/inCollection/InCollectionFormEditor';

export default function AlleleDetailPage(){
	const { curie } = useParams();
	const alleleService = new AlleleService();
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

					{/* <div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="assertedGenes">Asserted Genes</label>
						</div>
						<div className={widgetColumnSize}>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"assertedGenes"}/>
							<AssertedGenesAdditionalFieldData fieldData={newAnnotation.assertedGenes}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="assertedAllele">Asserted Allele</label>
						</div>
						<div className={widgetColumnSize}>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"assertedAllele"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"assertedAllele"}/>
							<AssertedAlleleAdditionalFieldData fieldData={newAnnotation.assertedAllele}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="diseaseRelation"><font color={'red'}>*</font>Disease Relation</label>
						</div>
						<div className={widgetColumnSize}>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"diseaseRelation"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="negated">Negated</label>
						</div>
						<div className={widgetColumnSize}>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"negated"}/>
						</div>
					</div>

					<div className="grid">
						<div className={labelColumnSize}>
							<label htmlFor="object"><font color={'red'}>*</font>Disease</label>
						</div>
						<div className={widgetColumnSize}>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"object"}/>
							<DiseaseAdditionalFieldData fieldData={newAnnotation.object}/>
						</div>
					</div> */}

			</form>
		</ErrorBoundary>
		</>
	)
	
};
