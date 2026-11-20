import React from 'react';
import { render, within } from '@testing-library/react';
import { RelatedNotesReadOnlyDialog } from '../RelatedNotesReadOnlyDialog';

const note = {
	noteType: { name: 'comment' },
	freeText: 'Observed in homozygotes',
	internal: false,
	references: [
		{ curie: 'AGRKB:101000000000002', crossReferences: [{ referencedCurie: 'PMID:12345' }] },
		{ curie: 'AGRKB:101000000000001' },
	],
};

const renderDialog = (props = {}) =>
	render(
		<RelatedNotesReadOnlyDialog
			originalRelatedNotesData={{ originalRelatedNotes: [note], isInEdit: false, dialog: true }}
			setOriginalRelatedNotesData={() => {}}
			{...props}
		/>
	);

describe('<RelatedNotesReadOnlyDialog />', () => {
	it("lists a note's references as sorted reference strings", async () => {
		const result = renderDialog();

		const dialog = await result.findByRole('dialog');
		const evidenceItems = (await within(dialog).findAllByRole('listitem')).map((item) => item.textContent);
		expect(evidenceItems).toEqual(['AGRKB:101000000000001', 'PMID:12345 (AGRKB:101000000000002)']);
	});

	it('leaves out the Evidence column when references are not shown', async () => {
		const result = renderDialog({ showReferences: false });

		const dialog = await result.findByRole('dialog');
		expect(await within(dialog).findByText('Observed in homozygotes')).toBeInTheDocument();
		expect(within(dialog).queryByText('Evidence')).not.toBeInTheDocument();
		expect(within(dialog).queryAllByRole('listitem')).toHaveLength(0);
	});
});
