import { Button } from "primereact/button";

export const DeleteAction = ({ disabled, deletionHandler }) => {
  return (
    <Button icon="pi pi-trash" className="p-button-text" disabled={disabled}
      onClick={deletionHandler} />
  );
};