import { render, fireEvent } from '@testing-library/react';
import { EvidenceCodesTemplate } from '../EvidenceCodesTemplate';
import '../../../tools/jest/setupTests';

describe('EvidenceCodesTemplate', () => {
	it('should render a sorted list of evidence codes', () => {
		const evidenceCodes = [
			{ abbreviation: 'Code B', name: 'Code B Name', curie: 'Code B Curie' },
			{ abbreviation: 'Code A', name: 'Code A Name', curie: 'Code A Curie' },
			{ abbreviation: 'Code C', name: 'Code C Name', curie: 'Code C Curie' },
		];

		const { container } = render(<EvidenceCodesTemplate evidenceCodes={evidenceCodes} />);
		const listItems = container.querySelectorAll('li');

		expect(listItems[0]).toHaveTextContent('Code A - Code A Name (Code A Curie)');
		expect(listItems[1]).toHaveTextContent('Code B - Code B Name (Code B Curie)');
		expect(listItems[2]).toHaveTextContent('Code C - Code C Name (Code C Curie)');
	});

	it('should return null when evidenceCodes is undefined', () => {
		const { container } = render(<EvidenceCodesTemplate evidenceCodes={undefined} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return null when evidenceCodes is an empty array', () => {
		const evidenceCodes = [];

		const { container } = render(<EvidenceCodesTemplate evidenceCodes={evidenceCodes} />);
		expect(container.firstChild).toBeNull();
	});

	it('should display tooltip when hovering over the evidence codes list', async () => {
		const evidenceCodes = [
			{ abbreviation: 'ABC', name: 'Code ABC', curie: 'ABC123' },
			{ abbreviation: 'DEF', name: 'Code DEF', curie: 'DEF456' },
			{ abbreviation: 'GHI', name: 'Code GHI', curie: 'GHI789' },
		];

		const result = render(<EvidenceCodesTemplate evidenceCodes={evidenceCodes} />);

		let listContentArray = result.getAllByText('ABC - Code ABC (ABC123)');
		expect(listContentArray).toHaveLength(1);

		fireEvent.mouseEnter(result.container.firstChild);

		//using find... here because it's async and the tooltip is dynamically added
		listContentArray = await result.findAllByText('ABC - Code ABC (ABC123)');

		expect(listContentArray).toHaveLength(2);
	});
});
