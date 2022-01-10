import React from 'react'
import { OntologyTable } from './OntologyTable';

export const GOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "namespace", header: "Name Space" },
    { field: "definition", header: "Definition" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"goterm"}
      ontologyAbbreviation={"GO"}
      columnMap={columns}
    />
  )
}
