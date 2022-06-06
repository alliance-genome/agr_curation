import React, { useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Tooltip } from 'primereact/tooltip';

import {EllipsisTableCell} from "../../components/EllipsisTableCell";

export const AffectedGenomicModelTable = () => {
  const defaultVisibleColumns = ["Curie", "Name", "Sub Type", "Taxon"];

  const [isEnabled, setIsEnabled] = useState(true);

  const nameTemplate = (rowData) => {
    return (
      <>
        <div className={`overflow-hidden text-overflow-ellipsis ${rowData.curie.replace(':', '')}`} dangerouslySetInnerHTML={{ __html: rowData.name }} />
        <Tooltip target={`.${rowData.curie.replace(':', '')}`}>
          <div dangerouslySetInnerHTML={{ __html: rowData.name }} />
        </Tooltip>
      </>
    )
  }

  const taxonBodyTemplate = (rowData) => {
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
  };
  
  const columns = [
    {
      field: "curie",
      header: "Curie",
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]}, 
    },
    {
      field: "name",
      header: "Name",
      body: nameTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]}, 
    },
    {
      field: "subtype",
      header: "Sub Type",
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "subtypeFilter", fields: ["subtype"]}, 
    },
    {
      field: "parental_population",
      header: "Parental Population",
      sortable: isEnabled,
      filter: true,
      filterElement: {type: "input", filterName: "parental_populationFilter", fields: ["parental_population"]}, 
    },
    {
      field: "taxon.name",
      header: "Taxon",
      sortable: isEnabled,
      body: taxonBodyTemplate,
      filter: true,
      filterElement: {type: "input", filterName: "taxonFilter", fields: ["taxon.curie","taxon.name"]}, 
    }
 ];

  return (
      <div className="card">
        <GenericDataTable 
          endpoint="agm" 
          tableName="Affected Genomic Models" 
          columns={columns}  
          isEditable={false}
          isEnabled={isEnabled}
          setIsEnabled={setIsEnabled}
          initialColumnWidth={100 / columns.length}
          defaultVisibleColumns={defaultVisibleColumns}
        />
      </div>
  )
}
