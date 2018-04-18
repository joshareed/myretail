package com.myretail

class ProductsController {
    static responseFormats = ['json']

    ProductsService productsService

    def index() {
        [products: []]
    }

    def show(Long id) {
        // handle malformed ids
        if (!id) throw new NotFoundException(params?.id)

        [product: productsService.getProduct(id)]
    }

    def update(Long id) {
        // handle malformed ids
        if (!id) throw new NotFoundException(params?.id)

        // parse the payload
        def updates = request?.JSON
        if (!updates?.id) throw new BadRequestException("Missing required parameter 'id'")
        if (id != updates?.id) throw new BadRequestException("URL and payload ids don't match (${id} != ${updates?.id})")

        def price = updates?.current_price?.value ?: updates?.price
        if (!price) throw new BadRequestException("Missing required parameter 'price'")

        render view: 'show', model: [product: productsService.updatePrice(id, new BigDecimal(price.toString()))]
    }

    // error handling
    def notFound(NotFoundException e) {
        render view: '/errors/notFound', model: [error: e]
    }

    def badRequest(BadRequestException e) {
        render view: '/errors/badRequest', model: [error: e]
    }
}
