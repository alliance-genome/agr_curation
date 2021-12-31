import React from 'react'
import { OntologyTable } from './OntologyTable';

export const DAOOntologyComponent = () => {

  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" }
  ]

  return(
    < OntologyTable
      endpoint={"daoterm"}
      ontologyAbbreviation={"DAO"}
      columnMap={columns}
    />
  )
}
