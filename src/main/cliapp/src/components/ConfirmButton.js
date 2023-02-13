import React, { useState } from 'react';
import { ConfirmDialog } from 'primereact/confirmdialog';
import { Button } from 'primereact/button';

export const ConfirmButton = ({ messageText, headerText, buttonText, acceptHandler, rejectHandler, buttonClassName, disabled= false}) => {

	const [visible, setVisible] = useState(false);

	const accept = () => {
		if(acceptHandler) {
			acceptHandler();
		}
	}

	const reject = () => {
		if(rejectHandler) {
			rejectHandler();
		}
	}

	return (
		<>
		<ConfirmDialog visible={visible} onHide={() => setVisible(false)}
			message={messageText}
			header={headerText}
			icon="pi pi-exclamation-triangle"
			accept={accept} reject={reject} />

			<Button className={buttonClassName} onClick={() => setVisible(true)} label={buttonText} />
		</>
	);


}
