import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';

export default function AlleleDetailPage(){
	const [allele, setAllele] = useState();
	const { curie } = useParams();
	const alleleService = new AlleleService();

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

	return(
		<>
			<h1>Allele Detail Page</h1>
				<ErrorBoundary>
				<form>
					<div className="grid">
						<div className={labelColumnSize}>
					 		<label htmlFor="subject"><font color={'red'}>*</font>Subject</label>
						</div>
						<div className={widgetColumnSize}>
						</div>
						<div className={fieldDetailsColumnSize}>
							<FormErrorMessageComponent errorMessages={errorMessages} errorField={"subject"}/>
							<FormErrorMessageComponent errorMessages={uiErrorMessages} errorField={"subject"}/>
							<SubjectAdditionalFieldData fieldData={newAnnotation.subject}/>
						</div>
					</div>

					<div className="grid">
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
					</div>

			</form>
				</ErrorBoundary>
		</>
	)
	
};
