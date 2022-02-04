import React from 'react'
import { OntologyTable } from './OntologyTable';

export const SOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "namespace", header: "Name Space" },
    { field: "definition", header: "Definition" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"soterm"}
      ontologyAbbreviation={"SO"}
      columns={columns}
    />
  )
}
