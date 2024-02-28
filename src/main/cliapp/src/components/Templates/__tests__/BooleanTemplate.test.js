import { render } from '@testing-library/react';
import { BooleanTemplate } from '../BooleanTemplate';
import '../../../tools/jest/setupTests';

describe('BooleanTemplate', () => {

  it('should return null when value is null', () => {
    const { container } = render(<BooleanTemplate value={null} />);
    expect(container.firstChild).toBeNull();
  });

  it('should render the JSON stringified value inside the div', () => {
    const result = render(<BooleanTemplate value={false} />);
    expect(result.getByText("false")).toBeInTheDocument();
  });

  it('should return null when value is not a boolean', () => {
    const { container } = render(<BooleanTemplate value={123} />);
    expect(container.firstChild).toBeNull();
  });
});