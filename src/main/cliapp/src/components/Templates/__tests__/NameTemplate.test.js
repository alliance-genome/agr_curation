import { render } from '@testing-library/react';
import { NameTemplate } from '../NameTemplate';
import '../../../tools/jest/setupTests';

describe('NameTemplate', () => {
	it('should return null when value is null', () => {
		const { container } = render(<NameTemplate name={null} />);
		expect(container.firstChild).toBeNull();
	});

	it('should render the name inside the div', () => {
		const result = render(<NameTemplate name="test" />);
		expect(result.getByText('test')).toBeInTheDocument();
	});
});
