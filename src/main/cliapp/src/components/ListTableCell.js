import React from 'react';

export const ListTableCell = ({ template, listData }) => {
  return (
    <ul className='pl-0 list-none'>
      {listData?.map((item, index) =>
        <li key={index}>
          {template(item)}
        </li>
      )}
    </ul>
  );
}
