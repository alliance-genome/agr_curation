import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * A function that opens the gene create form in a new tab.
 *
 * @returns {(sourceIdentifier?: string) => void} opens the form, filled with a copy of the gene with this identifier
 *   when one is given
 */
export const useOpenGeneCreatePage = () => {
	// resolved through the router so the hash and any basename are applied
	const createGeneHref = useHref('/gene/create');

	return (sourceIdentifier) => {
		const href = sourceIdentifier ? `${createGeneHref}?from=${encodeURIComponent(sourceIdentifier)}` : createGeneHref;
		window.open(href, '_blank');
	};
};

/**
 * Opens the gene create form in a new tab, so a gene part way through being edited is not navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewGeneButton = ({ disabled = false, className }) => {
	const openGeneCreatePage = useOpenGeneCreatePage();

	return (
		<Button
			label="New Gene"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => openGeneCreatePage()}
		/>
	);
};

/**
 * Opens the gene create form in a new tab, filled with a copy of an existing gene.
 *
 * @param {Object} props
 * @param {string} props.sourceIdentifier - identifier of the gene to copy
 * @param {boolean} [props.disabled] - true while there is no gene to copy
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const CopyGeneButton = ({ sourceIdentifier, disabled = false, className }) => {
	const openGeneCreatePage = useOpenGeneCreatePage();

	return (
		<Button
			label="Clone"
			icon="pi pi-copy"
			className={className}
			disabled={disabled || !sourceIdentifier}
			onClick={() => openGeneCreatePage(sourceIdentifier)}
		/>
	);
};
