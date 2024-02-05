import { fireEvent, render } from '@testing-library/react';
import { SubjectTemplate } from '../SubjectTemplate';
import '../../../../tools/jest/setupTests';

describe('SubjectTemplate', () => {

  it('should render subject text and curie when subject has geneSymbol', () => {
    const subject = {
      geneSymbol: {
        displayText: 'Gene Symbol'
      },
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    const content = result.getByText('Gene Symbol (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render subject text and curie when subject has alleleSymbol', () => {
    const subject = {
      alleleSymbol: {
        displayText: 'Allele Symbol'
      },
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    const content = result.getByText('Allele Symbol (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render subject text and curie when subject has geneFullName', () => {
    const subject = {
      geneFullName: {
        displayText: 'Gene Full Name'
      },
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    const content = result.getByText('Gene Full Name (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render subject text and curie when subject has alleleFullName', () => {
    const subject = {
      alleleFullName: {
        displayText: 'Allele Full Name'
      },
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    const content = result.getByText('Allele Full Name (CURIE)');
    expect(content).toBeInTheDocument();
  });
  it('should render subject name and curie when subject has name', () => {
    const subject = {
      name: 'Subject Name',
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    const content = result.getByText('Subject Name (CURIE)');
    expect(content).toBeInTheDocument();
  });

  it('should render subject curie in a div when subject has no displayable text', () => {
    const subject = {
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    const divContent = result.getByText('CURIE');
    expect(divContent).toBeInTheDocument();
  });

  it('should render null when subject is null', () => {
    const subject = null;

    const { container } = render(<SubjectTemplate subject={subject} />);

    expect(container.firstChild).toBeNull();
  });

  it('should render subject text and curie in a tooltip when subject has geneSymbol', async () => {
    const subject = {
      geneSymbol: {
        displayText: 'Gene Symbol'
      },
      curie: 'CURIE'
    };

    const result = render(<SubjectTemplate subject={subject} />);

    let divContentArray = result.getAllByText('Gene Symbol (CURIE)');
    expect(divContentArray).toHaveLength(1);

    fireEvent.mouseEnter(divContentArray[0]);
    //using find... here because it's async and the tooltip is dynamically added
    expect(await result.findAllByText('Gene Symbol (CURIE)')).toHaveLength(2);
  });

  it('should render <sup> tags in the HTML', async () => {
    const subject = {
      geneSymbol: {
        displayText: 'Gene <sup>Symbol</sup>'
      },
      curie: 'CURIE'
    };

    const { container } = render(<SubjectTemplate subject={subject} />);

    let superScript = container.querySelector('sup');
    expect(superScript).toBeInTheDocument();
  });
});
