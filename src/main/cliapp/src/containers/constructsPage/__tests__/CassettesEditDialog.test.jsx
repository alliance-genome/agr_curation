import React from 'react';
import { fireEvent, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { setupFindHandler } from '../../../tools/jest/commonMswhandlers';
import { CassettesEditDialog } from '../CassettesEditDialog';

const storedAssociation = {
	id: 1,
	relation: { id: 10, name: 'has_component' },
	constructCassetteAssociationObject: {
		id: 200096001,
		primaryExternalId: 'ZFIN:ZDB-TGCONSTRCT-260422-7_cas',
		cassetteSymbol: { displayText: 'Tg(tnfa:EGFP)' },
	},
};

const renderDialog = (editorCallback = vi.fn()) =>
	renderWithClient(
		<CassettesEditDialog
			originalCassettesData={{
				dialog: true,
				isInEdit: true,
				rowIndex: 0,
				originalCassettes: [storedAssociation],
				mainRowProps: { editorCallback },
			}}
			setOriginalCassettesData={() => {}}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={() => {}}
		/>
	);

describe('<CassettesEditDialog />', () => {
	beforeEach(() => {
		setupFindHandler();
	});

	it('lists the stored cassettes without editors', async () => {
		const result = await renderDialog();
		const dialog = await result.findByRole('dialog');

		expect(await within(dialog).findByText('Tg(tnfa:EGFP) (ZFIN:ZDB-TGCONSTRCT-260422-7_cas)')).toBeInTheDocument();
		expect(within(dialog).getByText('has_component')).toBeInTheDocument();
		expect(within(dialog).queryAllByRole('combobox')).toHaveLength(0);
	});

	it('keeps the stored cassettes when no cassette is added', async () => {
		const editorCallback = vi.fn();
		const result = await renderDialog(editorCallback);
		const dialog = await result.findByRole('dialog');
		await within(dialog).findByText('has_component');

		fireEvent.click(within(dialog).getByText('Keep Edits'));

		await vi.waitFor(() => expect(editorCallback).toHaveBeenCalledWith([storedAssociation]));
	});

	it('adds an editable row for a new cassette', async () => {
		const result = await renderDialog();
		const dialog = await result.findByRole('dialog');
		await within(dialog).findByText('has_component');

		fireEvent.click(within(dialog).getByText('Add Cassette'));

		expect(await within(dialog).findAllByRole('combobox')).not.toHaveLength(0);
	});

	it('removes a stored cassette', async () => {
		const editorCallback = vi.fn();
		const result = await renderDialog(editorCallback);
		const dialog = await result.findByRole('dialog');
		await within(dialog).findByText('has_component');

		fireEvent.click(dialog.querySelector('.pi-trash').closest('button'));
		fireEvent.click(within(dialog).getByText('Keep Edits'));

		await vi.waitFor(() => expect(editorCallback).toHaveBeenCalledWith([]));
	});
});
