import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import { FORM_LOAD_WAIT, renderAtAlleleRoute } from './alleleTestUtils';

const getAllele = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService()
);

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

describe('<AlleleDetailPage /> without an allele', () => {
	beforeEach(() => {
		getAllele.mockReset();
	});

	it('Says no allele has the identifier when the API finds none', async () => {
		getAllele.mockResolvedValue({ data: {} });
		renderAtAlleleRoute(<AlleleDetailPage />);

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Allele not found'),
			FORM_LOAD_WAIT
		);
		expect(screen.getByText('No allele has the identifier MGI:5146840')).toBeInTheDocument();
		expect(screen.getByRole('link', { name: 'Back to the allele table' })).toHaveAttribute('href', '/alleles');
		expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
	});

	it('Reports the failure when the request for the allele fails', async () => {
		getAllele.mockRejectedValue({ response: { status: 500, statusText: 'Internal Server Error', data: {} } });
		renderAtAlleleRoute(<AlleleDetailPage />);

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Allele not loaded'),
			FORM_LOAD_WAIT
		);
		expect(screen.getByText('Could not load allele MGI:5146840: 500 Internal Server Error')).toBeInTheDocument();
		expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
	});
});
