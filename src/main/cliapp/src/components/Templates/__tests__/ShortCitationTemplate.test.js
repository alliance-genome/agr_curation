import { render } from '@testing-library/react';
import { ShortCitationTemplate } from '../reference/ShortCitationTemplate';
import '../../../tools/jest/setupTests';

describe("ShortCitationTemplate", () => {
  it('should should display the short citation of a reference with the field "short_citation"', () => {
    const reference = {
      short_citation: 'Short Citation'
    };
    
    const result = render(<ShortCitationTemplate reference={reference} />);
    
    const divElement = result.getByText('Short Citation');
    expect(divElement).toBeInTheDocument();
  });
  it('should should display the short citation of a reference with the field "shortCitation"', () => {
    const reference = {
      shortCitation: 'Short Citation'
    };
  
    const result = render(<ShortCitationTemplate reference={reference} />);
  
    const divElement = result.getByText('Short Citation');
    expect(divElement).toBeInTheDocument();
  });
  it('should return an empty div when reference is provided but it does not have a short_citation or shortCitation property', () => {
    const reference = {};

    const { container } = render(<ShortCitationTemplate reference={reference} />);

    const div = container.getElementsByTagName('div')[0];

    expect(div).toBeInTheDocument();
    expect(div.firstChild).toBeNull();
  });
  
  it('should return null when reference is not provided', () => {
    const { container } = render(<ShortCitationTemplate reference={null} />);
    expect(container.firstChild).toBeNull();
  });

});

