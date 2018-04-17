package com.myretail

import spock.lang.Specification

class NitriteDbSpec extends Specification {

    void "init() creates the in-memory database"() {
        setup:
        NitriteDb db = new NitriteDb()

        expect: "null before init()"
        null == db.db
        null == db.products

        when:
        db.init()

        then: "not null"
        null != db.db
        null != db.products
    }
}
