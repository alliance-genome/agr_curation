import { Button } from "primereact/button";
export const DuplicationAction = ({ props, handleDuplication }) => {
  return <Button icon="pi pi-copy" className="p-button-text" onClick={() => handleDuplication(props)}/> ;
}