import React from 'react';
import { InputText } from 'primereact/inputtext';
import { CronFields } from '../../components/CronFields';
import { Dropdown } from 'primereact/dropdown';
import { DataLoadService } from '../../service/DataLoadService';

export const URLForm = ({ hideURL, hideOntology, newBulkLoad, onChange, disableFormFields }) => {
	const dataLoadService = new DataLoadService();

	const ontologyTypes = dataLoadService.getOntologyTypes();
	return (
		<>
			{!hideURL.current && (
				<>
					{!hideOntology.current && (
						<div className="field">
							<label htmlFor="ontology">Ontology Type</label>
							<Dropdown
								id="ontology"
								value={newBulkLoad.ontologyType}
								options={ontologyTypes}
								onChange={onChange}
								placeholder={'Select Ontology Type'}
								className="p-col-12"
								name="ontologyType"
								disabled={disableFormFields}
							/>
						</div>
					)}
					<CronFields newItem={newBulkLoad} onChange={onChange} />
					<div className="field">
						<label htmlFor="bulkloadUrl">URL</label>
						<InputText name="bulkloadUrl" value={newBulkLoad.bulkloadUrl} onChange={onChange} placeholder="Enter URL" />
					</div>
				</>
			)}
		</>
	);
};
