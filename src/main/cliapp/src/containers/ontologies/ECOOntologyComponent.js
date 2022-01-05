import React from 'react'
import { OntologyTable } from './OntologyTable';

export const ECOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" },
    { field: "abbreviation", header: "Abbreviation" }
  ]

  return(
    < OntologyTable
      endpoint={"ecoterm"}
      ontologyAbbreviation={"ECO"}
      columnMap={columns}
    />
    )
}
