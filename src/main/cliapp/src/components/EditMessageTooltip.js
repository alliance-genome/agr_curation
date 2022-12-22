import React from "react";
import {Tooltip} from "primereact/tooltip";

export const EditMessageTooltip = () => {
	return (
		<>
			<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',	 }}/>
			<span className="exclamation-icon" data-pr-tooltip="Edits made to this field will only be saved to the database once the entire annotation is saved.">
				<i className="pi pi-exclamation-circle" style={{ 'fontSize': '1em' }}></i>
			</span>
		</>
	)
};
