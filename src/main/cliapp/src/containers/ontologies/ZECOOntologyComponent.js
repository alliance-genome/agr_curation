import React from 'react'
import { OntologyTable } from './OntologyTable';

export const ZECOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"zecoterm"}
      ontologyAbbreviation={"ZECO"}
      columns={columns}
    />
  )
}
