package com.epam.tat

import groovyx.net.http.RESTClient

class RestClient {

    def host = 'localhost'
    def port = '10010'

    RESTClient getRestClient() {
        return new RESTClient("http://$host:$port")
    }
}
