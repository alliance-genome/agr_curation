import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { geneDetailData } from '../mockData/mockData.js';

// Each wait follows a stubbed call through a full render of the form, which can outlast the default under load.
export const FORM_LOAD_WAIT = { timeout: 5000 };

export const storedGene = geneDetailData.entity;

export const geneRejection = {
	response: {
		status: 400,
		statusText: 'Bad Request',
		data: {
			errorMessage: 'Could not update Gene: [FB:FBgn0019932]',
			errorMessages: { gcrpCrossReference: 'referencedCurie - Invalid' },
			supplementalData: { errorMap: { gcrpCrossReference: 'referencedCurie - Invalid' } },
		},
	},
};

/**
 * Renders the detail page at a gene's route, so it reads the identifier from the path.
 *
 * @param {React.ReactElement} page
 */
export const renderAtGeneRoute = (page) =>
	renderWithClient(
		<MemoryRouter initialEntries={[`/gene/${storedGene.primaryExternalId}`]}>
			<Routes>
				<Route path="/gene/:identifier" element={page} />
			</Routes>
		</MemoryRouter>
	);

/**
 * @param {string} name a section's heading
 * @returns {HTMLElement} the section under it
 */
export const section = (name) => screen.getByRole('heading', { name }).closest('.grid').parentElement;
