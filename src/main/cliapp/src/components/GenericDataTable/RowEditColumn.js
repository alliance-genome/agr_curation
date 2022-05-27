import React from 'react';
import { Column } from 'primereact/column';

export const RowEditColumn = ({ isEditable }) => {
  console.log("Hello");
  if(isEditable){
    return (
      <Column field='rowEditor' rowEditor style={{maxWidth: '7rem', minWidth: '7rem'}} 
        headerStyle={{ width: '7rem', position: 'sticky' }} bodyStyle={{ textAlign: 'center' }} frozen headerClassName='surface-0'/>
    );
  }else {
    return null;
  };
}; 
