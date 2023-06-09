import React from 'react';

export const ListTableCell = ({ template, listData, showBullets, style}) => {

	const leftAlign = { textAlign: 'left' };
	let alignment = style ?? leftAlign;
	let suppress = 'list-none'
	if(showBullets)
		suppress = ''
	return (
		<ul className={`pl-0 ${suppress}`}>
			{listData?.map((item, index) =>
				<li key={index} style={alignment}>
					{template(item)}
				</li>
			)}
		</ul>
	);
}
