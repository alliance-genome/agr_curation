import React from 'react';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { PhenotypeAnnotationsPage } from '../index';
import '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData';
import 'core-js/features/structured-clone';

describe('<PhenotypeAnnotationsPage />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(<PhenotypeAnnotationsPage />);

		await waitFor(() => {
			expect(result);
		});
	}, 10000);

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(<PhenotypeAnnotationsPage />);

		const tableTitle = await result.findByText(/Phenotype Annotations Table/i);
		expect(tableTitle).toBeInTheDocument();
	}, 10000);

	it('The table contains correct data', async () => {
		let result = await renderWithClient(<PhenotypeAnnotationsPage />);

		const uniqueIdTd = await result.findByText(
			'ZFIN:ZDB-FISH-150901-427|has_phenotype|head decreased size, abnormal|PATO:0000587|ZFA:0001114|AGRKB:101000000388372|has_condition|ZECO:0000103|standard conditions'
		);
		const subjectTd = await result.findByText(/slc35d1ahi3378Tg\/hi3378Tg/i);
		const relationTd = await result.findByText('has_phenotype');
		const objectTd = await result.findByText('head decreased size, abnormal');
		const referenceTd = await result.findByText(/PMID:15256591/i);
		const inferredGeneTd = await result.findByText('Acd (MGI:87873)');
		const inferredAlleleTd = await result.findByText('Ccdt (MGI:1856658)');
		const assertedGeneTd = await result.findByText('Acox1 (MGI:1330812)');
		const assertedAlleleTd = await result.findByText('Acox1tm1Jkr (MGI:1857811)');
		const dataProviderTd = await result.findByText('ZFIN');
		const dateCreatedTd = await result.findByText('2024-01-17T15:26:57Z');

		await waitFor(() => {
			expect(uniqueIdTd).toBeInTheDocument();
			expect(subjectTd).toBeInTheDocument();
			expect(relationTd).toBeInTheDocument();
			expect(objectTd).toBeInTheDocument();
			expect(referenceTd).toBeInTheDocument();
			expect(inferredGeneTd).toBeInTheDocument();
			expect(inferredAlleleTd).toBeInTheDocument();
			expect(assertedGeneTd).toBeInTheDocument();
			expect(assertedAlleleTd).toBeInTheDocument();
			expect(dataProviderTd).toBeInTheDocument();
			expect(dateCreatedTd).toBeInTheDocument();
		});
	}, 10000);
});
