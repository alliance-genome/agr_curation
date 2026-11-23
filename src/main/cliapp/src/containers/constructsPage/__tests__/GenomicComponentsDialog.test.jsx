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
		expect(within(dialog).getByText('RelatedNotes')).toBeInTheDocument();
		expect(within(dialog).getByText('Evidence')).toBeInTheDocument();
	});

	it('leaves out the Related Notes and Evidence columns when told to', async () => {
		const sfGFP = { primaryExternalId: 'FB:FBto0000063', transgenicToolSymbol: { displayText: 'sfGFP' } };
		const result = renderDialog(
			[{ id: 4, relation: { name: 'is_compatible_with' }, transgenicToolTransgenicToolAssociationObject: sfGFP }],
			{
				objectField: 'transgenicToolTransgenicToolAssociationObject',
				title: 'Transgenic Tool Associations',
				getComponentLabel: (tool) => tool.transgenicToolSymbol.displayText,
				showNotesAndEvidence: false,
			}
		);

		const dialog = await result.findByRole('dialog');
		expect(await within(dialog).findByText('is_compatible_with')).toBeInTheDocument();
		expect(within(dialog).queryByText('RelatedNotes')).not.toBeInTheDocument();
		expect(within(dialog).queryByText('Evidence')).not.toBeInTheDocument();
		expect(within(dialog).getByText('Relation')).toBeInTheDocument();
		expect(within(dialog).getByText('Obsolete')).toBeInTheDocument();
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

	it('shows a component with the label it is given', async () => {
		const sfGFP = { primaryExternalId: 'FB:FBto0000063', transgenicToolSymbol: { displayText: 'sfGFP' } };
		const result = renderDialog(
			[{ id: 3, relation: { name: 'tagged_with' }, cassetteTransgenicToolAssociationObject: sfGFP }],
			{
				objectField: 'cassetteTransgenicToolAssociationObject',
				title: 'Transgenic Tool Components',
				getComponentLabel: (tool) => `${tool.transgenicToolSymbol.displayText} (${tool.primaryExternalId})`,
			}
		);

		const dialog = await result.findByRole('dialog');
		expect(within(dialog).getByText('Transgenic Tool Components')).toBeInTheDocument();
		expect(await within(dialog).findByText('tagged_with')).toBeInTheDocument();
		expect(within(dialog).getAllByText('sfGFP (FB:FBto0000063)').length).toBeGreaterThan(0);
	});
});
