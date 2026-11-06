import { Button } from 'primereact/button';
import { useHref } from 'react-router-dom';

/**
 * A function that opens the cassette create form in a new tab (SCRUM-6748).
 *
 * @returns {(sourceIdentifier?: string) => void} opens the form, pre-filled from the cassette with this
 *   identifier when one is given
 */
export const useOpenCassetteCreatePage = () => {
	// resolved through the router so the hash and any basename are applied
	const createCassetteHref = useHref('/cassette/create');

	return (sourceIdentifier) => {
		const href = sourceIdentifier
			? `${createCassetteHref}?from=${encodeURIComponent(sourceIdentifier)}`
			: createCassetteHref;
		window.open(href, '_blank');
	};
};

/**
 * Opens the cassette create form in a new tab, so a cassette part way through being edited is not
 * navigated away from.
 *
 * @param {Object} props
 * @param {boolean} [props.disabled] - true while the table it sits above is in edit mode
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const NewCassetteButton = ({ disabled = false, className }) => {
	const openCassetteCreatePage = useOpenCassetteCreatePage();

	return (
		<Button
			label="New Cassette"
			icon="pi pi-plus"
			className={className}
			disabled={disabled}
			onClick={() => openCassetteCreatePage()}
		/>
	);
};

/**
 * Opens the cassette create form in a new tab, pre-filled from an existing cassette.
 *
 * @param {Object} props
 * @param {string} props.sourceIdentifier - identifier of the cassette to copy
 * @param {boolean} [props.disabled] - true while there is no cassette to copy
 * @param {string} [props.className] - the button style of the header it sits in
 */
export const DuplicateCassetteButton = ({ sourceIdentifier, disabled = false, className }) => {
	const openCassetteCreatePage = useOpenCassetteCreatePage();

	return (
		<Button
			label="Clone"
			icon="pi pi-copy"
			className={className}
			disabled={disabled || !sourceIdentifier}
			onClick={() => openCassetteCreatePage(sourceIdentifier)}
		/>
	);
};
