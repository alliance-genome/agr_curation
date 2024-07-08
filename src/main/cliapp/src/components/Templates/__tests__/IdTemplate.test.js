import { render, fireEvent } from '@testing-library/react';
import { IdTemplate } from '../IdTemplate';
import '../../../tools/jest/setupTests';

describe('IdTemplate', () => {
	it('should return null when id is falsy', () => {
		const { container } = render(<IdTemplate id={null} />);
		expect(container.firstChild).toBeNull();
	});

	it('should render Tooltip component with EllipsisTableCell as target and id as content', async () => {
		const result = render(<IdTemplate id={123} />);
		const divArray = result.getAllByText('123');
		expect(divArray).toHaveLength(1);
		fireEvent.mouseEnter(divArray[0]);
		expect(await result.findAllByText('123')).toHaveLength(2);
	});

	it('should handle id as number', () => {
		const { getByText } = render(<IdTemplate id={123} />);
		expect(getByText('123')).toBeInTheDocument();
	});

	it('should handle id as string', () => {
		const { getByText } = render(<IdTemplate id="abc" />);
		expect(getByText('abc')).toBeInTheDocument();
	});
});
