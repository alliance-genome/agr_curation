import { Tooltip } from "primereact/tooltip";
import { EllipsisTableCell } from "../EllipsisTableCell";

export const DiseaseTemplate = ({ object }) => {
  if (!object) return null;

  const targetClass = `a${global.crypto.randomUUID()}`;

  return (
    <>
      <EllipsisTableCell otherClasses={targetClass}>{object.name} ({object.curie})</EllipsisTableCell>
      <Tooltip target={`.${targetClass}`} content={`${object.name} (${object.curie})`} />
    </>
  );
};