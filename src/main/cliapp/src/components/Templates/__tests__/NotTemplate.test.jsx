import { render } from '@testing-library/react';
import { NotTemplate } from '../NotTemplate';
import '../../../tools/jest/setupTests';

describe('NotTemplate', () => {
	it('should return null when value is null', () => {
		const { container } = render(<NotTemplate value={null} />);
		expect(container.firstChild).toBeNull();
	});

	it('should return a component with the text "NOT" when the value is true', () => {
		const { getByText } = render(<NotTemplate value={true} />);
		expect(getByText('NOT')).toBeInTheDocument();
	});
	it('should return a component with an empty string when the value is false', () => {
		const { container } = render(<NotTemplate value={false} />);
		expect(container.textContent).toBe('');
	});

	it('should return null when value is not a boolean', () => {
		const { container } = render(<NotTemplate value={123} />);
		expect(container.firstChild).toBeNull();
	});
});
