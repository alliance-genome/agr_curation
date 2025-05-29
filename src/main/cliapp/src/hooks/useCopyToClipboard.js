import { useState } from 'react';

export const useCopyToClipboard = () => {
	const [copied, setCopied] = useState(false);

	const copy = async (text) => {
		try {
			await navigator.clipboard.writeText(text);
			setCopied(true);
			setTimeout(() => setCopied(false), 2000); // Reset after 2s
		} catch (err) {
			console.error('Copy failed:', err);
			setCopied(false);
		}
	};

	return { copy, copied };
};

export default useCopyToClipboard;
