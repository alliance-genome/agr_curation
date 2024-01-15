import { EllipsisTableCell } from "../EllipsisTableCell";
import { ListTableCell } from "../ListTableCell";
import { Tooltip } from "primereact/tooltip";

export const AssertedGenesBodyTemplate = ({ rowData }) => {
  if (rowData?.assertedGenes?.length > 0) {
    const sortedAssertedGenes = rowData.assertedGenes.sort((a, b) => (a.geneSymbol?.displayText > b.geneSymbol?.displayText) ? 1 : (a.curie === b.curie) ? 1 : -1);
    const listTemplate = (item) => {
      return (
        <EllipsisTableCell>
          {`${item.geneSymbol?.displayText} (${item.curie})`}
        </EllipsisTableCell>
      );
    };

    return (
      <>
        <div className={`-my-4 p-1 a${rowData.id}${rowData.assertedGenes[0]?.curie.replace(':', '')}`}>
          <ListTableCell template={listTemplate} listData={sortedAssertedGenes} />
        </div>
        <Tooltip target={`.a${rowData.id}${rowData.assertedGenes[0]?.curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
          <ListTableCell template={listTemplate} listData={sortedAssertedGenes} />
        </Tooltip>
      </>
    );
  } else {
    return null;
  }
};