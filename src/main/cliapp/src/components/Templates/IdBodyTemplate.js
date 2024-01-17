import { EllipsisTableCell } from "../EllipsisTableCell";
import { Tooltip } from "primereact/tooltip";

export const IdBodyTemplate = ({ id }) => {
  //the 'a' at the start is a hack since css selectors can't start with a number
  const targetClass =  `a${global.crypto.randomUUID()}`; 
  return (
    <>
      <EllipsisTableCell otherClasses={targetClass}>
        {id}
      </EllipsisTableCell>
      <Tooltip target={`.${targetClass}`} content={id} />
    </>
  );
};