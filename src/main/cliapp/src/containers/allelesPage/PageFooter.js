import { Splitter, SplitterPanel } from "primereact/splitter";
import { Button } from "primereact/button";

export const PageFooter = ({ handleSubmit }) => {
  return (
    <>
			<Splitter style={{border:'none', paddingTop:'12px', height:'10%'}} gutterSize="0">
				<SplitterPanel style={{textAlign: 'right', flexGrow: 0}} size={30}>
						<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
				</SplitterPanel>
			</Splitter>
		</>
  )
}