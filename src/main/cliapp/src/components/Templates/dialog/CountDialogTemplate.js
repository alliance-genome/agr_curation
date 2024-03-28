import { Button } from "primereact/button";

export const CountDialogTemplate = ({ entities, handleOpen, text }) => {
  if (!entities || !handleOpen || !text) return null;

  return (
    <Button className="p-button-text" onClick={() => handleOpen(entities)} >
      <span className="-my-4 p-1 underline">
        {`${text} (${entities.length})`}
      </span>
    </Button>
  );

};