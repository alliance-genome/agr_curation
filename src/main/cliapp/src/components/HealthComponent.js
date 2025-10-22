import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { HealthService } from '../service/HealthService';

export const HealthComponent = () => {
	const [health, setHealth] = useState(null);
	const [refresh, setRefresh] = useState(false);

	const healthService = new HealthService();

	const { data, isSuccess } = useQuery({
		queryKey: ['getHealth', refresh],
		queryFn: () => healthService.getHealth(),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (isSuccess && data) {
			setHealth(data);
		}
	}, [data, isSuccess]);

	return (
		<div className="card">
			<div className="flex justify-content-between">
				<h2>Health Status</h2>
				<Button onClick={() => setRefresh(!refresh)} label="Refresh Table" />
			</div>
			<div className="fixed">
				<pre style={{ whiteSpace: 'pre-wrap' }}>{JSON.stringify(health, null, 2)}</pre>
			</div>
		</div>
	);
};
