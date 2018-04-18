package com.myretail

class ProductsController {
    static responseFormats = ['json']

    ProductsService productsService

    def index() {
        [products: []]
    }

    def show(long id) {
        // TODO: handle invalid id -> 404

        [product: productsService.getProduct(id)]
    }

    def update(long id) {
        // TODO: handle invalid id -> 404

        def updates = request?.JSON
        def price = updates?.current_price?.value ?: updates?.price
        // TODO: handle no price specified/invalid payload -> 400

        render view: 'show', model: [product: productsService.updatePrice(id, price)]
    }

    def notFound(IllegalArgumentException e) {
        render view: '/errors/notFound', model: [error: e]
    }
}
