import React from 'react'
import { OntologyTable } from './OntologyTable';

export const CHEBIOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" }
  ]

  return(
    < OntologyTable
      endpoint={"chebiterm"}
      ontologyAbbreviation={"ChEBI"}
      columnMap={columns}
    />
    )
}
