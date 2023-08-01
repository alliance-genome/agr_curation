import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { TaxonAdditionalFieldData } from '../../components/FieldData/TaxonAdditionalFieldData';
import { FormErrorMessageComponent } from '../../components/Error/FormErrorMessageComponent';
import { TaxonFormEditor } from '../../components/Editors/taxon/TaxonFormEditor';

export default function AlleleDetailPage(){
	const [allele, setAllele] = useState();
	const { curie } = useParams();
	const alleleService = new AlleleService();
	const errorMessages = useState([]);

	const labelColumnSize = "col-3";
	const widgetColumnSize = "col-4";
	const fieldDetailsColumnSize = "col-5";

	useQuery([curie],
		() => alleleService.getAllele(curie), 
		{
			onSuccess: (result) => {
				setAllele(result?.data?.entity);
			},
			onError: (error) => {
				console.warn(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
		}
	);

	const onTaxonValueChange = () => {
		console.log("onTaxonValueChange");
	}

	return(
		<>
			<h1>Allele Detail Page</h1>
			<ErrorBoundary>
				<form>
					<div className="grid">
						<div className={labelColumnSize}>
					 		<label htmlFor="taxon"><font color={'red'}>*</font>Taxon</label>
						</div>
						<div className={widgetColumnSize}>
							<TaxonFormEditor curie={allele?.taxon?.curie} onTaxonValueChange={onTaxonValueChange} />
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"taxon"}/>
							<TaxonAdditionalFieldData curie={allele?.taxon?.curie}/>
						</div>
					</div>

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
