import React, { useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

import { Tooltip } from 'primereact/tooltip';

export const AllelesTable = () => {

  const [isEnabled, setIsEnabled] = useState(true);

  const descriptionTemplate = (rowData) => {
    return (
      <>
        <EllipsisTableCell otherClasses={`a${rowData.curie.replace(':', '')}`}>
          {rowData.description}
        </EllipsisTableCell>
        <Tooltip target={`.a${rowData.curie.replace(':', '')}`} content={rowData.description} />
      </>
    );
  }

  const symbolTemplate = (rowData) => {
    return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
  }

  const taxonTemplate = (rowData) => {
      if (rowData.taxon) {
          return (
              <>
                  <EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.taxon.curie.replace(':', '')}`}>
                      {rowData.taxon.name} ({rowData.taxon.curie})
                  </EllipsisTableCell>
                  <Tooltip target={`.${"TAXON_NAME_"}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
              </>
          );
      }
  }

  const columns = [
    {
      field: "curie",
      header: "Curie",
      sortable: { isEnabled },
      filter: true,
      filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]}, 
    },
    {
      field: "description",
      header: "Description",
      sortable: isEnabled,
      filter: true,
      body: descriptionTemplate,
      filterElement: {type: "input", filterName: "descriptionFilter", fields: ["description"]}, 
    },
    {
      field: "symbol",
      header: "Symbol",
      body: symbolTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "symbolFilter", fields: ["symbol"]}, 
    },
    {
      field: "taxon.name",
      header: "Taxon",
      body: taxonTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "taxonFilter", fields: ["taxon.curie","taxon.name"]}, 
    }
  ];

  return (
      <div className="card">
        <GenericDataTable 
          endpoint="allele" 
          tableName="Alleles" 
          columns={columns}  
          isEditable={false}
          isEnabled={isEnabled}
          setIsEnabled={setIsEnabled}
          initialColumnWidth={20}
        />
      </div>
  )
}
