import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';

const find = vi.fn();

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		find = find;
	},
}));

const { StrComponentsDialog } = await import('../StrComponentsDialog');

// The association as the cassette row carries it: the STR with its own fields only
const association = {
	id: 42,
	relation: { name: 'expresses (RO:0002292)' },
	evidence: [{ curie: 'AGRKB:101000000000001', referenceID: 'PMID:12345' }],
	relatedNotes: [{ id: 7, freeText: 'A component note.', noteType: { name: 'comment' } }],
	cassetteStrAssociationObject: { id: 5, primaryExternalId: 'FB:FBsf0000086447', name: 'dsRNA-JF02416' },
};

// The STR as the STR table's view returns it
const str = {
	id: 5,
	curie: 'AGRKB:112000000000005',
	primaryExternalId: 'FB:FBsf0000086447',
	name: 'dsRNA-JF02416',
	synonyms: ['JF02416'],
	crossReferences: [{ referencedCurie: 'FB:FBsf0000086447', displayName: 'FB:FBsf0000086447' }],
	taxon: { curie: 'NCBITaxon:7227', name: 'Drosophila melanogaster' },
	targetedGenesPreview: [{ relation: 'targets', geneSymbol: 'croc', geneIdentifier: 'FB:FBgn0014143' }],
	targetedGeneCount: 1,
	dataProvider: { abbreviation: 'FB' },
	internal: false,
	obsolete: false,
};

const renderDialog = () =>
	renderWithClient(
		<StrComponentsDialog
			strComponentsData={{ originalComponents: [association], dialog: true }}
			setStrComponentsData={vi.fn()}
		/>
	);

describe('<StrComponentsDialog />', () => {
	beforeEach(() => find.mockReset());

	it('shows the association and its STR as the STR table shows it', async () => {
		find.mockResolvedValue({ results: [str], totalResults: 1 });
		renderDialog();

		expect(screen.getByText('Sequence Targeting Reagent Components')).toBeInTheDocument();
		expect(screen.getByText('expresses')).toBeInTheDocument();
		expect(screen.getByText('Notes (1)')).toBeInTheDocument();
		// fetched in the STR table's view: the fields the cassette row does not carry
		expect(await screen.findByText('targets: croc')).toBeInTheDocument();
		expect(screen.getByText('AGRKB:112000000000005')).toBeInTheDocument();
		expect(screen.getByText('Drosophila melanogaster (NCBITaxon:7227)')).toBeInTheDocument();
		expect(find).toHaveBeenCalledWith(Endpoints.Entity.SEQUENCE_TARGETING_REAGENT, 1, 0, { id: 5 });
	});

	it("shows the row's STR until the STR has been fetched", async () => {
		let resolveFind;
		find.mockReturnValue(new Promise((resolve) => (resolveFind = resolve)));
		renderDialog();

		expect(screen.getByText('dsRNA-JF02416')).toBeInTheDocument();
		expect(screen.queryByText('AGRKB:112000000000005')).not.toBeInTheDocument();

		resolveFind({ results: [str], totalResults: 1 });
		expect(await screen.findByText('AGRKB:112000000000005')).toBeInTheDocument();
	});
});
