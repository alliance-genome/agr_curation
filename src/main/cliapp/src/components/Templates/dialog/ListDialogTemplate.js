import { Button } from "primereact/button";
import { ListTableCell } from "../../ListTableCell";

export const ListDialogTemplate = ({ entities, handleOpen, getTextField, underline = true }) => {
  if (!entities || entities.length === 0 || !handleOpen || !getTextField) return null;

  const uniqueItemsSet = new Set(entities.map(entity => getTextField(entity)));
  const sortedItems = Array.from(uniqueItemsSet).sort();

  const listTemplate = (item) => (
    <div
      className={`overflow-hidden text-overflow-ellipsis ${underline ? "underline" : ""}`}
      dangerouslySetInnerHTML={{ __html: item }}
    />
  );

  return (
    <>
      <div className={`-my-4 p-1`}>
        <Button className="p-button-text" onClick={() => handleOpen(entities)}>
          <ListTableCell template={listTemplate} listData={sortedItems} />
        </Button>
      </div>
    </>
  );
};