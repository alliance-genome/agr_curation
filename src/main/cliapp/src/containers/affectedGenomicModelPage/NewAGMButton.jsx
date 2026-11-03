import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * A function that opens the AGM create form in a new tab.
 *
 * @returns {(sourceIdentifier?: string) => void} opens the form, pre-filled from the AGM with this
 *   identifier when one is given
 */
export const useOpenAGMCreatePage = () => {
	// resolved through the router so the hash and any basename are applied
	const createAGMHref = useHref('/agm/create');

	return (sourceIdentifier) => {
		const href = sourceIdentifier ? `${createAGMHref}?from=${encodeURIComponent(sourceIdentifier)}` : createAGMHref;
		window.open(href, '_blank');
	};
};

/**
 * Opens the AGM create form in a new tab, so an AGM part way through being edited is not
 * navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewAGMButton = ({ disabled = false, className }) => {
	const openAGMCreatePage = useOpenAGMCreatePage();

	return (
		<Button
			label="New AGM"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => openAGMCreatePage()}
		/>
	);
};

/**
 * Opens the AGM create form in a new tab, pre-filled from an existing AGM.
 *
 * @param {Object} props
 * @param {string} props.sourceIdentifier - identifier of the AGM to copy
 * @param {boolean} [props.disabled] - true while there is no AGM to copy
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const DuplicateAGMButton = ({ sourceIdentifier, disabled = false, className }) => {
	const openAGMCreatePage = useOpenAGMCreatePage();

	return (
		<Button
			label="Clone"
			icon="pi pi-copy"
			className={className}
			disabled={disabled || !sourceIdentifier}
			onClick={() => openAGMCreatePage(sourceIdentifier)}
		/>
	);
};
