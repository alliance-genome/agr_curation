import { render } from '@testing-library/react';
import { CountDialogTemplate } from '../dialog/CountDialogTemplate';
import '../../../tools/jest/setupTests';

describe('CountDialogTemplate', () => {

  it('should return a non-null JSX element with the correct text and length of entities', () => {
    const entities = [1, 2, 3];
    const handleOpen = jest.fn();
    const text = 'Count';

    const result = render(<CountDialogTemplate entities={entities} handleOpen={handleOpen} text={text} />);

    const element = result.getByText('Count (3)');

    expect(result).not.toBeNull();
    expect(element).toBeInTheDocument();
  });

  it('should return null when entities prop is undefined', () => {
    const entities = undefined;
    const handleOpen = jest.fn();
    const text = 'Count';

    const { container } = render(<CountDialogTemplate entities={entities} handleOpen={handleOpen} text={text} />);

    expect(container.firstChild).toBeNull();
  });

  it('should return null when handleOpen prop is undefined', () => {
    const entities = [1, 2, 3];
    const handleOpen = undefined;
    const text = 'Count';

    const { container } = render(<CountDialogTemplate entities={entities} handleOpen={handleOpen} text={text} />);

    expect(container.firstChild).toBeNull();
  });

  it('should return null when text prop is undefined', () => {
    const entities = [1, 2, 3];
    const handleOpen = jest.fn();
    const text = undefined;

    const { container } = render(<CountDialogTemplate entities={entities} handleOpen={handleOpen} text={text} />);

    expect(container.firstChild).toBeNull();
  });

  it('should return null when entities prop is an empty array', () => {
    const entities = [];
    const handleOpen = jest.fn();
    const text = 'Count';

    const { container } = render(<CountDialogTemplate entities={entities} handleOpen={handleOpen} text={text} />);

    expect(container.firstChild).toBeNull();
  });
});