import React from 'react'
import { OntologyTable } from './OntologyTable';

export const DiseaseOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "definition", header: "Definition" }
  ]

  return(
    < OntologyTable
      endpoint={"doterm"}
      ontologyAbbreviation={"Diseases"}
      columnMap={columns}
    />
  )
  
}
