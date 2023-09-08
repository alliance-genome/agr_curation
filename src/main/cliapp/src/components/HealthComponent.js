import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { Button } from 'primereact/button';
import { HealthService } from '../service/HealthService';

export const HealthComponent = () => {

	const [health, setHealth] = useState(null);
	const [refresh, setRefresh] = useState(false);
	
	const healthService = new HealthService();

	useQuery(['getHealth', refresh],
		() => healthService.getHealth(), {
			onSuccess: (results) => {
				// console.log(results);
				setHealth(results);
			},
			onError: (error) => {
				console.log(error);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false
		}
	);

	return (
		<div className="card">
			<div className="flex justify-content-between">
				<h2>Healt Status</h2>
				<Button onClick={() => setRefresh(!refresh)} label="Refresh Table" />
			</div>
			<div className="fixed"><pre style={{whiteSpace: "pre-wrap"}}>{JSON.stringify(health, null, 2) }</pre></div>
		</div>
	)
}
