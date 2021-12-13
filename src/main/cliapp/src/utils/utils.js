export function returnSorted(event, originalSort) {

    let found = false;
    let replace = false;
    let newSort = [...originalSort];

    // console.log(event);
    // console.log(newSort);
    if (event.multiSortMeta.length > 0) {
        newSort.forEach((o) => {
            if (o.field === event.multiSortMeta[0].field) {
                if (o.order === event.multiSortMeta[0].order) {
                    replace = true;
                    found = true;
                } else {
                    o.order = event.multiSortMeta[0].order;
                    found = true;
                }
            }
        });
    } else {
        newSort = [];
    }

    if (!found) {
        return newSort.concat(event.multiSortMeta);
    } else {
        if (replace) {
            return event.multiSortMeta;
        } else {
            return newSort;
        }
    }

}

export function getSecondarySorts(sorts) {
    const newSorts = [];
    sorts.forEach(sort => {
        const secondarySorts = findSecondarySortFields(sort.field, sort.order);
        newSorts.push(sort);
        secondarySorts.forEach(secondarySort => {
            if (secondarySort.field) {
                newSorts.push(secondarySort);
            }
        })
    })

    return newSorts;
}

export function findSecondarySortFields(sortField, sortOrder) {
    let newSortFields = [];
    let newSorts = [];

    switch (sortField) {
        case 'object.name':
            newSortFields = ['object.curie', 'object.namespace'];
            break;
        case 'with.symbol':
            newSortFields = ['with.curie'];
            break;
        default:
            newSortFields = [null];
            break;
    }

    newSortFields.forEach(field => {
        let newSort = {}
        newSort["field"] = field;
        newSort["order"] = sortOrder;
        newSorts.push(newSort);
    })

    return newSorts;

}
