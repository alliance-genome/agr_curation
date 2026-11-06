import React from 'react';
import { render, within } from '@testing-library/react';
import { TransgenicToolUsesDialog } from '../TransgenicToolUsesDialog';
import { data } from '../mockData/mockData';

describe('<TransgenicToolUsesDialog />', () => {
	it('lists the uses with their terms', async () => {
		const result = render(
			<TransgenicToolUsesDialog
				usesData={{ dialog: true, originalUses: data.results[0].transgenicToolUses }}
				setUsesData={() => {}}
			/>
		);
		const dialog = await result.findByRole('dialog');
		expect(within(dialog).getByText('Transgenic Tool Uses')).toBeInTheDocument();
		expect(within(dialog).getByText('green fluorescent protein (FBcv:0005012)')).toBeInTheDocument();
	});
});
