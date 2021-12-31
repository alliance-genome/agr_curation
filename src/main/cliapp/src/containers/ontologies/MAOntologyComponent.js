import React from 'react'
import { OntologyTable } from './OntologyTable';

export const MAOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" }
  ]

  return (
    < OntologyTable
      endpoint={"materm"}
      ontologyAbbreviation={"MA"}
      columnMap={columns}
    />
  )
}
