package com.myretail

class ProductsController {
    static responseFormats = ['json']

    def index() {
        [products: []]
    }

    def show(long id) {
        [product: [id: id, name: 'Test', price: 123.45]]
    }

    def update(long id) {
        // TODO: update price
        render view: 'show', model: [product: [id: id, name: 'Test', price: 123.45]]
    }
}
