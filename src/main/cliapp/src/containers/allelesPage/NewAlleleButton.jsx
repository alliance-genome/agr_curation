import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * A function that opens the allele create form in a new tab.
 *
 * @returns {(sourceIdentifier?: string) => void} opens the form, pre-filled from the allele with this
 *   identifier when one is given
 */
export const useOpenAlleleCreatePage = () => {
	// resolved through the router so the hash and any basename are applied
	const createAlleleHref = useHref('/allele/create');

	return (sourceIdentifier) => {
		const href = sourceIdentifier
			? `${createAlleleHref}?from=${encodeURIComponent(sourceIdentifier)}`
			: createAlleleHref;
		window.open(href, '_blank');
	};
};

/**
 * Opens the allele create form in a new tab, so an allele part way through being edited is not
 * navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewAlleleButton = ({ disabled = false, className }) => {
	const openAlleleCreatePage = useOpenAlleleCreatePage();

	return (
		<Button
			label="New Allele"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => openAlleleCreatePage()}
		/>
	);
};

/**
 * Opens the allele create form in a new tab, pre-filled from an existing allele.
 *
 * @param {Object} props
 * @param {string} props.sourceIdentifier - identifier of the allele to copy
 * @param {boolean} [props.disabled] - true while there is no allele to copy
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const DuplicateAlleleButton = ({ sourceIdentifier, disabled = false, className }) => {
	const openAlleleCreatePage = useOpenAlleleCreatePage();

	return (
		<Button
			label="Duplicate"
			icon="pi pi-copy"
			className={className}
			disabled={disabled || !sourceIdentifier}
			onClick={() => openAlleleCreatePage(sourceIdentifier)}
		/>
	);
};
