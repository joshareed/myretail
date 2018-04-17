package com.myretail

import org.dizitart.no2.Document
import org.dizitart.no2.filters.Filters

import static org.dizitart.no2.Document.createDocument

class ProductsService {
    static transactional = false

    NitriteDb nitriteDb

    Map getProduct(long id) {
        Map redsky = fetchRedskyProduct(id)
        BigDecimal redskyPrice = redsky?.price

        buildProduct(redsky, lookupLocalPrice(id, redskyPrice))
    }

    Map updatePrice(long id, BigDecimal price) {
        Map redsky = fetchRedskyProduct(id)
        if (redsky) {
            Document local = lookupLocalDocument(id)
            if (local) {
                local.price = price
                nitriteDb.products.update(local)
            } else {
                nitriteDb.products.insert(createDocument('id', id).put('price', price))
            }
        }

        buildProduct(redsky, price)
    }

    protected Map buildProduct(Map redsky, BigDecimal price) {
        [
                id   : redsky?.id,
                name : redsky?.name,
                price: price
        ]
    }

    // NB: could probably cache this since only using name, which is unlikely to change for an id
    protected Map fetchRedskyProduct(long id) {
        // TODO: call Redsky API
        // TODO: handle invalid id
        [
                id   : id,
                name : 'Test',
                price: 19.98
        ]
    }

    protected BigDecimal lookupLocalPrice(long id, BigDecimal defaultPrice) {
        // query the collection and use the first document if it exists
        Document local = lookupLocalDocument(id)
        if (local?.price) {
            return local?.price
        }

        // otherwise return the default price
        defaultPrice
    }

    protected Document lookupLocalDocument(long id) {
        nitriteDb.products.find(Filters.eq('id', id)).find { true }
    }
}
