import { Button } from 'primereact/button';

export const PageFooter = ({ handleSubmit }) => {
	return (
		<>
			<div className="flex justify-content-end">
				<Button
					label="Save"
					icon="pi pi-check"
					className="p-button-text p-button-lg pr-8 pt-5 "
					onClick={handleSubmit}
				/>
			</div>
		</>
	);
};
