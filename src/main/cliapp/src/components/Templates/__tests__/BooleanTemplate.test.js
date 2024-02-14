import { render } from '@testing-library/react';
import { BooleanTemplate } from '../BooleanTemplate';
import '../../../tools/jest/setupTests';

describe('BooleanTemplate', () => {

  it('should return null when value is null', () => {
    const { container } = render(<BooleanTemplate value={null} />);
    expect(container.firstChild).toBeNull();
  });

  it('should render a div with correct classes', () => {
    const { container } = render(<BooleanTemplate value={true} />);
    expect(container.firstChild).toHaveClass('overflow-hidden text-overflow-ellipsis');
  });

  it('should render the JSON stringified value inside the div', () => {
    const { container } = render(<BooleanTemplate value={false} />);
    //todo: change to rtl query
    expect(container.firstChild).toHaveTextContent('false');
  });

  it('should return null when value is not a boolean', () => {
    const { container } = render(<BooleanTemplate value={123} />);
    expect(container.firstChild).toBeNull();
  });
});