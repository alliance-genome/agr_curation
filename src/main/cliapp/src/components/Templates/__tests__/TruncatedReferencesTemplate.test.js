import { render } from '@testing-library/react';
import { TruncatedReferencesTemplate } from '../reference/TruncatedReferencesTemplate';
import '../../../tools/jest/setupTests';
import { BrowserRouter } from 'react-router-dom/cjs/react-router-dom.min';

describe("TruncatedReferencesTemplate", () => {

  it('should render a list of references when references prop is not null and has length greater than 0', () => {
    const references = [
      {
        curie: "MainCurie:111",
        crossReferences: [
          { referencedCurie: "PMID:000001" },
          { referencedCurie: "FB:FBgn0000001" },
          { referencedCurie: "MGI:MGI:0000001" },
          { referencedCurie: "RGD:2000001" },
          { referencedCurie: "SGD:S000001" },
          { referencedCurie: "WB:WBGene00000001" },
          { referencedCurie: "ZFIN:ZDB-GENE-000000-1" },
          { referencedCurie: "ExtraCurie:111" }
        ]
      },
      {
        curie: "MainCurie:222",
        crossReferences: [
          { referencedCurie: "PMID:000002" },
          { referencedCurie: "FB:FBgn0000002" },
          { referencedCurie: "MGI:MGI:0000002" },
          { referencedCurie: "RGD:2000002" },
          { referencedCurie: "SGD:S000002" },
          { referencedCurie: "WB:WBGene00000002" },
          { referencedCurie: "ZFIN:ZDB-GENE-000000-2" },
          { referencedCurie: "ExtraCurie:222" }
        ]
      }
    ];

    const identifier = '123';

    const result = render(<TruncatedReferencesTemplate references={references} identifier={identifier} detailPage="Name"/>);

    const reference1 = result.getByText('PMID:000001 (FB:FBgn0000001|MGI:MGI:0000001|RGD:2000001|SGD:S000001|WB:WBGene00000001|ZFIN:ZDB-GENE-000000-1|ExtraCurie:111|MainCurie:111)');
    const reference2 = result.getByText('PMID:000002 (FB:FBgn0000002|MGI:MGI:0000002|RGD:2000002|SGD:S000002|WB:WBGene00000002|ZFIN:ZDB-GENE-000000-2|ExtraCurie:222|MainCurie:222)');
    expect(reference1).toBeInTheDocument();
    expect(reference2).toBeInTheDocument();
  });

  it('should not render anything when references prop is null', () => {
    const references = null;
    const identifier = '123';

    const result = render(<TruncatedReferencesTemplate references={references} identifier={identifier} detailPage="Name"/>);

    expect(result.container.firstChild).toBeNull();
  });

  it('should render a truncated list of references when references prop has length greater than 5', () => {
    const references = [
      {
        curie: "MainCurie:111",
        crossReferences: [
          { referencedCurie: "PMID:000001" },
          { referencedCurie: "FB:FBgn0000001" },
        ]
      },
      {
        curie: "MainCurie:222",
        crossReferences: [
          { referencedCurie: "PMID:000002" },
          { referencedCurie: "MGI:MGI:0000002" },
        ]
      },
      {
        curie: "MainCurie:333",
        crossReferences: [
          { referencedCurie: "PMID:000003" },
          { referencedCurie: "RGD:2000003" },
        ]
      },
      {
        curie: "MainCurie:444",
        crossReferences: [
          { referencedCurie: "PMID:000004" },
          { referencedCurie: "SGD:S000004" },
        ]
      },
      {
        curie: "MainCurie:555",
        crossReferences: [
          { referencedCurie: "PMID:000005" },
          { referencedCurie: "WB:WBGene00000005" },
        ]
      },
      {
        curie: "MainCurie:666",
        crossReferences: [
          { referencedCurie: "PMID:000006" },
          { referencedCurie: "ZFIN:ZDB-GENE-000000-6" },
        ]
      }
    ];

    const identifier = '123';

    const result = render(
      <BrowserRouter>
        <TruncatedReferencesTemplate references={references} identifier={identifier} detailPage="Name"/>
      </BrowserRouter>
    );
    //using queryBy here because getBy will return an error if not found and the point is to test that reference6 isn't shown
    const reference1 = result.queryByText('PMID:000001 (FB:FBgn0000001|MainCurie:111)');
    const reference2 = result.queryByText('PMID:000002 (MGI:MGI:0000002|MainCurie:222)');
    const reference3 = result.queryByText('PMID:000003 (RGD:2000003|MainCurie:333)');
    const reference4 = result.queryByText('PMID:000004 (SGD:S000004|MainCurie:444)');
    const reference5 = result.queryByText('PMID:000005 (WB:WBGene00000005|MainCurie:555)');
    const reference6 = result.queryByText('PMID:000006 (ZFIN:ZDB-GENE-000000-6|MainCurie:666)');
    expect(reference1).toBeInTheDocument();
    expect(reference2).toBeInTheDocument();
    expect(reference3).toBeInTheDocument();
    expect(reference4).toBeInTheDocument();
    expect(reference5).toBeInTheDocument();
    expect(reference6).not.toBeInTheDocument();
  });

  it('should display detail message link when references prop has length greater than 5', () => {
    const references = [
      {
        curie: "MainCurie:111",
        crossReferences: [
          { referencedCurie: "PMID:000001" },
          { referencedCurie: "FB:FBgn0000001" },
        ]
      },
      {
        curie: "MainCurie:222",
        crossReferences: [
          { referencedCurie: "PMID:000002" },
          { referencedCurie: "MGI:MGI:0000002" },
        ]
      },
      {
        curie: "MainCurie:333",
        crossReferences: [
          { referencedCurie: "PMID:000003" },
          { referencedCurie: "RGD:2000003" },
        ]
      },
      {
        curie: "MainCurie:444",
        crossReferences: [
          { referencedCurie: "PMID:000004" },
          { referencedCurie: "SGD:S000004" },
        ]
      },
      {
        curie: "MainCurie:555",
        crossReferences: [
          { referencedCurie: "PMID:000005" },
          { referencedCurie: "WB:WBGene00000005" },
        ]
      },
      {
        curie: "MainCurie:666",
        crossReferences: [
          { referencedCurie: "PMID:000006" },
          { referencedCurie: "ZFIN:ZDB-GENE-000000-6" },
        ]
      }
    ];

    const identifier = '123';

    const result = render(
      <BrowserRouter>
        <TruncatedReferencesTemplate references={references} identifier={identifier} detailPage="Name"/>
      </BrowserRouter>
    );

    let detailMessage = result.getByRole('alert', { name: 'detailMessage' });

    expect(detailMessage).toBeInTheDocument();
  });

});

