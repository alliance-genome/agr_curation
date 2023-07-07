import React, { useState } from 'react';
import ReactJson from 'react-json-view'
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';

export const AllelesDetailComponent = () => {
	const [allele, setAllele] = useState();
	const { curie } = useParams();
	const { layoutColorMode } = localStorage.getItem("themeSettings");
	//call service
	const alleleService = new AlleleService();
	//retrieve data
	useQuery([curie],
		() => alleleService.getAllele(curie), 
		{
			onSuccess: (result) => {
				// const alleleString = JSON.stringify(result.data.entity);
				setAllele(result?.data?.entity);
			},
			onError: (error) => {
				console.warn(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
		}
	);
	//display output data
	return(
		<>
			<h1>Allele Detail Page</h1>
			<ReactJson src={allele} theme={layoutColorMode === "light" ? "rjv-default" : "google"} />
		</>
	)
	
};
