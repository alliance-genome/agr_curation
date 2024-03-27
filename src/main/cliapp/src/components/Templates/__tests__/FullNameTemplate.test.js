import { render, fireEvent } from '@testing-library/react';
import { FullNameTemplate } from '../dialog/FullNameTemplate';
import '../../../tools/jest/setupTests';

describe('FullNameTemplate', () => {
  it('should return null when alleleFullName is falsy', () => {
    const alleleFullName = null;
    const handleOpen = jest.fn();

    const { container } = render(<FullNameTemplate alleleFullName={alleleFullName} handleOpen={handleOpen} />);

    expect(container.firstChild).toBeNull();
  });

  it('should return null when handleOpen is falsy', () => {
    const alleleFullName = { displayText: "Full Name" };
    const handleOpen = null;

    const { container } = render(<FullNameTemplate alleleFullName={alleleFullName} handleOpen={handleOpen} />);

    expect(container.firstChild).toBeNull();
  });

  it('should call handleOpen function with correct parameter when Button is clicked', () => {
    const alleleFullName = { displayText: "Full Name" };
    const handleOpen = jest.fn();

    const result = render(<FullNameTemplate alleleFullName={alleleFullName} handleOpen={handleOpen} />);
    fireEvent.click(result.getByRole('button'));

    expect(handleOpen).toHaveBeenCalledWith(alleleFullName);
  });
});