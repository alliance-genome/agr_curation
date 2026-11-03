import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * A function that opens the variant create form in a new tab.
 *
 * @returns {(sourceIdentifier?: string) => void} opens the form, pre-filled from the variant with this
 *   identifier when one is given
 */
export const useOpenVariantCreatePage = () => {
	// resolved through the router so the hash and any basename are applied
	const createVariantHref = useHref('/variant/create');

	return (sourceIdentifier) => {
		const href = sourceIdentifier
			? `${createVariantHref}?from=${encodeURIComponent(sourceIdentifier)}`
			: createVariantHref;
		window.open(href, '_blank');
	};
};

/**
 * Opens the variant create form in a new tab, so a variant part way through being edited is not
 * navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewVariantButton = ({ disabled = false, className }) => {
	const openVariantCreatePage = useOpenVariantCreatePage();

	return (
		<Button
			label="New Variant"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => openVariantCreatePage()}
		/>
	);
};

/**
 * Opens the variant create form in a new tab, pre-filled from an existing variant.
 *
 * @param {Object} props
 * @param {string} props.sourceIdentifier - identifier of the variant to copy
 * @param {boolean} [props.disabled] - true while there is no variant to copy
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const DuplicateVariantButton = ({ sourceIdentifier, disabled = false, className }) => {
	const openVariantCreatePage = useOpenVariantCreatePage();

	return (
		<Button
			label="Clone"
			icon="pi pi-copy"
			className={className}
			disabled={disabled || !sourceIdentifier}
			onClick={() => openVariantCreatePage(sourceIdentifier)}
		/>
	);
};
