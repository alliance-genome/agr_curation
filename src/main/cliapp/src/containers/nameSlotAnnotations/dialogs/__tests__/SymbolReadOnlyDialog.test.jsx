import React from 'react';
import { render, within } from '@testing-library/react';
import { SymbolReadOnlyDialog } from '../SymbolReadOnlyDialog';

const symbol = {
	displayText: 'Nemf<sup>tm1Nju</sup>',
	formatText: 'Nemf<tm1Nju>',
	synonymScope: { name: 'exact' },
	nameType: { name: 'nomenclature_symbol' },
	synonymUrl: 'https://www.informatics.jax.org/allele/MGI:8187460',
	internal: false,
	evidence: [
		{ curie: 'AGRKB:101000000000002', crossReferences: [{ referencedCurie: 'PMID:12345' }] },
		{ curie: 'AGRKB:101000000000001' },
	],
};

const renderDialog = (symbols) =>
	render(
		<SymbolReadOnlyDialog
			originalSymbolData={{ originalSymbols: symbols, isInEdit: false, dialog: true }}
			setOriginalSymbolData={() => {}}
		/>
	);

describe('<SymbolReadOnlyDialog />', () => {
	it('lists the evidence as sorted reference strings', async () => {
		const result = renderDialog([symbol]);

		const dialog = await result.findByRole('dialog');
		const evidenceItems = (await within(dialog).findAllByRole('listitem')).map((item) => item.textContent);
		expect(evidenceItems).toEqual(['AGRKB:101000000000001', 'PMID:12345 (AGRKB:101000000000002)']);
	});
});
