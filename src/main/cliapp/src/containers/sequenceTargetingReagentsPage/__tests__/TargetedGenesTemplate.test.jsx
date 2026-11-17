import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import { TargetedGenesTemplate } from '../TargetedGenesTemplate';
import { data } from '../mockData/mockData';

describe('<TargetedGenesTemplate />', () => {
	const str = data.results[0];

	it('lists the preview and how many more there are, and opens the pop-up', () => {
		const handleOpen = vi.fn();
		const result = render(<TargetedGenesTemplate sequenceTargetingReagent={str} handleOpen={handleOpen} />);
		expect(result.getByText('targets: dre-mir-430a-1')).toBeInTheDocument();
		expect(result.getByText('targets: dre-mir-430a-2')).toBeInTheDocument();
		expect(result.getByText('and 5 more')).toBeInTheDocument();
		fireEvent.click(result.getByRole('button'));
		expect(handleOpen).toHaveBeenCalledWith(str);
	});

	it('renders nothing without targeted genes', () => {
		const result = render(
			<TargetedGenesTemplate
				sequenceTargetingReagent={{ targetedGenesPreview: [], targetedGeneCount: 0 }}
				handleOpen={() => {}}
			/>
		);
		expect(result.container).toBeEmptyDOMElement();
	});
});
