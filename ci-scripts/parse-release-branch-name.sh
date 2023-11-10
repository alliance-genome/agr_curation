#!/bin/bash
set -e

BRANCH_NAME=$1

if [[ ! $BRANCH_NAME =~ ^release/v?[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+(-rc[[:digit:]]+)?$ ]]; then
    >&2 echo "Failure: branch '${BRANCH_NAME}' does not match the release-branch pattern."
    false
fi

RELEASE=$(echo ${BRANCH_NAME} | sed s~release/~~)
echo ${RELEASE}
