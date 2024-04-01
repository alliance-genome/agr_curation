import { render } from '@testing-library/react';
import { CommaSeparatedArrayTemplate } from '../CommaSeparatedArrayTemplate';
import '../../../tools/jest/setupTests';

describe('CommaSeparatedArrayTemplate', () => {

    it('should return null when array is empty', () => {
        const emptyArray = [];
        const { container } = render(<CommaSeparatedArrayTemplate array={emptyArray} />);
        expect(container.firstChild).toBeNull();
    });
  
    it('should render the resultant comma separated array when passed an array of multiple elements', () => {
        const testArray = ["Element1", "Element2", "Element3"];
        const { container } = render(<CommaSeparatedArrayTemplate array={testArray} />);
        expect(container.textContent).toBe('Element1, Element2, Element3');
     });
  
     it('should return null when an array is not passed', () => {
        const notArray = 'not an array';
        const { container } = render(<CommaSeparatedArrayTemplate array={notArray} />);
        expect(container.firstChild).toBeNull();
    });

    it('should return single element when array has one element', () => {
        const singleElementArray = ['Element1'];
        const { container } = render(<CommaSeparatedArrayTemplate array={singleElementArray} />);
        expect(container.textContent).toBe('Element1');
    });
  });