import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '../../tools/jest/setupTests';
import { useDeleteOrDeprecateDialogs } from '../DeleteOrDeprecateDialogs';

const ENTITY = { id: 7, curie: 'AGRKB:101000000000007' };

const DialogsHarness = ({ deprecateOption, onDelete, onDeprecate }) => {
	const { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs } = useDeleteOrDeprecateDialogs({
		deprecateOption,
		onDelete,
		onDeprecate,
	});

	return (
		<>
			<button onClick={() => openDeleteOrDeprecateDialog(ENTITY.id, ENTITY)}>Open</button>
			{deleteOrDeprecateDialogs}
		</>
	);
};

const dialog = () => screen.getByRole('dialog');

describe('useDeleteOrDeprecateDialogs', () => {
	it('Deletes after a plain confirmation when deprecation is not offered', async () => {
		const user = userEvent.setup();
		const onDelete = vi.fn(() => Promise.resolve(null));
		render(<DialogsHarness onDelete={onDelete} />);

		await user.click(screen.getByText('Open'));
		await user.click(within(dialog()).getByRole('button', { name: 'Confirm' }));

		expect(onDelete).toHaveBeenCalledWith(ENTITY.id, ENTITY);
		await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument());
	});

	it('Shows why the API refused a deletion', async () => {
		const user = userEvent.setup();
		const onDelete = vi.fn(() => Promise.resolve('Allele AGRKB:101000000000007 is in use'));
		render(<DialogsHarness onDelete={onDelete} />);

		await user.click(screen.getByText('Open'));
		await user.click(within(dialog()).getByRole('button', { name: 'Confirm' }));

		await waitFor(() => expect(screen.getByText('Deletion Error')).toBeInTheDocument());
		expect(screen.getByText('Allele AGRKB:101000000000007 is in use')).toBeInTheDocument();
	});

	it('Offers deprecation, holding deletion behind the confirmation checkbox', async () => {
		const user = userEvent.setup();
		const onDelete = vi.fn(() => Promise.resolve(null));
		const onDeprecate = vi.fn();
		render(<DialogsHarness deprecateOption onDelete={onDelete} onDeprecate={onDeprecate} />);

		await user.click(screen.getByText('Open'));
		expect(within(dialog()).getByRole('button', { name: 'Delete' })).toBeDisabled();

		await user.click(within(dialog()).getByRole('checkbox'));
		await user.click(within(dialog()).getByRole('button', { name: 'Delete' }));

		expect(onDelete).toHaveBeenCalledWith(ENTITY.id, ENTITY);
		expect(onDeprecate).not.toHaveBeenCalled();
	});

	it('Holds deletion behind the checkbox again after a cancel', async () => {
		const user = userEvent.setup();
		render(<DialogsHarness deprecateOption onDelete={vi.fn()} onDeprecate={vi.fn()} />);

		await user.click(screen.getByText('Open'));
		await user.click(within(dialog()).getByRole('checkbox'));
		expect(within(dialog()).getByRole('button', { name: 'Delete' })).toBeEnabled();
		await user.click(within(dialog()).getByRole('button', { name: 'Cancel' }));

		await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument());
		await user.click(screen.getByText('Open'));
		expect(within(dialog()).getByRole('button', { name: 'Delete' })).toBeDisabled();
	});

	it('Deprecates instead of deleting', async () => {
		const user = userEvent.setup();
		const onDelete = vi.fn();
		const onDeprecate = vi.fn();
		render(<DialogsHarness deprecateOption onDelete={onDelete} onDeprecate={onDeprecate} />);

		await user.click(screen.getByText('Open'));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		expect(onDeprecate).toHaveBeenCalledWith(ENTITY);
		expect(onDelete).not.toHaveBeenCalled();
	});
});
