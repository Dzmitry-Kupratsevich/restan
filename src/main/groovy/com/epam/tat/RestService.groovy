package com.epam.tat

import groovyx.net.http.ContentType

class RestService {

    RestClient restClient = new RestClient()
    def path = '/v1/reports/struct_agg'

    def restClientGet(json, status = "Success", description = null){

        def client = restClient.getRestClient()
        client.handler.failure = client.handler.success
        def res = client.post(path: path, contentType: ContentType.JSON, body: json)
        assert verifyResponseStatus(res, status, json)
        //assert verifyResponseDescription(client, description)
        res
    }

    static boolean verifyResponseStatus(res, String status, payload) {
        assert payload && (res.data.metadata.status == status)
        true
    }

//    static boolean verifyResponseDescription(res, description){
//        description = res.metadata.description
//        true
//    }
}
