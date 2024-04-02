import { Button } from "primereact/button";

export const TextDialogTemplate = ({ entity, handleOpen, text, underline=true }) => {
  if (!entity || !handleOpen || !text) return null;

  return (
    <>
      <Button className="p-button-text"
        onClick={() => { handleOpen(entity) }} >
        <div
          className={`-my-4 p-1 overflow-hidden text-overflow-ellipsis ${underline ? "underline" : ""}`}
          dangerouslySetInnerHTML={{ __html: text }}
        />
      </Button>
    </>
  );
};