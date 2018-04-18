package com.myretail.redsky

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

@CompileStatic
@TupleConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class RedSkyProduct {
    RedSkyItemGroup item
    RedSkyPriceGroup price
}
