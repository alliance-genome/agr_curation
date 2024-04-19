import { Message } from "primereact/message";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
export const DetailMessage = ({ identifier, text, display }) => {
  if (!display || !identifier || !text) return null;
  return (
    <Message aria-label="detailMessage" severity="info" text={<Link target="_blank" to={`allele/${identifier}`}>{text}</Link>} />
  );
};