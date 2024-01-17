import { EllipsisTableCell } from "../EllipsisTableCell";

export const NegatedTemplate = ({ rowData }) => {
  if (rowData?.negated !== null || rowData?.negated !== undefined) {
    return <EllipsisTableCell>{JSON.stringify(rowData.negated)}</EllipsisTableCell>;
  } else { 
    return null;
  }
};