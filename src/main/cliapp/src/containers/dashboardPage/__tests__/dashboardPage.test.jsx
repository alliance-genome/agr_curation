import React from 'react';
import { renderWithClient } from '../../../tools/jest/utils';
import { DashboardPage } from '../index';
import '../../../tools/jest/setupTests';

describe('<DashboardPage />', () => {
	it('Renders without crashing', async () => {
		let result = await renderWithClient(<DashboardPage />);
		await expect(result);
	});

	it('Contains the Table Headers', async () => {
		let result = await renderWithClient(<DashboardPage />);

		const entities = await result.getByText(/Entities/i);
		const ontologies = await result.getByText(/Ontologies/i);
		const system = await result.getByText(/System/i);
		expect(entities).toBeInTheDocument();
		expect(ontologies).toBeInTheDocument();
		expect(system).toBeInTheDocument();
	});
});
