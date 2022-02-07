import React from 'react'
import { OntologyTable } from './OntologyTable';

export const ECOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" },
    { field: "abbreviation", header: "Abbreviation" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return(
    < OntologyTable
      endpoint={"ecoterm"}
      ontologyAbbreviation={"ECO"}
      columns={columns}
    />
    )
}
