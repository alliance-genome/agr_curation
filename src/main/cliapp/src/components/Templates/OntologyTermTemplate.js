import { Tooltip } from "primereact/tooltip";
import { EllipsisTableCell } from "../EllipsisTableCell";

export const OntologyTermTemplate = ({ object }) => {
  if (!object) return null;

  const targetClass = `a${global.crypto.randomUUID()}`;
  const textString = getTextString(object);

  return (
    <>
      <EllipsisTableCell otherClasses={targetClass}>{textString}</EllipsisTableCell>
      <Tooltip target={`.${targetClass}`} content={textString} />
    </>
  );
};

const getTextString = (object) => {
  if(!object.name) return object.curie;
  if(!object.curie) return object.name;
  if(!object.curie && !object.name) return "";
  return `${object.name} (${object.curie})`; 
}