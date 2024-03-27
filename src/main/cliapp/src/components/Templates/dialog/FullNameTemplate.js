import { Button } from "primereact/button";

export const FullNameTemplate = ({ alleleFullName, handleOpen }) => {
  if (!alleleFullName || !handleOpen) return null;

  return (
    <>
      <Button className="p-button-text"
        onClick={() => { handleOpen(alleleFullName) }} >
        <div
          className='overflow-hidden text-overflow-ellipsis'
          dangerouslySetInnerHTML={{ __html: alleleFullName.displayText }}
        />
      </Button>
    </>
  );
};