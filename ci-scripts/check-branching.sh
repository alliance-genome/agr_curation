#!/bin/bash
set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

BASE=$1
HEAD=$2

if [[ $BASE == 'production' ]]; then
    # Only allow merges from prodfix/* and release/* branches with a full release (no release candidates)
    if [[ ! $HEAD =~ ^prodfix/ ]] && [[ ! $HEAD =~ ^release/v?[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+$ ]]; then
        echo "Failure: branch '${HEAD}' not allowed to merge in '${BASE}'"
        false
    fi
elif [[ $BASE == 'beta' ]]; then
    # Only allow merges from betafix/*, release/* branches with any release (including release candidates)
    # or 'PRmerge/production' branch (for merge-back after production release)
    if [[ ! $HEAD =~ ^betafix/ ]] && [[ ! $HEAD =~ ^release/v?[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+-rc[[:digit:]]+$ ]] \
        && [[ ! $HEAD == 'PRmerge/production' ]]; then
        echo "Failure: branch '${HEAD}' not allowed to merge in '${BASE}'"
        false
    fi
    # If HEAD is a release/* branch, ensure the release does not yet exist
    if [[ $HEAD =~ ^release/v?[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+-rc[[:digit:]]+$ ]]; then
        RELEASE=$(bash ${SCRIPT_DIR}/parse-release-branch-name.sh $HEAD)
        if [[ ! -z $(git tag -l ${RELEASE}) ]]; then
            echo "Failure: release '${RELEASE}' already exists."
            false
        fi
    fi
elif [[ $BASE == 'alpha' ]]; then
    # Prevent direct merges from beta or production, as GH PR merge behavior merges BASE into HEAD first,
    # before merging HEAD back into BASE (which would conflate the different environment branches)
    if [[ $HEAD == 'beta' ]] || [[ $HEAD == 'production' ]]; then
        echo "Failure: branch '${HEAD}' not allowed to merge in '${BASE}'"
        false
    fi
fi
