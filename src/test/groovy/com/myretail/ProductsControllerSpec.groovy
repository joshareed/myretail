package com.myretail

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ProductsControllerSpec extends Specification implements ControllerUnitTest<ProductsController> {

    def setup() {
    }

    def cleanup() {
    }

    void "index returns an empty list"() {
        when: "call index action"
        def result = controller.index()

        then: "always an empty list"
        result == [products: []]
    }

    void "show returns the product"() {
        when: "call show with id"
        def result = controller.show(12345)

        then:
        result == [product: [id: 12345, name: "Test", price: 123.45]]
    }
}