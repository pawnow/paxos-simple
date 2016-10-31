package pl.edu.agh.iosr.controller

import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class UtilsControllerTest extends Specification {

    public static final String STATUS_URL = '/utils/status'
    public static final String ONLINE_URL = '/utils/online'
    public static final String OFFLINE_URL = '/utils/offline'
    def MockMvc mockMvc

    def setup() {
        mockMvc = standaloneSetup(new UtilsController()).build()
    }

    def testReturnOnlineStatusByDefault() {
        when: 'rest status url is hit'
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response

        then: 'should return correct status'
        response.status == OK.value()
        response.contentAsString == 'true'
    }

    def testShouldTurnStatusToOffline() {
        when: 'rest status url is hit'
        def statusChangeResponse = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response

        then: 'should return correct status'
        statusChangeResponse.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'false'
    }

    def testShouldTurnStatusToOfflineAndThenOnline() {
        when: 'rest status url is hit'
        def statusChangeResponseOffline = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def statusChangeResponseOnline = mockMvc.perform(post(ONLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response

        then: 'should return correct status'
        statusChangeResponseOffline.status == OK.value()
        statusChangeResponseOnline.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'true'
    }

    def testShouldNotChangeStatusIfItIsOnlineWhenPostOnline() {
        when: 'rest status url is hit'
        def statusChangeResponseOnline = mockMvc.perform(post(ONLINE_URL)).andReturn().response
        def statusChangeResponseOnline2 = mockMvc.perform(post(ONLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response

        then: 'should return correct status'
        statusChangeResponseOnline.status == OK.value()
        statusChangeResponseOnline2.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'true'
    }

    def testShouldNotChangeStatusIfItIsOfflineWhenPostOffline() {
        when: 'rest status url is hit'
        def statusChangeResponseOffline = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def statusChangeResponseOffline2 = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response

        then: 'should return correct status'
        statusChangeResponseOffline.status == OK.value()
        statusChangeResponseOffline2.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'false'
    }

}
