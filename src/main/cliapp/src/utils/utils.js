export function returnSorted(event, originalSort){
    
    let found = false;
    let replace = false;
    let newSort = [...originalSort];

    console.log(event);
    console.log(newSort);
    if(event.multiSortMeta.length > 0){
        newSort.forEach((o) => {
            if (o.field === event.multiSortMeta[0].field) {
                if(o.order === event.multiSortMeta[0].order){
                    replace = true;
                    found = true;
                } else{
                    o.order = event.multiSortMeta[0].order;
                    found = true;
                }
            }
        });
    }else {
        newSort = [];
    }

    if (!found) {
        return newSort.concat(event.multiSortMeta);
    } else {
        if(replace){
            return event.multiSortMeta;
        }else{
            return newSort;
        }
    }

}

