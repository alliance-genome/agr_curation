import { EllipsisTableCell } from "../EllipsisTableCell";
import { ListTableCell } from "../ListTableCell";
import { Tooltip } from "primereact/tooltip";

export const EvidenceTemplate = ({ rowData }) => {
  if (rowData?.evidenceCodes && rowData.evidenceCodes.length > 0) {
    const sortedEvidenceCodes = rowData.evidenceCodes.sort((a, b) => (a.abbreviation > b.abbreviation) ? 1 : (a.curie === b.curie) ? 1 : -1);
    const listTemplate = (item) => {
      return (
        <EllipsisTableCell>
          {item.abbreviation + ' - ' + item.name + ' (' + item.curie + ')'}
        </EllipsisTableCell>
      );
    };
    return (
      <>
        <div className={`-my-4 p-1 a${rowData.id}${rowData.evidenceCodes[0].curie.replace(':', '')}`}>
          <ListTableCell template={listTemplate} listData={sortedEvidenceCodes} />
        </div>
        <Tooltip target={`.a${rowData.id}${rowData.evidenceCodes[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
          <ListTableCell template={listTemplate} listData={sortedEvidenceCodes} />
        </Tooltip>
      </>
    );
  } else {
    return null;
  }
};