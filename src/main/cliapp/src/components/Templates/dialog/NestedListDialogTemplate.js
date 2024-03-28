import { Button } from "primereact/button";
import { ListTableCell } from "../../ListTableCell";

export const NestedListDialogTemplate = ({ entities, subTypes, handleOpen, getTextString }) => {
  if (!entities || entities.length === 0 || !handleOpen || !subTypes || !getTextString) return null;
  
  const strings = entities.flatMap((entity) => entity[subTypes]?.map(item => getTextString(item)) || []);
  const uniqueStrings = new Set(strings);
  const sortedStrings = Array.from(uniqueStrings).sort();

  const listTemplate = (item) => {
    return (
      <span style={{ textDecoration: 'underline' }}>
        {item && item}
      </span>
    );
  };
  
  return (
    <>
      <Button className="p-button-text" onClick={() => handleOpen(entities)} >
        <ListTableCell template={listTemplate} listData={sortedStrings} />
      </Button >
    </>
  );
};