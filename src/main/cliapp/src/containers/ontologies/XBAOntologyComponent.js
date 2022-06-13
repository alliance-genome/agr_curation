import React from 'react'
import { OntologyTable } from './OntologyTable';

export const XBAOntologyComponent = () => {
	const columns = [
		{ field: "curie", header: "Curie" },
		{ field: "name", header: "Name" },
		{ field: "definition", header: "Definition" },
		{ field: "obsolete", header: "Obsolete" }
	]

	return (
		< OntologyTable
			endpoint={"xbaterm"}
			ontologyAbbreviation={"XBA"}
			columns={columns}
		/>
	)

}
