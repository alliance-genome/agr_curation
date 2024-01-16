import { Tooltip } from "primereact/tooltip";
export const InferredGeneBodyTemplate = ({ rowData }) => {
  if (rowData?.inferredGene) {
    return (
      <>
        <div className={`overflow-hidden text-overflow-ellipsis ig${rowData.id}${rowData.inferredGene.curie.replace(':', '')}`}
          dangerouslySetInnerHTML={{
            __html: rowData.inferredGene.geneSymbol.displayText + ' (' + rowData.inferredGene.curie + ')'
          }}
        />
        <Tooltip target={`.ig${rowData.id}${rowData.inferredGene.curie.replace(':', '')}`}>
          <div dangerouslySetInnerHTML={{
            __html: rowData.inferredGene.geneSymbol.displayText + ' (' + rowData.inferredGene.curie + ')'
          }}
          />
        </Tooltip>
      </>
    );
  } else {
    return null;
  }
};