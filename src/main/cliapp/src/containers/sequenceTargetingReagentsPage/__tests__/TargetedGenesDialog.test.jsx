import React from 'react';
import { render, within, waitFor } from '@testing-library/react';
import { TargetedGenesDialog } from '../TargetedGenesDialog';

const find = vi.fn();
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		find = find;
	},
}));

describe('<TargetedGenesDialog />', () => {
	beforeEach(() => {
		find.mockReset();
		find.mockResolvedValue({
			results: [
				{
					id: 1,
					relation: { name: 'targets' },
					sequenceTargetingReagentGeneAssociationObject: {
						primaryExternalId: 'ZFIN:ZDB-MIRNAG-081210-1',
						geneSymbol: { displayText: 'dre-mir-430a-1' },
					},
				},
			],
		});
	});

	it('fetches the first page of the STR targeted genes', async () => {
		const result = render(
			<TargetedGenesDialog
				targetedGenesData={{ dialog: true, sequenceTargetingReagent: { id: 2001, targetedGeneCount: 627 } }}
				setTargetedGenesData={() => {}}
			/>
		);
		const dialog = await result.findByRole('dialog');
		expect(within(dialog).getByText('Targeted Genes')).toBeInTheDocument();
		await waitFor(() =>
			expect(find).toHaveBeenCalledWith('sqtrgeneassociation', 10, 0, {
				'sequenceTargetingReagentAssociationSubject.id': 2001,
			})
		);
		expect(await within(dialog).findByText('dre-mir-430a-1 (ZFIN:ZDB-MIRNAG-081210-1)')).toBeInTheDocument();
	});
});
