import { render } from '@testing-library/react';
import { CrossReferenceTemplate } from '../reference/CrossReferenceTemplate';
import '../../../tools/jest/setupTests';
import 'core-js/features/structured-clone';

describe('CrossReferenceTemplate', () => {
	it('should render a sorted list of cross references by curie field', () => {
		const reference = {
			crossReferences: [{ referencedCurie: 'def' }, { referencedCurie: 'abc' }, { referencedCurie: 'ghi' }],
		};

		const result = render(<CrossReferenceTemplate reference={reference} />);

		const listItems = result.getAllByRole('listitem');
		expect(listItems).toHaveLength(3);
		expect(listItems[0]).toHaveTextContent('abc');
		expect(listItems[1]).toHaveTextContent('def');
		expect(listItems[2]).toHaveTextContent('ghi');
	});

	it('should handle references with cross_references field', () => {
		const reference = {
			cross_references: [{ curie: 'abc' }, { curie: 'def' }, { curie: 'ghi' }],
		};

		const result = render(<CrossReferenceTemplate reference={reference} />);

		const listItems = result.getAllByRole('listitem');
		expect(listItems).toHaveLength(3);
		expect(listItems[0]).toHaveTextContent('abc');
		expect(listItems[1]).toHaveTextContent('def');
		expect(listItems[2]).toHaveTextContent('ghi');
	});

	it('should display an empty list when cross references are null or empty', () => {
		const reference = {
			cross_references: null,
		};

		const result = render(<CrossReferenceTemplate reference={reference} />);

		const listItems = result.queryAllByRole('listitem');
		expect(listItems).toHaveLength(0);
	});

	it('should handle cross references with undefined curie fields', () => {
		const reference = {
			cross_references: [{}, {}, {}],
		};

		const result = render(<CrossReferenceTemplate reference={reference} />);

		const listItems = result.getAllByRole('listitem');
		expect(listItems).toHaveLength(3);
	});
});
