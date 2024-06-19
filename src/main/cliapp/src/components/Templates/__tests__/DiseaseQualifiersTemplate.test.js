import { render, within } from '@testing-library/react';
import { DiseaseQualifiersTemplate } from '../DiseaseQualifiersTemplate';
import '../../../tools/jest/setupTests';

describe('DiseaseQualifiersTemplate', () => {
	it('should return null when diseaseQualifiers is falsy', () => {
		const { container } = render(<DiseaseQualifiersTemplate diseaseQualifiers={null} />);
		expect(container.firstChild).toBeNull();
	});

	it('should sort diseaseQualifiers by name', () => {
		const diseaseQualifiers = [{ name: 'Qualifier C' }, { name: 'Qualifier A' }, { name: 'Qualifier B' }];

		const result = render(<DiseaseQualifiersTemplate diseaseQualifiers={diseaseQualifiers} />);

		const qualifiersList = result.getByRole('list');
		const listItems = within(qualifiersList).getAllByRole('listitem');

		expect(listItems[0]).toHaveTextContent('Qualifier A');
		expect(listItems[1]).toHaveTextContent('Qualifier B');
		expect(listItems[2]).toHaveTextContent('Qualifier C');
	});

	it('should return null when diseaseQualifiers is an empty array', () => {
		const { container } = render(<DiseaseQualifiersTemplate diseaseQualifiers={[]} />);
		expect(container.firstChild).toBeNull();
	});

	it('should render diseaseQualifiers without a name property', () => {
		const diseaseQualifiers = [{ id: 1 }, { id: 2 }, { id: 3 }];

		const result = render(<DiseaseQualifiersTemplate diseaseQualifiers={diseaseQualifiers} />);

		const qualifiersList = result.getByRole('list');
		const listItems = within(qualifiersList).getAllByRole('listitem');

		expect(listItems[0]).toHaveTextContent('');
		expect(listItems[1]).toHaveTextContent('');
		expect(listItems[2]).toHaveTextContent('');
	});
});
