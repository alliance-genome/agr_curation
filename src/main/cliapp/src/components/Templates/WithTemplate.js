import { EllipsisTableCell } from "../EllipsisTableCell";
import { ListTableCell } from "../ListTableCell";

export const WithTemplate = ({ rowData }) => {
  if (rowData?.with) {
    const sortedWithGenes = rowData.with.sort((a, b) => (a.geneSymbol.displayText > b.geneSymbol.displayText) ? 1 : (a.curie === b.curie) ? 1 : -1);
    const listTemplate = (item) => {
      return (
        <EllipsisTableCell>
          {item.geneSymbol.displayText + ' (' + item.curie + ')'}
        </EllipsisTableCell>
      );
    };
    return (
      <div className="-my-4 p-1">
        <ListTableCell template={listTemplate} listData={sortedWithGenes} />
      </div>
    );
  } else {
    return null;
  }
};