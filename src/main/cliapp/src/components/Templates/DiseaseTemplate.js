import { Tooltip } from "primereact/tooltip";
import { EllipsisTableCell } from "../EllipsisTableCell";

export const DiseaseTemplate = ({ object }) => {
  const targetClass = `a${global.crypto.randomUUID()}`; 
  if (object) {
    return (
      <>
        <EllipsisTableCell otherClasses={targetClass}>{object.name} ({object.curie})</EllipsisTableCell>
        <Tooltip target={`.${targetClass}`} content={`${object.name} (${object.curie})`} />
      </>
    );
  } else {
    return null;
  }
};