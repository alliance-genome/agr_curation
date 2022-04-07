import React from 'react';

export const EllipsisTableCell = ({ children, otherClasses = '' }) => {
  return (
    <div className={`overflow-hidden text-overflow-ellipsis ${otherClasses}`} >
      {children}
    </div>
  );
}
