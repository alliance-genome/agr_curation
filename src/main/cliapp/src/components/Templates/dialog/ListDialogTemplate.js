import { Button } from "primereact/button";
import { ListTableCell } from "../../ListTableCell";

export const ListDialogTemplate = ({ entities, handleOpen, getTextField }) => {
  if (!entities) return null;
  const uniqueItemsSet = new Set(entities.map(entity => getTextField(entity)));
  const sortedItems = Array.from(uniqueItemsSet).sort();
  const listTemplate = (item) => (
    <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: item }} />
  );
  return (
    <>
      <Button className="p-button-text" onClick={() => { handleOpen(entities); }}>
        <ListTableCell template={listTemplate} listData={sortedItems} />
      </Button>
    </>
  );
};