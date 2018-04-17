package com.myretail

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ProductsServiceSpec extends Specification implements ServiceUnitTest<ProductsService> {

    def setup() {
    }

    def cleanup() {
    }

    void "getProduct() returns mocked data"() {
        expect: "mocked data"
        service.getProduct(1) == [id: 1, name: 'Test', price: 123.45]
        service.getProduct(42) == [id: 42, name: 'Test', price: 123.45]
    }

    void "updatePrice() returns mocked data"() {
        expect: "mocked data"
        service.updatePrice(1, 0.00) == [id: 1, name: 'Test', price: 123.45]
        service.updatePrice(42, 100.00) == [id: 42, name: 'Test', price: 123.45]
    }
}
