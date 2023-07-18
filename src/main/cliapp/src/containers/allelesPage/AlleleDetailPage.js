import React, { useState } from 'react';
import ReactJson from 'react-json-view'
import { useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';

export default function AlleleDetailPage(){
	const [allele, setAllele] = useState();
	const { curie } = useParams();
	const { layoutColorMode } = localStorage.getItem("themeSettings");
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
			<ReactJson src={allele} theme={layoutColorMode === "light" ? "rjv-default" : "google"} />
		</>
	)
	
};
