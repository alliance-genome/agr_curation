import { render, fireEvent, within } from '@testing-library/react';
import { NotEditor } from '../NotEditor';
import '../../../tools/jest/setupTests';

describe('NotEditor', () => {
  it('should display "NOT" as the placeholder text when the initial value is true', () => {
    const props = {};
    const value = true;
    const editorChange = jest.fn();

    const result = render(<NotEditor props={props} value={value} editorChange={editorChange} />);

    expect(result.getAllByText("NOT")).toHaveLength(2);
  });

  it('should render a Dropdown component with no options when value prop is undefined', () => {
    const props = {};
    const value = undefined;
    const editorChange = jest.fn();

    const result = render(<NotEditor props={props} value={value} editorChange={editorChange} />);


    expect(result.getByText("empty")).toBeInTheDocument();
  });
  
  it('should update the selected value when an option is selected', () => {
    const props = {};
    const value = false;
    const editorChange = jest.fn();


    const result = render(<NotEditor props={props} value={value} editorChange={editorChange} />);
    const span = result.container.getElementsByTagName('span')[0];
    
    expect(within(span).getByText('empty')).toBeInTheDocument();
    
    fireEvent.click(span);
    
    const option = result.getAllByText('NOT');
    fireEvent.click(option[0]);
    const updatedSpan = result.container.getElementsByTagName('span')[0];
    

    expect(within(updatedSpan).getByText('NOT')).toBeInTheDocument();
  });

  it('should call editorChange when an option is selected', () => {
    const props = {};
    const value = false;
    const editorChange = jest.fn();
    const result = render(<NotEditor props={props} value={value} editorChange={editorChange} />);
    const span = result.container.getElementsByTagName('span')[0];
    
    fireEvent.click(span);
    
    
    const option = result.getAllByText('NOT');
    fireEvent.click(option[0]);
    
    expect(editorChange).toHaveBeenCalledTimes(1);
  });

});