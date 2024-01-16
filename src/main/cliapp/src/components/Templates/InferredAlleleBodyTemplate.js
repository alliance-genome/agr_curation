import { Tooltip } from "primereact/tooltip";

export const InferredAlleleBodyTemplate = ({ rowData }) => {
  if (rowData?.inferredAllele) {
    if (rowData.inferredAllele.alleleSymbol?.displayText) {
      return (
        <>
          <div className={`overflow-hidden text-overflow-ellipsis ia${rowData.id}${rowData.inferredAllele?.curie?.replace(':', '')}`}
            dangerouslySetInnerHTML={{
              __html: rowData.inferredAllele.alleleSymbol?.displayText + ' (' + rowData.inferredAllele.curie + ')'
            }}
          />
          <Tooltip target={`.ia${rowData.id}${rowData.inferredAllele.curie?.replace(':', '')}`}>
            <div dangerouslySetInnerHTML={{
              __html: rowData.inferredAllele.alleleSymbol?.displayText + ' (' + rowData.inferredAllele.curie + ')'
            }}
            />
          </Tooltip>
        </>
      );
    } else {
      return (
        <>
          <div className={`overflow-hidden text-overflow-ellipsis ia${rowData.id}${rowData.inferredAllele.curie?.replace(':', '')}`}
            dangerouslySetInnerHTML={{
              __html: rowData.inferredAllele.alleleFullName?.displayText + ' (' + rowData.inferredAllele.curie + ')'
            }}
          />
          <Tooltip target={`.ia${rowData.id}${rowData.inferredAllele.curie?.replace(':', '')}`}>
            <div dangerouslySetInnerHTML={{
              __html: rowData.inferredAllele.alleleFullName?.displayText + ' (' + rowData.inferredAllele.curie + ')'
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
