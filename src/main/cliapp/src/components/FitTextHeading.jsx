import { useLayoutEffect, useRef } from 'react';

// The smallest the heading shrinks to, as a share of its styled font size.
const MINIMUM_SCALE = 0.5;

/**
 * Shrinks a heading's font size until its text fits its width on one line. Text that still does not
 * fit at the smallest size is cut off with an ellipsis, and the full text is shown on hover.
 *
 * @param {HTMLElement} heading
 */
const fitHeading = (heading) => {
	heading.style.fontSize = '';
	heading.removeAttribute('title');

	const styledSize = parseFloat(window.getComputedStyle(heading).fontSize);
	const minimumSize = styledSize * MINIMUM_SCALE;
	if (!styledSize || heading.scrollWidth <= heading.clientWidth) return;

	let fontSize = Math.max(minimumSize, Math.floor((styledSize * heading.clientWidth) / heading.scrollWidth));
	heading.style.fontSize = `${fontSize}px`;
	while (heading.scrollWidth > heading.clientWidth && fontSize > minimumSize) {
		fontSize = Math.max(minimumSize, fontSize - 1);
		heading.style.fontSize = `${fontSize}px`;
	}

	if (heading.scrollWidth > heading.clientWidth) heading.title = heading.textContent;
};

/**
 * A page heading kept to one line, its font scaled down as needed to fit the width it is given.
 *
 * @param {Object} props
 * @param {string} props.html - the heading's content, as HTML
 * @param {string} [props.className] - classes added to the heading
 */
export const FitTextHeading = ({ html, className = '' }) => {
	const headingRef = useRef(null);

	useLayoutEffect(() => {
		const heading = headingRef.current;
		if (!heading) return undefined;

		fitHeading(heading);

		// A theme font that loads after the first fit changes the text's width but not the heading's.
		let isMounted = true;
		document.fonts?.ready.then(() => {
			if (isMounted) fitHeading(heading);
		});

		// Refitting changes the heading's height, so only a change in width refits it again.
		let observer = null;
		if (typeof ResizeObserver !== 'undefined') {
			let fittedWidth = heading.clientWidth;
			observer = new ResizeObserver(() => {
				if (heading.clientWidth === fittedWidth) return;
				fittedWidth = heading.clientWidth;
				fitHeading(heading);
			});
			observer.observe(heading);
		}

		return () => {
			isMounted = false;
			observer?.disconnect();
		};
	}, [html]);

	return (
		<h1
			ref={headingRef}
			className={`flex-1 min-w-0 m-0 align-self-center white-space-nowrap overflow-hidden text-overflow-ellipsis ${className}`.trim()}
			dangerouslySetInnerHTML={{ __html: html }}
		/>
	);
};
