import { createContext, useContext } from 'react';
import { SAVE_STATUS } from '../hooks/useSaveProgress';

// Colours come from the theme's palette variables; PrimeFlex has no red text classes.
const STATUS_DISPLAY = {
	[SAVE_STATUS.QUEUED]: { icon: 'pi pi-clock', color: 'var(--yellow-700)', text: 'Pending' },
	[SAVE_STATUS.SAVING]: { icon: 'pi pi-spin pi-spinner', color: 'var(--yellow-700)', text: 'Saving' },
	[SAVE_STATUS.SAVED]: { icon: 'pi pi-check-circle', color: 'var(--green-700)', text: 'Saved' },
	[SAVE_STATUS.FAILED]: { icon: 'pi pi-times-circle', color: 'var(--red-700)', text: 'Failed' },
	[SAVE_STATUS.SKIPPED]: { icon: 'pi pi-minus-circle', color: 'var(--text-color-secondary)', text: 'Not saved' },
};

/**
 * @param {import('../hooks/useSaveProgress').SaveStep[]} steps
 * @returns {string} the overlay's heading: in progress, done, or done with rows that did not save
 */
const headingFor = (steps) => {
	if (steps.some(({ status }) => status === SAVE_STATUS.QUEUED || status === SAVE_STATUS.SAVING)) return 'Saving...';
	if (steps.every(({ status }) => status === SAVE_STATUS.SAVED)) return 'Saved';
	return 'Save finished with errors';
};

/**
 * Covers the page while a save runs, listing each thing being written with its status and, for one that failed,
 * why.
 *
 * @param {Object} props
 * @param {boolean} props.isOpen
 * @param {import('../hooks/useSaveProgress').SaveStep[]} props.steps
 */
export const SaveProgressOverlay = ({ isOpen, steps }) => {
	if (!isOpen) return null;
	return (
		<div className="top-0 left-0 w-full h-full opacity-90 fixed p-5 surface-overlay z-2 origin-top">
			<div
				role="status"
				aria-label="Save progress"
				className="flex flex-column align-items-center justify-content-center h-screen"
			>
				<h1>{headingFor(steps)}</h1>
				<ul aria-label="Saved items" className="list-none p-0 m-0">
					{steps.map(({ key, label, status, message }) => {
						const { icon, color, text } = STATUS_DISPLAY[status];
						return (
							<li key={key} className="py-1" style={{ color }}>
								<div className="flex align-items-center gap-2 text-xl">
									<i className={icon} aria-hidden="true" />
									<span>
										{label}: {text}
									</span>
								</div>
								{message && <div className="ml-5">{message}</div>}
							</li>
						);
					})}
				</ul>
			</div>
		</div>
	);
};

const SaveProgressContext = createContext(null);

/**
 * Shares a page's {@link useSaveProgress} with the sections whose own Save buttons report to the page's overlay.
 *
 * @param {Object} props
 * @param {ReturnType<typeof import('../hooks/useSaveProgress').useSaveProgress>} props.value
 * @param {React.ReactNode} props.children
 */
export const SaveProgressProvider = ({ value, children }) => (
	<SaveProgressContext.Provider value={value}>{children}</SaveProgressContext.Provider>
);

/**
 * @returns {ReturnType<typeof import('../hooks/useSaveProgress').useSaveProgress> | null} the page's save
 *   progress, or null for a section rendered outside a page that shows one
 */
export const useSaveProgressContext = () => useContext(SaveProgressContext);
