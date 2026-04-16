import { Message } from 'primereact/message';
import { Link } from 'react-router-dom';
export const DetailMessage = ({ identifier, text, display, detailPage }) => {
	if (!display || !identifier || !text || !detailPage) return null;
	return (
		<Message
			aria-label="detailMessage"
			severity="info"
			text={
				<Link target="_blank" to={`/${detailPage.toLowerCase()}/${identifier}`}>
					{text}
				</Link>
			}
		/>
	);
};
