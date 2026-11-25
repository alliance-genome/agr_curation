import React from 'react';
import { render, within } from '@testing-library/react';
import { DatabaseStatusReadOnlyDialog } from '../databaseStatus/DatabaseStatusReadOnlyDialog';

const renderDialog = (databaseStatuses) =>
	render(
		<DatabaseStatusReadOnlyDialog
			originalDatabaseStatusData={{ originalDatabaseStatuses: databaseStatuses, isInEdit: false, dialog: true }}
			setOriginalDatabaseStatusData={() => {}}
		/>
	);

describe('<DatabaseStatusReadOnlyDialog />', () => {
	it('lists the evidence as sorted reference strings, each cross reference shown before its curie', async () => {
		const result = renderDialog([
			{
				databaseStatus: { name: 'approved' },
				internal: false,
				evidence: [
					{ curie: 'AGRKB:101000000000002', crossReferences: [{ referencedCurie: 'PMID:12345' }] },
					{ curie: 'AGRKB:101000000000001' },
				],
			},
		]);

		const dialog = await result.findByRole('dialog');
		const evidenceItems = (await within(dialog).findAllByRole('listitem')).map((item) => item.textContent);
		expect(evidenceItems).toEqual(['AGRKB:101000000000001', 'PMID:12345 (AGRKB:101000000000002)']);
	});

	it('leaves the Evidence cell empty when a status has no evidence', async () => {
		const result = renderDialog([{ databaseStatus: { name: 'approved' }, internal: false }]);

		const dialog = await result.findByRole('dialog');
		expect(await within(dialog).findByText('approved')).toBeInTheDocument();
		expect(within(dialog).queryAllByRole('listitem')).toHaveLength(0);
	});
});
