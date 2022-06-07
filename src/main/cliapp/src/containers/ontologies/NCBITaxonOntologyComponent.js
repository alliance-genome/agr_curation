import React from 'react'
import { OntologyTable } from './OntologyTable';
import { NameTemplate } from './NameTemplate';

export const NCBITaxonOntologyComponent = () => {
	const columns = [
		{ field: "curie", header: "Curie" },
		{ field: "name", header: "Name", body: (rowData) => <NameTemplate rowData={rowData}/> },
		{ field: "obsolete", header: "Obsolete" }
	]

	return (
		< OntologyTable
			endpoint={"ncbitaxonterm"}
			ontologyAbbreviation={"NCBITaxon"}
			columns={columns}
		/>
	)

}
