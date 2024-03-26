import { Message } from "primereact/message";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
export const DetailMessage = ({ identifier, text, display }) => {
  if (!display) return null;
  return (
    <Message severity="info" text={<Link target="_blank" to={`allele/${identifier}`}>{text}</Link>} />
  );
};