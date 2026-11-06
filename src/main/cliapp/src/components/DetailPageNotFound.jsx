import { Message } from 'primereact/message';
import { Link } from 'react-router-dom';
import { errorMessageOf } from '../utils/utils';

/**
 * The message a detail page shows in place of its form when it has no entity to show: none has the
 * identifier in its URL, or the request for it failed.
 *
 * @param {Object} props
 * @param {string} props.entityType the entity's display name, such as 'Allele'
 * @param {string} props.identifier the identifier from the page's URL
 * @param {string} props.listPath the path of the entity's table page
 * @param {Object} [props.error] the failed request's error; absent when the request found nothing
 */
export const DetailPageNotFound = ({ entityType, identifier, listPath, error }) => {
	const text = error
		? `Could not load ${entityType.toLowerCase()} ${identifier}: ${errorMessageOf(error)}`
		: `No ${entityType.toLowerCase()} has the identifier ${identifier}`;

	return (
		<div className="flex flex-column align-items-center gap-3 p-6">
			<h1>{error ? `${entityType} not loaded` : `${entityType} not found`}</h1>
			<Message severity="error" text={text} />
			<Link to={listPath}>Back to the {entityType.toLowerCase()} table</Link>
		</div>
	);
};
