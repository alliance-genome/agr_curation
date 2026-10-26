import { useCallback, useEffect, useRef, useState } from 'react';

export const SAVE_STATUS = {
	QUEUED: 'queued',
	SAVING: 'saving',
	SAVED: 'saved',
	FAILED: 'failed',
	SKIPPED: 'skipped',
};

/**
 * @typedef {'queued'|'saving'|'saved'|'failed'|'skipped'} SaveStatus one of {@link SAVE_STATUS}
 * @typedef {{key: string, label: string, status: SaveStatus, message?: string}} SaveStep one thing a save writes;
 *   `message` says why it failed
 */

// How long the finished overlay stays up: briefly when everything saved, longer when there are messages to read.
export const SUCCESS_HOLD_MS = 750;
export const FAILURE_HOLD_MS = 2500;

/**
 * The progress of a save that writes several things, one row per thing, for {@link SaveProgressOverlay}.
 *
 * `isSaving` is true while any row is queued or being written, before the hold that follows.
 *
 * `begin` opens the overlay with every row queued; `start` and `settle` move a row through saving to saved or
 * failed; `skipRemaining` marks the rows that will not be written; `finish` closes the overlay after a hold.
 *
 * `finish` resolves `true` once the overlay has closed, at once when it is not open. It resolves `false` when it is
 * superseded before closing - by a later `begin` or `finish`, or by the page unmounting - and at once after the page
 * has unmounted, so a caller waiting to leave the page can tell that it should not.
 *
 * @returns {{
 *   steps: SaveStep[],
 *   isOpen: boolean,
 *   isSaving: boolean,
 *   begin: (steps: Array<{key: string, label: string}>) => void,
 *   start: (key: string) => void,
 *   settle: (key: string, outcome: {isSuccess: boolean, message?: string}) => void,
 *   skipRemaining: () => void,
 *   finish: () => Promise<boolean>,
 * }}
 */
export const useSaveProgress = () => {
	const [steps, setSteps] = useState([]);
	const [isOpen, setIsOpen] = useState(false);
	const stepsRef = useRef([]);
	const isOpenRef = useRef(false);
	const closeTimer = useRef(null);
	const resolveClose = useRef(null);
	const isMounted = useRef(true);

	const updateSteps = useCallback((update) => {
		stepsRef.current = update(stepsRef.current);
		setSteps(stepsRef.current);
	}, []);

	const setOpen = useCallback((open) => {
		isOpenRef.current = open;
		setIsOpen(open);
	}, []);

	const cancelClose = useCallback(() => {
		clearTimeout(closeTimer.current);
		closeTimer.current = null;
		resolveClose.current?.(false);
		resolveClose.current = null;
	}, []);

	useEffect(() => {
		isMounted.current = true;
		return () => {
			isMounted.current = false;
			cancelClose();
		};
	}, [cancelClose]);

	const begin = useCallback(
		(newSteps) => {
			cancelClose();
			updateSteps(() => newSteps.map(({ key, label }) => ({ key, label, status: SAVE_STATUS.QUEUED })));
			setOpen(true);
		},
		[cancelClose, updateSteps, setOpen]
	);

	const start = useCallback(
		(key) =>
			updateSteps((current) =>
				current.map((step) => (step.key === key ? { ...step, status: SAVE_STATUS.SAVING } : step))
			),
		[updateSteps]
	);

	const settle = useCallback(
		(key, outcome) =>
			updateSteps((current) =>
				current.map((step) =>
					step.key === key
						? outcome.isSuccess
							? { key, label: step.label, status: SAVE_STATUS.SAVED }
							: { key, label: step.label, status: SAVE_STATUS.FAILED, message: outcome.message }
						: step
				)
			),
		[updateSteps]
	);

	const skipRemaining = useCallback(
		() =>
			updateSteps((current) =>
				current.map((step) =>
					step.status === SAVE_STATUS.QUEUED || step.status === SAVE_STATUS.SAVING
						? { ...step, status: SAVE_STATUS.SKIPPED }
						: step
				)
			),
		[updateSteps]
	);

	const finish = useCallback(() => {
		cancelClose();
		if (!isMounted.current) return Promise.resolve(false);
		if (!isOpenRef.current) return Promise.resolve(true);

		const allSaved = stepsRef.current.every((step) => step.status === SAVE_STATUS.SAVED);

		return new Promise((resolve) => {
			resolveClose.current = resolve;
			closeTimer.current = setTimeout(
				() => {
					closeTimer.current = null;
					resolveClose.current = null;
					setOpen(false);
					resolve(true);
				},
				allSaved ? SUCCESS_HOLD_MS : FAILURE_HOLD_MS
			);
		});
	}, [cancelClose, setOpen]);

	const isSaving =
		isOpen && steps.some((step) => step.status === SAVE_STATUS.QUEUED || step.status === SAVE_STATUS.SAVING);

	return { steps, isOpen, isSaving, begin, start, settle, skipRemaining, finish };
};
