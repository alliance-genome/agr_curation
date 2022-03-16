import React from 'react'
import { OntologyTable } from './OntologyTable';
import { NameTemplate } from './NameTemplate';
import { DefinitionTemplate } from './DefinitionTemplate';

export const XCOOntologyComponent = () => {
  const columns = [
    { field: "curie", header: "Curie" },
    { field: "name", header: "Name", body: (rowData) => <NameTemplate rowData={rowData}/> },
    { field: "definition", header: "Definition", body: (rowData) => <DefinitionTemplate rowData={rowData} />},
    { field: "obsolete", header: "Obsolete" }
  ]

  return (
    < OntologyTable
      endpoint={"xcoterm"}
      ontologyAbbreviation={"XCO"}
      columns={columns}
    />
  )
}
