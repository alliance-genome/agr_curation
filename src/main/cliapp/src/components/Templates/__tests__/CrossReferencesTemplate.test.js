import { render, fireEvent } from '@testing-library/react';
import { CrossReferencesTemplate } from '../CrossReferencesTemplate';
import '../../../tools/jest/setupTests';

describe('CrossReferencesTemplate', () => {
	it('should render a sorted list of cross-references with their page name in parenthesis', () => {
		const xrefs = [
			{ displayName: 'xref B', resourceDescriptorPage: { name: 'page B' } },
			{ displayName: 'xref C', resourceDescriptorPage: { name: 'page C' } },
			{ displayName: 'xref A', resourceDescriptorPage: { name: 'page A' } },
		];

		const { container } = render(<CrossReferencesTemplate xrefs={xrefs} />);
		const listItems = container.querySelectorAll('li');

		expect(listItems[0]).toHaveTextContent('xref A (page A)');
		expect(listItems[1]).toHaveTextContent('xref B (page B)');
		expect(listItems[2]).toHaveTextContent('xref C (page C)');
	});

	it('should return null when xrefs is undefined', () => {
		const { container } = render(<CrossReferencesTemplate xrefs={undefined} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return null when xrefs is an empty array', () => {
		const xrefs = [];

		const { container } = render(<CrossReferencesTemplate xrefs={xrefs} />);
		expect(container.firstChild).toBeNull();
	});

	it('should display tooltip when hovering over the cross references list', async () => {
		const xrefs = [
			{ displayName: 'xref ABC', resourceDescriptorPage: { name: 'page ABC' } },
			{ displayName: 'xref DEF', resourceDescriptorPage: { name: 'page DEF' } },
			{ displayName: 'xref GHI', resourceDescriptorPage: { name: 'page GHI' } },
		];

		const result = render(<CrossReferencesTemplate xrefs={xrefs} />);

		let listContentArray = result.getAllByText('xref ABC (page ABC)');
		expect(listContentArray).toHaveLength(1);

		fireEvent.mouseEnter(result.container.firstChild);

		//using find... here because it's async and the tooltip is dynamically added
		listContentArray = await result.findAllByText('xref ABC (page ABC)');

		expect(listContentArray).toHaveLength(2);
	});
});
