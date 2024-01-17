import { Tooltip } from "primereact/tooltip";

export const AssertedAlleleBodyTemplate = ({ rowData }) => {
  if (rowData?.assertedAllele) {
    if (rowData.assertedAllele?.alleleSymbol) {
      return (
        <>
          <div className={`overflow-hidden text-overflow-ellipsis aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}
            dangerouslySetInnerHTML={{
              __html: rowData.assertedAllele.alleleSymbol.displayText + ' (' + rowData.assertedAllele.curie + ')'
            }}
          />
          <Tooltip target={`.aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}>
            <div dangerouslySetInnerHTML={{
              __html: rowData.assertedAllele.alleleSymbol.displayText + ' (' + rowData.assertedAllele.curie + ')'
            }}
            />
          </Tooltip>
        </>
      );
    } else {
      return (
        <>
          <div className={`overflow-hidden text-overflow-ellipsis aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}
            dangerouslySetInnerHTML={{
              __html: rowData.assertedAllele.alleleFullName.displayText + ' (' + rowData.assertedAllele.curie + ')'
            }}
          />
          <Tooltip target={`.aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}>
            <div dangerouslySetInnerHTML={{
              __html: rowData.assertedAllele.alleleFullName.displayText + ' (' + rowData.assertedAllele.curie + ')'
            }}
            />
          </Tooltip>
        </>
      );
    }
  } else {
    return null;
  }
};