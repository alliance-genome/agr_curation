import React from 'react';

export const ListTableCell = ({ template, listData, showBullets, style}) => {
	let suppress = 'list-none'
	if(showBullets)
		suppress = ''
	return (
		<ul className={`pl-0 ${suppress}`}>
			{listData?.map((item, index) =>
				<li key={index}> 
					{template(item)}
				</li>
			)}
		</ul>
	);
}
