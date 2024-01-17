import { EllipsisTableCell } from "../EllipsisTableCell";
import { Tooltip } from "primereact/tooltip";

export const UniqueIdBodyTemplate = ({ rowData }) => {
  return (
    //the 'c' at the start is a hack since css selectors can't start with a number
    <>
      <EllipsisTableCell otherClasses={`c${rowData.id}`}>
        {rowData.uniqueId}
      </EllipsisTableCell>
      <Tooltip target={`.c${rowData.id}`} content={rowData.uniqueId} />
    </>
  );
};