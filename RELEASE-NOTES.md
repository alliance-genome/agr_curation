# AGR curation release notes

## v0.0.31
 * Setup additional application environments
    * Alpha environment (SCRUM-451, SCRUM-459, #83)
    * Beta environment (SCRUM-461, #102)
 * Cosmetic UI changes (SCRUM-568, #104, #100, #98, #97, #94)
 * Enabled in-table editing in disease annotation table
    * disease field (SCRUM-492, #90, #93, #101, #89)
    * subject field (SCRUM-473, #87, #86, #85, #84, #82, #75)
 * Added API endpoints to load disease annotations with evidence codes (SCRUM-322, #99)
 * Reduced reindexing failure rate (#95)
 * Integrated application compilation into Docker image building (#91)
 * Added integration testing for basic gene loading (SCRUM-248, #81, #80, #77, #74, #73, #72)
 * Added several testfile for later inclusion in integration testing (#71)
 * Temporarily disabled auto scaling (commit b54ebb8 in #76)
 * Other minor changes (#96, #92, #78, #69)