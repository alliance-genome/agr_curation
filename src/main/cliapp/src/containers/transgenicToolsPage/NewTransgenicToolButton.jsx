import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * A function that opens the transgenic tool create form in a new tab (SCRUM-6691).
 *
 * @returns {() => void}
 */
export const useOpenTransgenicToolCreatePage = () => {
	// resolved through the router so the hash and any basename are applied
	const createTransgenicToolHref = useHref('/transgenic-tool/create');

	return () => window.open(createTransgenicToolHref, '_blank');
};

/**
 * Opens the transgenic tool create form in a new tab, so a tool part way through being edited is not navigated
 * away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewTransgenicToolButton = ({ disabled = false, className }) => {
	const openTransgenicToolCreatePage = useOpenTransgenicToolCreatePage();

	return (
		<Button
			label="New Transgenic Tool"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => openTransgenicToolCreatePage()}
		/>
	);
};
