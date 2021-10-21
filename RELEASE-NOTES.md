# AGR curation release notes

## v0.0.31
 * Setup additional application environments
    * Alpha environment (SCRUM-451, SCRUM-459, [PR#83](#83))
    * Beta environment (SCRUM-461, [PR#102](#102))
 * Cosmetic UI changes (SCRUM-568, [PR#104](#104), [PR#100](#100), [PR#98](#98), [PR#97](#97), [PR#94](#94))
 * Enabled in-table editing in disease annotation table
    * disease field (SCRUM-492, [PR#90](#90), [PR#93](#93), [PR#101](#101), [PR#89](#89))
    * subject field (SCRUM-473, [PR#87](#87), [PR#86](#86), [PR#85](#85), [PR#84](#84), [PR#82](#82), [PR#75](#75))
 * Added API endpoints to load disease annotations with evidence codes (SCRUM-322, [PR#99](#99))
 * Reduced reindexing failure rate ([PR#95](#95))
 * Integrated application compilation into Docker image building ([PR#91](#91))
 * Added integration testing for basic gene loading (SCRUM-248, [PR#81](#81), [PR#80](#80), [PR#77](#77), [PR#74](#74), [PR#73](#73), [PR#72](#72))
 * Added several testfile for later inclusion in integration testing ([PR#71](#71))
 * Temporarily disabled auto scaling (commit b54ebb8 in [PR#76](#76))
 * Other minor changes ([PR#96](#96), [PR#92](#92), [PR#78](#78), [PR#69](#69))