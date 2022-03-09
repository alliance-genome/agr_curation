import React from 'react'
import { OntologyTable } from './OntologyTable';

export const MmusDvOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"mmusdvterm"}
      ontologyAbbreviation={"MmusDv"}
      columns={columns}
    />
  )

}
