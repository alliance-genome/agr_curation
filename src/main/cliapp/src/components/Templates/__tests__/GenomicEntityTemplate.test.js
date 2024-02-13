import { fireEvent, render } from '@testing-library/react';
import { GenomicEntityTemplate } from '../genomicEntity/GenomicEntityTemplate';
import '../../../tools/jest/setupTests';

describe('GenomicEntityTemplate', () => {

  it('should render genomicEntity text and curie when genomicEntity has geneSymbol', () => {
    const genomicEntity = {
      geneSymbol: {
        displayText: 'Gene Symbol'
      },
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    const content = result.getByText('Gene Symbol (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render genomicEntity text and curie when genomicEntity has alleleSymbol', () => {
    const genomicEntity = {
      alleleSymbol: {
        displayText: 'Allele Symbol'
      },
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    const content = result.getByText('Allele Symbol (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render genomicEntity text and curie when genomicEntity has geneFullName', () => {
    const genomicEntity = {
      geneFullName: {
        displayText: 'Gene Full Name'
      },
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    const content = result.getByText('Gene Full Name (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render genomicEntity text and curie when genomicEntity has alleleFullName', () => {
    const genomicEntity = {
      alleleFullName: {
        displayText: 'Allele Full Name'
      },
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    const content = result.getByText('Allele Full Name (CURIE)');
    expect(content).toBeInTheDocument();
  });
  it('should render genomicEntity name and curie when genomicEntity has name', () => {
    const genomicEntity = {
      name: 'genomicEntity Name',
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    const content = result.getByText('genomicEntity Name (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render genomicEntity curie in a div when genomicEntity has no displayable text', () => {
    const genomicEntity = {
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    const divContent = result.getByText('CURIE');
    expect(divContent).toBeInTheDocument();
  });

  it('should render null when genomicEntity is null', () => {
    const genomicEntity = null;

    const { container } = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    expect(container.firstChild).toBeNull();
  });

  it('should render genomicEntity text and curie in a tooltip when genomicEntity has geneSymbol', async () => {
    const genomicEntity = {
      geneSymbol: {
        displayText: 'Gene Symbol'
      },
      curie: 'CURIE'
    };

    const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    let divContentArray = result.getAllByText('Gene Symbol (CURIE)');
    expect(divContentArray).toHaveLength(1);

    fireEvent.mouseEnter(divContentArray[0]);
    //using find... here because it's async and the tooltip is dynamically added
    expect(await result.findAllByText('Gene Symbol (CURIE)')).toHaveLength(2);
  });

  it('should render <sup> tags in the HTML', async () => {
    const genomicEntity = {
      geneSymbol: {
        displayText: 'Gene <sup>Symbol</sup>'
      },
      curie: 'CURIE'
    };

    const { container } = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

    let superScript = container.querySelector('sup');
    expect(superScript).toBeInTheDocument();
  });
});
