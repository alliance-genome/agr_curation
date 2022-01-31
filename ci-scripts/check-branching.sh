#!/bin/bash
BASE=$1
HEAD=$2

if [[ $BASE == 'production' ]]; then
    # Only allow merges from prodfix/* and release/* branches with a full release (no release candidates)
    if [[ ! $HEAD =~ ^prodfix/ ]] && [[ ! $HEAD =~ ^release/v?[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+$ ]]; then
        echo "Failure: branch '${HEAD}' not allowed to merge in '${BASE}'"
        false
    fi
elif [[ $BASE == 'beta' ]]; then
    # Only allow merges from betafix/* and release/* branches with a any release (including release candidates)
    if [[ ! $HEAD =~ ^betafix/ ]] && [[ ! $HEAD =~ ^release/v?[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+ ]]; then
        echo "Failure: branch '${HEAD}' not allowed to merge in '${BASE}'"
        false
    fi
fi
