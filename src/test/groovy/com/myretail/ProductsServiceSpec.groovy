package com.myretail

import grails.testing.services.ServiceUnitTest
import org.dizitart.no2.Document
import spock.lang.Specification

import static org.dizitart.no2.Document.createDocument

class ProductsServiceSpec extends Specification implements ServiceUnitTest<ProductsService> {

    void "getProduct() combines data from Redsky API and local data store"() {
        setup:
        initializeNitriteDb([42: 9.99])

        when: "non-existent id"
        def product1 = service.getProduct(1)

        then: "uses mocked price"
        [id: 1, name: 'Test', price: 19.98] == product1

        when: "existing id"
        def product2 = service.getProduct(42)

        then:
        [id: 42, name: 'Test', price: 9.99] == product2
    }

    void "updatePrice sets the price in the local data store"() {
        setup:
        initializeNitriteDb()

        when: "no local price for id"
        def product1 = service.getProduct(1)

        then:
        [id: 1, name: 'Test', price: 19.98] == product1

        when: "set price for id"
        def product2 = service.updatePrice(1, 9.99)

        then:
        [id: 1, name: 'Test', price: 9.99] == product2
    }

    void "fetchRedskyProduct() returns mocked data"() {
        expect: "mocked data"
        service.fetchRedskyProduct(1) == [id: 1, name: 'Test', price: 19.98]
        service.fetchRedskyProduct(42) == [id: 42, name: 'Test', price: 19.98]
    }

    void "buildProduct() builds properly formatted Map"() {
        expect:
        service.buildProduct([id: 1234, name: 'Test'], 123.45) == [id: 1234, name: 'Test', price: 123.45]
    }

    void "lookupLocalDocument() returns null for ids that don't exist"() {
        setup:
        initializeNitriteDb()

        expect: "null when id does not exists"
        null == service.lookupLocalDocument(12345)
    }

    void "lookupLocalDocument() returns a document for ids that exist"() {
        setup: "initialize in-memory NoSQL database"
        initializeNitriteDb([42: 19.98])

        when: "lookup with id that exists"
        def document = service.lookupLocalDocument(42)

        then: "document exists"
        document?.price == 19.98
    }

    void "lookupLocalPrice() handles non-existing ids"() {
        setup: "initialize in-memory NoSQL database"
        initializeNitriteDb([42: 19.98])

        expect: "default price when id does not exist"
        9.99 == service.lookupLocalPrice(12345, 9.99)
    }

    void "lookupLocalPrice() handles existing ids"() {
        setup: "initialize in-memory NoSQL database"
        initializeNitriteDb([42: 19.99])

        expect: "stored price when id does exist"
        19.99 == service.lookupLocalPrice(42, null)
    }

    protected initializeNitriteDb(Map products = [:]) {
        service.nitriteDb = new NitriteDb()
        service.nitriteDb.init()

        def collection = service.nitriteDb.products

        products.each { id, price ->
            collection.insert(createDocument('id', (id as long)).put('price', price))
        }
    }
}
