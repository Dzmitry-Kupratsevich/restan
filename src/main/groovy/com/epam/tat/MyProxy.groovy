package com.epam.tat

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.CharsetUtil
import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersAdapter
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap

class MyProxy {

    def firstRequest = '''{"size":0,"query":{"filtered":{"filter":{"and":[{"or":[{"range":{"_doc_time":{"gte":"20150404000000","lte":"20150405235959"}}}]}]}}},"aggs":{"director":{"terms":{"field":"director","size":2,"shard_size":30,"collect_mode":"breadth_first"}}}}'''
    def firstResponse = '''{"took":8,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":21408,"max_score":0.0,"hits":[]},"aggregations":{"director":{"doc_count_error_upper_bound":0,"sum_other_doc_count":642,"buckets":[{"key":"tim hall","doc_count":666},{"key":"michele vercimak","doc_count":333}]}}}'''
    def secondRequest = '''{"size":0,"query":{"filtered":{"filter":{"and":[{"or":[{"range":{"_doc_time":{"gte":"20150404000000","lte":"20150405235959"}}}]}]}}},"aggs":{"director":{"terms":{"field":"director","size":2,"shard_size":30,"collect_mode":"breadth_first","include":["michele vercimak","tim hall"]}}}}'''
    def secondResponse = '''{"took":9,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":21408,"max_score":0.0,"hits":[]},"aggregations":{"director":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0,"buckets":[{"key":"pavel pantseleyeu","doc_count":666},{"key":"uladzimir shabetnik","doc_count":333}]}}}'''
    def thirdsRequest = '''{"size":0,"query":{"filtered":{"filter":{"and":[{"or":[{"range":{"_doc_time":{"gte":"20150404000000","lte":"20150405235959"}}}]}]}}},"aggs":{"director":{"terms":{"field":"director","size":2,"shard_size":30,"collect_mode":"breadth_first","exclude":[""]}}}}'''
    def forthRequest = '''{"size":0,"query":{"filtered":{"filter":{"and":[{"or":[{"range":{"_doc_time":{"gte":"20140801000000","lte":"20150805235959"}}}]}]}}},"aggs":{"max_metric_doc":{"max":{"field":"num_agent_rating"}}}},"variant":"document","index":"1$4036746"}'''
    def forthResponse = '''{"took":1147,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":9522231,"max_score":0.0,"hits":[]},"aggregations":{"_verbatimtype":{"doc_count_error_upper_bound":0,"sum_other_doc_count":3340,"buckets":[{"key":"comment","doc_count":2908178,"_doc_count":{"value":1265760}},{"key":"q_mbr_comments","doc_count":2146688,"_doc_count":{"value":1098409}},{"key":"comment_agent","doc_count":1025812,"_doc_count":{"value":639903}},{"key":"no_verbatim_text","doc_count":836692,"_doc_count":{"value":834815}},{"key":"q_mbr_comments : reason for rating - what is the reason for your","doc_count":596165,"_doc_count":{"value":287624}},{"key":"q_mso_agent_reason","doc_count":558406,"_doc_count":{"value":363825}},{"key":"q_mbr_comments : comment","doc_count":230463,"_doc_count":{"value":152238}},{"key":"q_mso_agent_reason : reason for agent rating - what is the reaso","doc_count":228578,"_doc_count":{"value":159274}},{"key":"please take a minute to provide your comments. if this is related to an issue please provide any ava","doc_count":187171,"_doc_count":{"value":33496}},{"key":"q_mbr_comments : comment - what is the reason for your rating?","doc_count":154981,"_doc_count":{"value":111952}},{"key":"q_mbr_comments : reason for rating","doc_count":127631,"_doc_count":{"value":83750}},{"key":"q_mbr_comments : reason for nps - what is the reason for your ra","doc_count":120013,"_doc_count":{"value":66040}},{"key":"reason_for_rating : reason for rating - what is the reason for y","doc_count":110032,"_doc_count":{"value":80839}},{"key":"q_mbr_comments : reason for nps","doc_count":99319,"_doc_count":{"value":50132}},{"key":"verbatim","doc_count":90609,"_doc_count":{"value":49033}},{"key":"q_online_nps_reason","doc_count":34256,"_doc_count":{"value":20720}},{"key":"notes","doc_count":25922,"_doc_count":{"value":1121}},{"key":"content","doc_count":9187,"_doc_count":{"value":4461}},{"key":"what is the reason for your rating?","doc_count":6942,"_doc_count":{"value":2442}},{"key":"contact request verbatim","doc_count":6858,"_doc_count":{"value":3271}},{"key":"what is the reason for your rating of this interaction?","doc_count":3404,"_doc_count":{"value":1928}},{"key":"problem: other","doc_count":3301,"_doc_count":{"value":1955}},{"key":"hs_describe problem with your prodtype","doc_count":2797,"_doc_count":{"value":2642}},{"key":"hs_reason choose company to repair","doc_count":2694,"_doc_count":{"value":2468}},{"key":"other","doc_count":984,"_doc_count":{"value":730}},{"key":"pharmacy","doc_count":462,"_doc_count":{"value":445}},{"key":"comment: other","doc_count":391,"_doc_count":{"value":244}},{"key":"othercomments","doc_count":376,"_doc_count":{"value":182}},{"key":"improveother","doc_count":303,"_doc_count":{"value":246}},{"key":"grocery","doc_count":276,"_doc_count":{"value":267}}]}}}'''

    def filePath = "D:\\file.json"
    def currentTestFilePath = "D:\\currentTest.json"
    File currentFile = new File(currentTestFilePath)
    File file = new File(filePath)
    def isEmptyFile = false
    def contentFile = Collections.synchronizedList(new ArrayList())
    def contentCurrentFile = Collections.synchronizedList(new ArrayList())
    def request
    def out = []
    LinkedHashMap mocksMap = new LinkedHashMap()
    LinkedHashMap current = new LinkedHashMap()
    HttpResponse response
    LinkedHashMap forCheckRequest = new LinkedHashMap()
    def requestInMocks = false
    ObjectMapper mapper = new ObjectMapper()
    LinkedHashMap query = new LinkedHashMap()

        HttpProxyServer server = DefaultHttpProxyServer.bootstrap()
                .withPort(8081)
                .withFiltersSource(new HttpFiltersSourceAdapter() {

                    @Override
                    int getMaximumResponseBufferSizeInBytes() {
                        return 10*1024*1024
                    }
                    @Override
                    int getMaximumRequestBufferSizeInBytes() {
                        return 10*1024*1024
                    }
                    @Override
                    HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        new HttpFiltersAdapter(originalRequest) {

                            @Override
                            HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                if (httpObject instanceof FullHttpRequest) {
                                    FullHttpRequest httpRequest = (FullHttpRequest) httpObject
                                    query.put("query", (httpRequest.content().toString(Charset.defaultCharset())))
                                    request = (httpRequest.content().toString(Charset.defaultCharset()))
                                    out.clear()
                                    if(!currentFile.createNewFile()) {
                                        if (!currentFile.readLines().isEmpty()) {
                                            contentCurrentFile = mapper.readValue(currentFile, ArrayList)
                                        }
                                    } else {
                                        currentFile.createNewFile()
                                    }
                                    if(!file.createNewFile()) {
                                        if (!file.readLines().isEmpty()) {
                                            contentFile = mapper.readValue(file, ArrayList)
                                            contentFile.each {
                                                mocksMap = new ObjectMapper().convertValue(it, LinkedHashMap)
                                                forCheckRequest.put(mocksMap.request, mocksMap.response)
                                            }
                                            forCheckRequest.keySet().each {
                                                if (it == request) {
                                                    requestInMocks = true
                                                } else {
                                                    requestInMocks = false
                                                }
                                            }
                                        } else {
                                            isEmptyFile = true
                                        }
                                    } else {
                                        file.createNewFile()
                                        isEmptyFile = true
                                    }
                                }
                                if (requestInMocks) {
                                    ByteBuf buffer = Unpooled.wrappedBuffer(forCheckRequest.get(request).getBytes("UTF-8"))
                                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer)
                                    HttpHeaders.setContentLength(response, buffer.readableBytes())
                                    HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "application/json")
                                    current.put(request, forCheckRequest.get(request))
                                    return response
                                } else {
                                    ((FullHttpRequest) httpObject).setUri("http://jsonplaceholder.typicode.com/posts")
                                    ((FullHttpRequest) httpObject).headers().remove("Host")
                                    ((FullHttpRequest) httpObject).headers().add("Host", "http://jsonplaceholder.typicode.com")
                                    ((FullHttpRequest) httpObject).headers().remove("Content-Type")
                                    ((FullHttpRequest) httpObject).headers().add("Content-Type", "application/json")
                                    ((FullHttpRequest) httpObject).headers().remove("CB_REQ_ID")
                                    println httpObject
                                    return null
                                }
                            }

                            @Override
                            HttpObject proxyToClientResponse(HttpObject httpObject) {
                                println "proxyToClient"
                                if (isEmptyFile) {
                                    println httpObject
                                    if (query.get("query") == firstRequest || query.get("query") == thirdsRequest) {
                                        ByteBuf buffer = Unpooled.wrappedBuffer(firstResponse.getBytes("UTF-8"))
                                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer)
                                        HttpHeaders.setContentLength(response, buffer.readableBytes())
                                        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "application/json")
                                        forCheckRequest.put(request, firstResponse)
                                        current.put(request, firstResponse)
                                    } else if (query.get("query") == secondRequest) {
                                        ByteBuf buffer = Unpooled.wrappedBuffer(secondResponse.getBytes("UTF-8"))
                                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer)
                                        HttpHeaders.setContentLength(response, buffer.readableBytes())
                                        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "application/json")
                                        forCheckRequest.put(request, secondResponse)
                                        current.put(request, secondResponse)

                                    }
                                } else {
                                    if (!requestInMocks) {
//                                        CompositeByteBuf compositeByteBuf = (CompositeByteBuf) ((FullHttpResponse) httpObject).content()
//                                        String temp = compositeByteBuf.toString(CharsetUtil.UTF_8)
                                        current.put(request, secondResponse)
                                        forCheckRequest.put(request, secondResponse)
                                        ByteBuf buffer = Unpooled.wrappedBuffer(secondResponse.getBytes("UTF-8"))
                                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer)
                                        HttpHeaders.setContentLength(response, buffer.readableBytes())
                                        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "application/json")

                                    }
                                }
                                forCheckRequest.each {
                                    out.add([
                                            request: it.key,
                                            response: it.value
                                            ])
                                }
                                current.each {
                                    contentCurrentFile.add([
                                            request: it.key,
                                            response: it.value
                                    ])
                                }
                                mapper.writeValue(file, out)
                                mapper.writeValue(currentFile, contentCurrentFile)
                                out.clear()

                                return response
                            }
                        }
                    }
        })
                .start()
}
