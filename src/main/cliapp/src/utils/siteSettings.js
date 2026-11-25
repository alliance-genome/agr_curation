/**
 * Whether the curator's site settings turn search debugging on. Accepts the setting as a boolean or as the string
 * `'true'`/`'false'`, the two forms saved settings hold.
 *
 * @param {object|null} siteSettings - the curator's site settings
 * @returns {boolean}
 */
export const isDebugOn = (siteSettings) => siteSettings?.debug === true || siteSettings?.debug === 'true';
