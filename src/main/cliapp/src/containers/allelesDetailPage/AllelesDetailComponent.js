import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { AlleleService } from '../../service/AlleleService';

export const AllelesDetailComponent = ({ curie }) => {
	const [allele, setAllele] = useState({});
	//call service
	const alleleService = new AlleleService();
	//retrieve data
	useQuery([curie],
		() => alleleService.getAllele(curie), 
		{
			onSuccess: (data) => {
				setAllele(data.result);
			},
			onError: (error) => {
				console.warn(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
		}
	);
	//display output data
	return(<>{allele}</>)
	
};
