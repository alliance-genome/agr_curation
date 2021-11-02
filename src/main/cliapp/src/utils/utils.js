export function returnSorted(event, originalSort){
    let found = false;
    const newSort = [...originalSort];

    newSort.forEach((o) => {
        if (o.field === event.multiSortMeta[0].field) {
            o.order = event.multiSortMeta[0].order;
            found = true;
        }
    });

    if (!found) {
        return newSort.concat(event.multiSortMeta);
    } else {
        return newSort;
    }

}