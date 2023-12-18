import { Button } from "primereact/button";
export const DuplicationAction = ({ props, handleDuplication, disabled }) => {
  return <Button icon="pi pi-copy" disabled={disabled} style={{padding:'0rem', fontSize: '1rem'}} className="p-button-text" onClick={() => handleDuplication(props)}/> ;
}
