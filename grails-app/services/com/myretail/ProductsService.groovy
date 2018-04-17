package com.myretail

class ProductsService {
    static transactional = false

    Map getProduct(long id) {
        [id: id, name: 'Test', price: 123.45]
    }

    Map updatePrice(long id, BigDecimal price) {
        // TODO: set price in NoSQL database

        getProduct(id)
    }
}
