import { render } from '@testing-library/react';
import { NestedListDialogTemplate } from '../dialog/NestedListDialogTemplate';
import '../../../tools/jest/setupTests';

describe('NestedListDialogTemplate', () => {

  it('should return null when the entities prop is falsy', () => {
    const entities = null;
    const handleOpen = jest.fn();
    const getTextField = (entity) => entity.name;

    const { container } = render(<NestedListDialogTemplate
      entities={entities}
      handleOpen={handleOpen}
      getTextField={getTextField}
    />);

    expect(container.firstChild).toBeNull();
  });

  it('should return null when the handleOpen prop is falsy', () => {
    const entities = [
      { id: 1, name: 'Item 1', subType: [{ name: "name 1" }, { name: "name 2" }] },
      { id: 2, name: 'Item 2', subType: [{ name: "name 3" }, { name: "name 4" }] },
      { id: 3, name: 'Item 3', subType: [{ name: "name 5" }] }
    ];
    const handleOpen = undefined;
    const getTextField = (entity) => entity.name;

    const { container } = render(<NestedListDialogTemplate
      entities={entities}
      handleOpen={handleOpen}
      getTextField={getTextField}
    />);

    expect(container.firstChild).toBeNull();
  });
  it('should return null when the getTextField prop is falsy', () => {
    const entities = [
      { id: 1, name: 'Item 1', subType: [{ name: "name 1" }, { name: "name 2" }] },
      { id: 2, name: 'Item 2', subType: [{ name: "name 3" }, { name: "name 4" }] },
      { id: 3, name: 'Item 3', subType: [{ name: "name 5" }] }
    ];
    const handleOpen = jest.fn();

    const { container } = render(<NestedListDialogTemplate
      entities={entities}
      handleOpen={handleOpen}
    />);

    expect(container.firstChild).toBeNull();
  });
  it('should return null when the entities prop is an empty array', () => {
    const entities = [];
    const handleOpen = undefined;
    const getTextField = (entity) => entity.name;
    const subType = "subType";

    const { container } = render(<NestedListDialogTemplate
      entities={entities}
      subType={subType}
      handleOpen={handleOpen}
      getTextField={getTextField}
    />);

    expect(container.firstChild).toBeNull();
  });
  it('should return null when the subType prop is falsy', () => {
    const entities = [
      { id: 1, name: 'Item 1', subType: [{ name: "name 1" }, { name: "name 2" }] },
      { id: 2, name: 'Item 2', subType: [{ name: "name 3" }, { name: "name 4" }] },
      { id: 3, name: 'Item 3', subType: [{ name: "name 5" }] }
    ];

    const handleOpen = undefined;
    const getTextField = (entity) => entity.name;

    const { container } = render(<NestedListDialogTemplate
      entities={entities}
      handleOpen={handleOpen}
      getTextField={getTextField}
    />);

    expect(container.firstChild).toBeNull();
  });

  it('should render a list of strings from subtype array', () => {
    const entities = [
      { id: 1, name: 'Item 1', subType: [{ name: "name 1" }, { name: "name 2" }] },
      { id: 2, name: 'Item 2', subType: [{ name: "name 3" }, { name: "name 4" }] },
      { id: 3, name: 'Item 3', subType: [{ name: "name 5" }] }
    ];

    const handleOpen = jest.fn();
    const subType = 'subType';
    const getTextString = jest.fn((item) => item.name);

    const result = render(NestedListDialogTemplate({ entities, handleOpen, subType, getTextString }));

    expect(result.getByText('name 1')).toBeInTheDocument();
    expect(result.getByText('name 2')).toBeInTheDocument();
    expect(result.getByText('name 3')).toBeInTheDocument();
    expect(result.getByText('name 4')).toBeInTheDocument();
    expect(result.getByText('name 5')).toBeInTheDocument();
  });

});