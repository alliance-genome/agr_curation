import { Link } from 'react-router-dom';
import { Tooltip } from 'primereact/tooltip';

/**
 * A CSS class naming the row's tooltip target.
 *
 * The class is built from the identifier, which is whatever the entity carries: a curie, a
 * primary external id, or a MOD internal id, which for a variant is an MD5 hash. A class
 * cannot start with a digit and cannot hold a colon, and an invalid one makes the
 * querySelectorAll Tooltip runs on it throw, so the identifier is prefixed and everything
 * outside the safe set is replaced.
 *
 * @param {string} identifier
 * @returns {string} a valid CSS class
 */
const toTooltipClass = (identifier) => `entity-details-${identifier.replace(/[^a-zA-Z0-9_-]/g, '-')}`;

export const EntityDetailsAction = ({ endpoint, identifier, disabled }) => {
	const disabledClasses = disabled ? 'pointer-events-none opacity-50' : '';

	if (!identifier) return null;

	const tooltipClass = toTooltipClass(identifier);

	return (
		<>
			<Link to={`/${endpoint}/${identifier}`} target="_blank" className={`${tooltipClass} ${disabledClasses}`}>
				<i className="pi pi-info-circle"></i>
			</Link>
			<Tooltip target={`.${tooltipClass}`} content={'Open Details'} />
		</>
	);
};
