import React from 'react'
import { OntologyTable } from './OntologyTable';
import { NameTemplate } from './NameTemplate';
import { DefinitionTemplate } from './DefinitionTemplate';

export const GOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name", body: (rowData) => <NameTemplate rowData={rowData}/> },
    { field: "definition", header: "Definition", body: (rowData) => <DefinitionTemplate rowData={rowData} />},
    { field: "namespace", header: "Name Space" },
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"goterm"}
      ontologyAbbreviation={"GO"}
      columns={columns}
    />
  )
}
