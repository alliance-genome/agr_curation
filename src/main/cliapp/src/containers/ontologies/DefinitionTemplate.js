import React from 'react';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';

export const DefinitionTemplate = ({ rowData }) => {
  return (
    <>
      <EllipsisTableCell otherClasses={`b${rowData.curie.replaceAll(':', '')}`}>{rowData.definition}</EllipsisTableCell>
      <Tooltip target={`.b${rowData.curie.replaceAll(':', '')}`} content={rowData.definition} style={{ width: '450px', maxWidth: '450px' }} />
    </>
  )
}
