package com.epam.tat

import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class RestServiceTest {

    RestService restService = new RestService()
    MyProxy myProxy = new MyProxy()
    def server = myProxy.server
    def source




    private setSources() {
        String indexName = "1\$4036746"
        source = getSource(indexName) + getSchema() + getMetadata()
    }

    def static getSchema(projectId = 1) {
        [
                systemSchema: [
                        user        : "ss_testshm",
                        password    : "ss_testshm",
                        url         : "jdbc:postgresql://10.153.75.47:5432/postgres",
                        serverName  : "dev02",
                        databaseName: 'postgres',
                        portNumber  : 5432,
                        description : 'System schema'
                ],
                projectId   : projectId
        ]
    }

    def static getMetadata() {
        [
                projectName        : 'projectName_log',
                accountId          : 101,
                accountName        : 'accountName_log',
                contentProviderId  : 102,
                contentProviderName: 'contentProviderName_log',
                widgetId           : 103,
                widgetName         : 'widgetName_log',
                userLoggedInName   : 'userName_log',
                userLoggedInId     : 104,
                userRunAsName      : 'userName_log',
                userRunAsId        : 104,
                dashboardId        : 105,
                dashboardName      : 'dashboardName_log'
        ]
    }

    def static getSource(String indexName) {
        def es01_host_name = "dev02", es01_host = "localhost", es01_02_http_port = 8081, es01_02_transport_port = 8081
        [
                source: [
                        cluster: [
                                hosts      : [
                                        [
                                                http     : [
                                                        hostName   : es01_host_name,
                                                        hostAddress: es01_host,
                                                        port       : es01_02_http_port
                                                ],
                                                transport: [
                                                        hostName   : es01_host_name,
                                                        hostAddress: es01_host,
                                                        port       : es01_02_transport_port
                                                ]
                                        ]
                                ],
                                description: es01_host_name,
                                name       : es01_host_name
                        ],
                        index  : [
                                readAlias          : 'read_' + indexName - '_0',
                                writeAlias         : 'write_' + indexName - '_0',
                                dataIndexName      : indexName,
                                percolatorIndexName: '1\$p\$' + indexName[2..indexName.size() - 1]
                        ]
                ]
        ]
    }

    @BeforeMethod
    def setup() {
        setSources()
        myProxy.currentFile.text = ""
    }

    @Test
    void testGet() {
        def payload = [
                refinementBehavior : 0,
                attr: [
                        [name: 'director', size: 2],
                ],
                filter: [date: [startDate: 20150404, endDate: 20150405]]
        ] << source
        server
        def res = restService.restClientGet(payload)
        println(res.data.data)
    }

    @Test
    void "test2"() {
        def payload = [
                refinementBehavior: 0,
                attr: [
                        [name: '_verbatimtype', size: 2,],
                ],
                metrics: [
                        [type: 'max', name: 'num_agent_rating', displayName: 'max_metric_doc'],
                ],
                filter : [date: [startDate: 20140801, endDate: 20150805]]
        ] << source
        server
        def res = restService.restClientGet(payload)
        println(res.data.data)
    }

    @AfterMethod
    def cleanup() {
        if (server != null) {
            server.stop()
        }
    }

}
