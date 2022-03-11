import React from 'react';

export const EllipsisTableCell = ({ children }) => {
  return (
    <div className='overflow-hidden text-overflow-ellipsis' >
      {children}
    </div>
  );
}
