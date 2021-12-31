import React from 'react'
import { OntologyTable } from './OntologyTable';

export const WBbtOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" }
  ]

  return (
    < OntologyTable
      endpoint={"wbbtterm"}
      ontologyAbbreviation={"WBbt"}
      columnMap={columns}
    />
  )

}
