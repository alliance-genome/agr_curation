import React from 'react';
import { BrowserRouter, MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { alleleDetailData } from '../mockData/mockData.js';

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
export const FORM_LOAD_WAIT = { timeout: 5000 };

// The shared fixture carries no id, and the page reads and writes its sub-resources by the allele's id.
export const storedAllele = { ...alleleDetailData.entity, id: 4242 };

export const alleleRejection = {
	response: {
		status: 400,
		statusText: 'Bad Request',
		data: { errorMessage: 'Could not update Allele', errorMessages: { taxon: 'Taxon is not valid' } },
	},
};

export const SOURCE_CURIE = 'AGRKB:101000000000001';

// The allele a create page fills itself from when opened to duplicate one.
export const sourceAllele = {
	type: 'Allele',
	id: 1,
	curie: SOURCE_CURIE,
	primaryExternalId: 'WB:WBVar1',
	internal: false,
	obsolete: false,
	taxon: { curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	alleleSymbol: { id: 4, displayText: 'abc-1', formatText: 'abc-1', nameType: { name: 'nomenclature_symbol' } },
};

export const DUPLICATE_SOURCE_ENTRY = `/allele/create?from=${encodeURIComponent(SOURCE_CURIE)}`;

/**
 * Renders a page under a router, at `entry` when one is given.
 *
 * @param {React.ReactElement} page
 * @param {string} [entry] the location to open, such as {@link DUPLICATE_SOURCE_ENTRY}
 */
export const renderInRouter = (page, entry) =>
	renderWithClient(
		entry ? <MemoryRouter initialEntries={[entry]}>{page}</MemoryRouter> : <BrowserRouter>{page}</BrowserRouter>
	);

/**
 * Renders the detail page at an allele's route, so it reads the identifier from the path.
 *
 * @param {React.ReactElement} page
 */
export const renderAtAlleleRoute = (page) =>
	renderWithClient(
		<MemoryRouter initialEntries={['/allele/MGI:5146840']}>
			<Routes>
				<Route path="/allele/:identifier" element={page} />
			</Routes>
		</MemoryRouter>
	);

/**
 * @param {string} name the button's accessible name
 */
export const button = (name) => screen.getByRole('button', { name });

/**
 * Makes an edit to the allele alone, so the page's Save has something to write.
 *
 * @param {Object} user a `userEvent.setup()` instance
 */
export const markAlleleExtinct = async (user) => {
	const isExtinctRow = screen.getByRole('heading', { name: 'Is Extinct' }).closest('.grid');
	await user.click(isExtinctRow.querySelector('.p-dropdown'));
	const panel = await waitFor(() => {
		const openPanel = document.querySelector('.p-dropdown-panel');
		expect(openPanel).not.toBeNull();
		return openPanel;
	});
	await user.click(within(panel).getByText('true'));
};
