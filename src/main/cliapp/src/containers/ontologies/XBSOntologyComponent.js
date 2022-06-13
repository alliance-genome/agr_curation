import React from 'react'
import { OntologyTable } from './OntologyTable';

export const XBSOntologyComponent = () => {
	const columns = [
		{ field: "curie", header: "Curie" },
		{ field: "name", header: "Name" },
		{ field: "definition", header: "Definition" },
		{ field: "obsolete", header: "Obsolete" }
	]

	return (
		< OntologyTable
			endpoint={"xbsterm"}
			ontologyAbbreviation={"XBS"}
			columns={columns}
		/>
	)

}
