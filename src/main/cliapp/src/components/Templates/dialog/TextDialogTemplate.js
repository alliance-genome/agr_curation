import { Button } from "primereact/button";

export const TextDialogTemplate = ({ entity, handleOpen, text, underline=true }) => {
  if (!entity || !handleOpen || !text) return null;

  return (
    <>
      <Button className="-my-2 p-0 p-button-text"
        onClick={() => { handleOpen(entity) }} >
        <div
          className={`m-0 p-1 overflow-hidden text-overflow-ellipsis ${underline ? "underline" : ""}`}
          dangerouslySetInnerHTML={{ __html: text }}
        />
      </Button>
    </>
  );
};