import { Button } from "primereact/button";

export const TextDialogTemplate = ({ entity, handleOpen, text }) => {
  if (!entity || !handleOpen || !text) return null;

  return (
    <>
      <Button className="p-button-text"
        onClick={() => { handleOpen(entity) }} >
        <div
          className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{ __html: text }}
        />
      </Button>
    </>
  );
};