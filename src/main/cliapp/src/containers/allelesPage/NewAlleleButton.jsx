import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * Opens the allele create form in a new tab, so an allele part way through being edited is not
 * navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewAlleleButton = ({ disabled = false, className }) => {
	// resolved through the router so the hash and any basename are applied
	const createAlleleHref = useHref('/allele/create');

	return (
		<Button
			label="New Allele"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => window.open(createAlleleHref, '_blank')}
		/>
	);
};
