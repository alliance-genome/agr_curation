import React from 'react';

export const DialogCard = ({ topText="", bottomText="", children }) => {
	return (
		<div className="card mb-0">
			<div>
				<span className="block text-500 font-medium mb-3">{topText}</span>
				<div className="text-900 font-medium text-xl">
					{children}
				</div>
			</div>
			<span className="text-500">{bottomText}</span>
		</div>
	)
}
