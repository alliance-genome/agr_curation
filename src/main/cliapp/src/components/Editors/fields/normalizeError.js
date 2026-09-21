/**
 * Coerces an error payload into a `{severity, message}` object. Accepts either a
 * bare message string or an object already in that shape.
 *
 * @param {string|{severity?: string, message: string}|null|undefined} raw - the error payload
 * @returns {{severity: string, message: string}|null} null when there is no error
 */
export function normalizeError(raw) {
	if (!raw) return null;
	if (typeof raw === 'string') return { severity: 'error', message: raw };
	return { severity: raw.severity || 'error', message: raw.message };
}
