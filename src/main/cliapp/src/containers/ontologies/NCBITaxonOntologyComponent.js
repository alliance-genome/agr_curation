import React from 'react'
import { OntologyTable } from './OntologyTable';

export const NCBITaxonOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"ncbitaxonterm"}
      ontologyAbbreviation={"NCBITaxon"}
      columnMap={columns}
    />
  )

}
