package com.myretail.redsky

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
class RedSkyDescription {
    String title
}
