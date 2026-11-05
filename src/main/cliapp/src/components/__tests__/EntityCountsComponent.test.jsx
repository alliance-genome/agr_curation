import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';

const getSiteSummary = vi.fn();
const reindex = vi.fn();

vi.mock('../../service/SystemService', () => ({
	SystemService: class {
		getSiteSummary = getSiteSummary;
		reindex = reindex;
	},
}));

const { EntityCountsComponent } = await import('../EntityCountsComponent');

describe('EntityCountsComponent', () => {
	beforeEach(() => {
		getSiteSummary.mockReset();
		getSiteSummary.mockResolvedValue({
			entity: {
				Gene: { dbCount: 10, esCount: 10 },
				Reference: { dbCount: 5, esCount: 5 },
				DOTerm: { dbCount: 3, esCount: 3 },
			},
		});
		reindex.mockReset();
		reindex.mockResolvedValue({ status: 204 });
	});

	it('shows reindex buttons only for rows with a reindex endpoint', async () => {
		render(<EntityCountsComponent />);
		await screen.findByText('Genes');
		expect(screen.getByLabelText('Reindex Genes')).toBeInTheDocument();
		expect(screen.getByLabelText('Reindex DO')).toBeInTheDocument();
		expect(screen.queryByLabelText('Reindex Literature References')).not.toBeInTheDocument();
	});

	it('calls the entity reindex endpoint after confirmation', async () => {
		render(<EntityCountsComponent />);
		fireEvent.click(await screen.findByLabelText('Reindex Genes'));
		expect(reindex).not.toHaveBeenCalled();
		fireEvent.click(await screen.findByText('Yes'));
		await waitFor(() => expect(reindex).toHaveBeenCalledWith('gene'));
		expect(await screen.findByText('Reindex Started')).toBeInTheDocument();
	});

	it('shows an error toast when the reindex request fails', async () => {
		reindex.mockRejectedValue(Object.assign(new Error('Forbidden'), { response: { status: 403 } }));
		render(<EntityCountsComponent />);
		fireEvent.click(await screen.findByLabelText('Reindex DO'));
		fireEvent.click(await screen.findByText('Yes'));
		await waitFor(() => expect(reindex).toHaveBeenCalledWith('doterm'));
		expect(await screen.findByText('Reindex Failed')).toBeInTheDocument();
	});
});
