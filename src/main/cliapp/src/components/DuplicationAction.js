import { Button } from "primereact/button";
export const DuplicationAction = ({ props, handleDuplication, disabled }) => {
  return <Button icon="pi pi-copy" disabled={disabled} className="p-button-text" onClick={() => handleDuplication(props)}/> ;
}