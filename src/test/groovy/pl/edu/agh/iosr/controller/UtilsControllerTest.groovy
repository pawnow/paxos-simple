package pl.edu.agh.iosr.controller

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import pl.edu.agh.iosr.service.FaultService
import pl.edu.agh.iosr.service.LeaderService
import spock.lang.Specification

import static org.mockito.Matchers.any
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class UtilsControllerTest extends Specification {

    public static final String STATUS_URL = '/utils/status'
    public static final String ONLINE_URL = '/utils/online'
    public static final String OFFLINE_URL = '/utils/offline'
    def MockMvc mockMvc

    @Mock
    FaultService faultService

    @Mock
    LeaderService leaderService

    @InjectMocks
    UtilsController controllerUnderTest;

    def setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(controllerUnderTest).build()
    }

    def testReturnOnlineStatusByDefault() {
        given:
        when(faultService.isDown()).thenReturn(false)

        when: 'rest status url is hit'
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response
        verify(faultService, times(1)).isDown();

        then: 'should return correct status'
        response.status == OK.value()
        response.contentAsString == 'true'
    }

    def testShouldTurnStatusToOffline() {
        given:
        when(faultService.isDown()).thenReturn(true)

        when: 'rest status url is hit'
        def statusChangeResponse = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response
        verify(faultService).destroyNode();
        verify(faultService).isDown();

        then: 'should return correct status'
        statusChangeResponse.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'false'
    }

    def testShouldTurnStatusToOfflineAndThenOnline() {
        given:
        when(faultService.isDown()).thenReturn(false)

        when: 'rest status url is hit'
        def statusChangeResponseOffline = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def statusChangeResponseOnline = mockMvc.perform(post(ONLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response
        verify(faultService, times(1)).destroyNode();
        verify(faultService, times(1)).repareNode();
        verify(faultService, times(1)).isDown();

        then: 'should return correct status'
        statusChangeResponseOffline.status == OK.value()
        statusChangeResponseOnline.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'true'
    }

    def testShouldNotChangeStatusIfItIsOnlineWhenPostOnline() {
        given:
        when(faultService.isDown()).thenReturn(false)

        when: 'rest status url is hit'
        def statusChangeResponseOnline = mockMvc.perform(post(ONLINE_URL)).andReturn().response
        def statusChangeResponseOnline2 = mockMvc.perform(post(ONLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response
        verify(faultService, times(2)).repareNode();
        verify(faultService, times(1)).isDown();

        then: 'should return correct status'
        statusChangeResponseOnline.status == OK.value()
        statusChangeResponseOnline2.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'true'
    }

    def testShouldNotChangeStatusIfItIsOfflineWhenPostOffline() {
        given:
        when(faultService.isDown()).thenReturn(true)

        when: 'rest status url is hit'
        def statusChangeResponseOffline = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def statusChangeResponseOffline2 = mockMvc.perform(post(OFFLINE_URL)).andReturn().response
        def response = mockMvc.perform(get(STATUS_URL)).andReturn().response
        verify(faultService, times(2)).destroyNode();
        verify(faultService, times(1)).isDown();

        then: 'should return correct status'
        statusChangeResponseOffline.status == OK.value()
        statusChangeResponseOffline2.status == OK.value()
        response.status == OK.value()
        response.contentAsString == 'false'
    }

}
