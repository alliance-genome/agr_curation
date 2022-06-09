import React from 'react'
import { OntologyTable } from './OntologyTable';

export const XSMOOntologyComponent = () => {
	const columns = [
		{ field: "curie", header: "Curie" },
		{ field: "name", header: "Name" },
		{ field: "definition", header: "Definition" },
		{ field: "obsolete", header: "Obsolete" }
	]

	return (
		< OntologyTable
			endpoint={"xsmoterm"}
			ontologyAbbreviation={"XSMO"}
			columns={columns}
		/>
	)

}
