package com.myretail

import groovy.transform.CompileStatic
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteCollection
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static org.dizitart.no2.IndexOptions.indexOptions
import static org.dizitart.no2.IndexType.Unique

@CompileStatic
@Component
class NitriteDb {
    private static final String PRODUCTS_COLLECTION = "products"

    protected Nitrite db

    @PostConstruct
    void init() {
        // create an in-memory database
        db = Nitrite.builder().openOrCreate()
        products?.createIndex("id", indexOptions(Unique))
    }

    NitriteCollection getProducts() {
        db?.getCollection(PRODUCTS_COLLECTION)
    }
}
