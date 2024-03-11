import { render, fireEvent } from '@testing-library/react';
import { TaxonTemplate } from '../TaxonTemplate';
import '../../../tools/jest/setupTests';

describe('TaxonTemplate', () => {

    it('should return null when object is null/falsy', () => {
        const result = render(<TaxonTemplate taxon={null} />);
        expect(result.container.firstChild).toBeNull();
    });

    it('should render an empty EllipsisTableCell component if the object has no name or curie', () => {
        const object = {};

        const result = render(<TaxonTemplate taxon={object} />);

        const ellipsisTableCell = result.container.firstChild;
        expect(ellipsisTableCell).toHaveTextContent('');
    });

    it('should render an EllipsisTableCell component with only the name if the object has no curie', () => {
        const object = {
            name: 'Taxon Name'
        };

        const result = render(<TaxonTemplate taxon={object} />);

        const ellipsisTableCell = result.getByText('Taxon Name');
        expect(ellipsisTableCell).toBeInTheDocument();
    });

    it('should render an EllipsisTableCell component with only the curie if the object has no name', () => {
        const object = {
            curie: 'Taxon Curie'
        };

        const result = render(<TaxonTemplate taxon={object} />);

        const ellipsisTableCell = result.getByText('Taxon Curie');
        expect(ellipsisTableCell).toBeInTheDocument();
    });


    it('should render the name and curie of the Taxon object in an EllipsisTableCell component', () => {
        const object = {
            name: 'Taxon Name',
            curie: 'Taxon Curie'
        };

        const result = render(<TaxonTemplate taxon={object} />);

        const content = result.getByText('Taxon Name (Taxon Curie)');
        expect(content).toBeInTheDocument();
    });

    it('should display a tooltip with the name and curie of the Taxon object when hovering over the EllipsisTableCell component', async () => {
        const object = {
            name: 'Taxon Name',
            curie: 'Taxon Curie'
        };

        const result = render(<TaxonTemplate taxon={object} />);

        const divContentArray = result.getAllByText('Taxon Name (Taxon Curie)');
        expect(divContentArray).toHaveLength(1);
        fireEvent.mouseEnter(divContentArray[0]);

        expect(await result.findAllByText('Taxon Name (Taxon Curie)')).toHaveLength(2);
    });

});