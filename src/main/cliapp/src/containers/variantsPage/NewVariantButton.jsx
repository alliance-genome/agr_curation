import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * Opens the variant create form in a new tab, so a variant part way through being edited is not
 * navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewVariantButton = ({ disabled = false, className }) => {
	const createVariantHref = useHref('/variant/create');

	return (
		<Button
			label="New Variant"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => window.open(createVariantHref, '_blank')}
		/>
	);
};
