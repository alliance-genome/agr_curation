import axios from 'axios';

export class GeneService {

    getGenes(limit, page) {
        //console.log("Limit: " + limit + " Page: " + page);
        return axios.get('http://localhost:8080/api/gene/all?limit=' + limit + '&page=' + page).then(res => res.data);
    }

    getProducts() {
        return axios.get('assets/demo/data/products.json').then(res => res.data.data);
    }

    getProductsWithOrdersSmall() {
        return axios.get('assets/demo/data/products-orders-small.json').then(res => res.data.data);
    }
}
