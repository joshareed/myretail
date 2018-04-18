package com.myretail

import com.myretail.redsky.*
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

import static com.myretail.ProductsService.EXCLUDES
import static org.dizitart.no2.Document.createDocument

class ProductsServiceSpec extends Specification implements ServiceUnitTest<ProductsService> {

    void "getProduct() combines data from Redsky API and local data store"() {
        setup:
        initializeNitriteDb([42: 9.99])

        and:
        service.redSkyApi = Mock(RedSkyApi)
        RedSkyApiResponse mockedData = buildRedSkyProduct(42, 'Item Name', 123.45)

        when: "existing id"
        def product1 = service.getProduct(42)

        then:
        1 * service.redSkyApi.get(42, EXCLUDES) >> CompletableFuture.completedFuture(mockedData)

        and: "RedSky metadata and local price"
        [id: 42, name: 'Item Name', price: 9.99] == product1
    }

    void "updatePrice sets the price in the local data store"() {
        setup:
        initializeNitriteDb()

        and:
        service.redSkyApi = Mock(RedSkyApi)
        RedSkyApiResponse mockedData = buildRedSkyProduct(42, 'Item Name', 123.45)

        when: "no local price for id"
        def product1 = service.getProduct(42)

        then:
        1 * service.redSkyApi.get(42, EXCLUDES) >> CompletableFuture.completedFuture(mockedData)

        and: "RedSky price is used"
        [id: 42, name: 'Item Name', price: 123.45] == product1

        when: "set price for id"
        def product2 = service.updatePrice(42, 9.99)

        then:
        1 * service.redSkyApi.get(42, EXCLUDES) >> CompletableFuture.completedFuture(mockedData)

        and: "local price is used"
        [id: 42, name: 'Item Name', price: 9.99] == product2
    }

    void "fetchRedSkyProduct() calls RedSkyApi bean"() {
        setup:
        service.redSkyApi = Mock(RedSkyApi)
        RedSkyApiResponse mockedData = buildRedSkyProduct(42, 'Item Name', 123.45)

        when:
        def result = service.fetchRedSkyProduct(42)

        then:
        1 * service.redSkyApi.get(42, EXCLUDES) >> CompletableFuture.completedFuture(mockedData)

        and:
        [id: 42, name: 'Item Name', price: 123.45] == result
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

    protected buildRedSkyProduct(long id, String name, BigDecimal price) {
        RedSkyProduct product = new RedSkyProduct(
                item: new RedSkyItemGroup(
                        tcin: id.toString(),
                        productDescription: new RedSkyDescription(name)
                ),
                price: new RedSkyPriceGroup(
                        listPrice: new RedSkyPrice(price)
                )
        )

        new RedSkyApiResponse(product)
    }
}
