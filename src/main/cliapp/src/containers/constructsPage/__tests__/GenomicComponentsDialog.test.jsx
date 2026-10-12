import React from 'react';
import { render, within } from '@testing-library/react';
import { GenomicComponentsDialog } from '../GenomicComponentsDialog';

const croc = { type: 'Gene', primaryExternalId: 'FB:FBgn0014143', geneSymbol: { displayText: 'croc' } };

const renderDialog = (associations, props = {}) =>
	render(
		<GenomicComponentsDialog
			originalComponentsData={{ dialog: true, originalComponents: associations }}
			setOriginalComponentsData={() => {}}
			{...props}
		/>
	);

describe('<GenomicComponentsDialog />', () => {
	it('lists the genomic entity associations of a cassette', async () => {
		const result = renderDialog(
			[{ id: 1, relation: { name: 'targets' }, cassetteGenomicEntityAssociationObject: croc, relatedNotes: [] }],
			{ objectField: 'cassetteGenomicEntityAssociationObject', title: 'Genomic Entity Components' }
		);

		const dialog = await result.findByRole('dialog');
		expect(within(dialog).getByText('Genomic Entity Components')).toBeInTheDocument();
		// the rows are filled in by onShow, once the dialog has opened
		expect(await within(dialog).findByText('targets')).toBeInTheDocument();
		expect(within(dialog).getAllByText('croc (FB:FBgn0014143)').length).toBeGreaterThan(0);
		expect(within(dialog).getByText('Notes(0)')).toBeInTheDocument();
	});

	it('lists the genomic entity associations of a construct by default', async () => {
		const result = renderDialog([
			{ id: 2, relation: { name: 'expresses' }, constructGenomicEntityAssociationObject: croc },
		]);

		const dialog = await result.findByRole('dialog');
		expect(within(dialog).getByText('Component Associations')).toBeInTheDocument();
		expect(await within(dialog).findByText('expresses')).toBeInTheDocument();
		expect(within(dialog).getAllByText('croc (FB:FBgn0014143)').length).toBeGreaterThan(0);
	});
});
