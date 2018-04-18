package com.myretail

import com.myretail.redsky.RedSkyApi
import com.myretail.redsky.RedSkyProduct
import org.dizitart.no2.Document
import org.dizitart.no2.filters.Filters
import org.springframework.beans.factory.annotation.Autowired
import retrofit2.HttpException

import java.util.concurrent.ExecutionException

import static org.dizitart.no2.Document.createDocument

class ProductsService {
    static transactional = false

    protected static
    final String EXCLUDES = ['taxonomy', 'promotion', 'bulk_ship', 'rating_and_review_reviews',
                             'rating_and_review_statistics', 'question_answer_statistics'].join(',')

    @Autowired(required = false)
    NitriteDb nitriteDb

    @Autowired(required = false)
    RedSkyApi redSkyApi

    Map getProduct(long id) {
        Map redSky = fetchRedSkyProduct(id)
        BigDecimal redskyPrice = redSky?.price

        buildProduct(redSky, lookupLocalPrice(id, redskyPrice))
    }

    Map updatePrice(long id, BigDecimal price) {
        Map redSky = fetchRedSkyProduct(id)
        if (redSky) {
            Document local = lookupLocalDocument(id)
            if (local) {
                local.price = price
                nitriteDb.products.update(local)
            } else {
                nitriteDb.products.insert(createDocument('id', id).put('price', price))
            }
        }

        buildProduct(redSky, price)
    }

    protected Map buildProduct(Map redSky, BigDecimal price) {
        [
                id   : redSky?.id,
                name : redSky?.name,
                price: price
        ]
    }

    // NB: could probably cache this since only using name, which is unlikely to change for an id
    protected Map fetchRedSkyProduct(long id) {
        try {
            RedSkyProduct redSky = redSkyApi.get(id, EXCLUDES)?.get()?.product

            // extract our fields
            long tcin = Long.valueOf(redSky?.item?.tcin)
            String name = redSky?.item?.productDescription?.title
            BigDecimal price = redSky?.price?.listPrice?.price

            [
                    id   : tcin,
                    name : name,
                    price: price
            ]
        } catch (ExecutionException e) {
            def cause = e.cause
            if (cause instanceof HttpException && cause.code() == 404) {
                throw new IllegalArgumentException("Invalid id: ${id}")
            } else {
                throw cause
            }
        }
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
