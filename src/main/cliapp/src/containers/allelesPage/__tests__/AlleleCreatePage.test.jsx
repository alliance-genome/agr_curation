import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the service directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		createAllele = createAllele;
	},
}));

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<AlleleCreatePage />
		</BrowserRouter>
	);

const heading = (name) => screen.queryByRole('heading', { name });
const button = (name) => screen.getByRole('button', { name });

describe('<AlleleCreatePage />', () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { curie: 'AGRKB:101000000000001' } } });
		navigate.mockReset();
		window.localStorage.removeItem('AlleleFormSettings');
	});

	it('Renders the create form rather than the detail fields', async () => {
		await renderPage();

		expect(heading('Add Allele')).toBeInTheDocument();
		expect(screen.getByLabelText('primaryExternalId')).toBeInTheDocument();
		expect(heading('Curie')).not.toBeInTheDocument();
		expect(heading('Date Created')).not.toBeInTheDocument();
	});

	it('Offers all four actions', async () => {
		await renderPage();

		expect(button('Clear')).toBeInTheDocument();
		expect(button('Cancel')).toBeInTheDocument();
		expect(button('Save & Close')).toBeInTheDocument();
		expect(button('Save & Add Another')).toBeInTheDocument();
	});

	it('Blocks a save with no identifier or taxon and sends no request', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => {
			expect(screen.getAllByText('Required').length).toBeGreaterThan(0);
		});
		expect(createAllele).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Blocks a save when only the identifier is set', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.type(screen.getByLabelText('primaryExternalId'), 'WB:WBVar00000001');
		await user.click(button('Save & Close'));

		await waitFor(() => {
			expect(screen.getAllByText('Required').length).toBeGreaterThan(0);
		});
		expect(createAllele).not.toHaveBeenCalled();
	});

	it('Clears a populated field', async () => {
		const user = userEvent.setup();
		await renderPage();

		const identifier = screen.getByLabelText('primaryExternalId');
		await user.type(identifier, 'WB:WBVar00000001');
		expect(identifier).toHaveValue('WB:WBVar00000001');

		await user.click(button('Clear'));

		await waitFor(() => {
			expect(screen.getByLabelText('primaryExternalId')).toHaveValue('');
		});
	});

	it('Posts a payload without the blank placeholders and opens the new allele', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.type(screen.getByLabelText('primaryExternalId'), 'WB:WBVar00000001');
		await user.type(container.querySelector('input[name="taxon-input"]'), 'NCBITaxon:6239');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled());

		const payload = createAllele.mock.calls[0][0];
		expect(payload.primaryExternalId).toEqual('WB:WBVar00000001');
		expect(payload.taxon).toEqual({ curie: 'NCBITaxon:6239' });
		// the blank inCollection the initial state carries would be rejected by the API
		expect(payload).not.toHaveProperty('inCollection');

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001'));
	});

	it('Stays on a blank form on save and add another', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.type(screen.getByLabelText('primaryExternalId'), 'WB:WBVar00000001');
		await user.type(container.querySelector('input[name="taxon-input"]'), 'NCBITaxon:6239');
		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled());

		expect(navigate).not.toHaveBeenCalled();
		await waitFor(() => {
			expect(screen.getByLabelText('primaryExternalId')).toHaveValue('');
		});
	});

	it('Returns to the alleles table on cancel', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Cancel'));

		expect(navigate).toHaveBeenCalledWith('/alleles');
	});
});
