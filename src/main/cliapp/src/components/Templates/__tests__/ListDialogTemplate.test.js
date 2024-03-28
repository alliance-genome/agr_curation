import { render, fireEvent } from '@testing-library/react';
import { ListDialogTemplate } from '../dialog/ListDialogTemplate';
import '../../../tools/jest/setupTests';

describe('ListDialogTemplate', () => {
  it('should render a list of strings with a button that can be clicked to open a dialog', () => {
    const entities = [{ id: 1, name: 'Item 1' }, { id: 2, name: 'Item 2' }, { id: 3, name: 'Item 3' }];
    const handleOpen = jest.fn();
    const getTextField = (entity) => entity.name;
    
    const result = render(<ListDialogTemplate entities={entities} handleOpen={handleOpen} getTextField={getTextField} />);
    
    expect(result.getByRole('button')).toBeInTheDocument();
    expect(result.getByText('Item 1')).toBeInTheDocument();
    expect(result.getByText('Item 2')).toBeInTheDocument();
    expect(result.getByText('Item 3')).toBeInTheDocument();
  });

  it('should return null when the entities prop is falsy', () => {
    const entities = null;
    const handleOpen = jest.fn();
    const getTextField = (entity) => entity.name;
    
    const { container } = render(<ListDialogTemplate entities={entities} handleOpen={handleOpen} getTextField={getTextField} />);
    
    expect(container.firstChild).toBeNull();
  });
  
  it('should return null when the handleOpen prop is falsy', () => {
    const entities = [{ id: 1, name: 'Item 1' }, { id: 2, name: 'Item 2' }, { id: 3, name: 'Item 3' }];
    const handleOpen = undefined;
    const getTextField = (entity) => entity.name;
    
    const { container } = render(<ListDialogTemplate entities={entities} handleOpen={handleOpen} getTextField={getTextField} />);
    
    expect(container.firstChild).toBeNull();
  });
  it('should return null when the getTextField prop is falsy', () => {
    const entities = [{ id: 1, name: 'Item 1' }, { id: 2, name: 'Item 2' }, { id: 3, name: 'Item 3' }];
    const handleOpen = jest.fn();

    const { container } = render(<ListDialogTemplate entities={entities} handleOpen={handleOpen}/>);
    
    expect(container.firstChild).toBeNull();
  });
  it('should return null when the entities prop is an empty array', () => {
    const entities = [];
    const handleOpen = undefined;
    const getTextField = (entity) => entity.name;

    const { container } = render(<ListDialogTemplate entities={entities} handleOpen={handleOpen} getTextField={getTextField} />);
    
    expect(container.firstChild).toBeNull();
  });
});