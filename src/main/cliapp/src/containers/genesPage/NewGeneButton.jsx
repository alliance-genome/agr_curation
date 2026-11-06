import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * Opens the gene create form in a new tab, so a gene part way through being edited is not navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewGeneButton = ({ disabled = false, className }) => {
	// resolved through the router so the hash and any basename are applied
	const createGeneHref = useHref('/gene/create');

	return (
		<Button
			label="New Gene"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => window.open(createGeneHref, '_blank')}
		/>
	);
};
