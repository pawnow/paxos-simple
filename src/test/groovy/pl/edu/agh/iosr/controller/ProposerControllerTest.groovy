package pl.edu.agh.iosr.controller

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import pl.edu.agh.iosr.service.LeaderService
import pl.edu.agh.iosr.utils.ApplicationEndpoints
import spock.lang.Specification

import static org.mockito.Matchers.any
import static org.mockito.Mockito.when
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class ProposerControllerTest extends Specification {

    public static final String PROPOSER_URL = ApplicationEndpoints.PROPOSER_URL.getEndpoint()
    @Mock
    LeaderService leaderService

    @InjectMocks
    ProposerController controllerUnderTest;

    def MockMvc mockMvc

    def setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(controllerUnderTest).build()
    }

    def testProposerReturnEmptyProposal() {
        given:
        when(leaderService.isLeader(any())).thenReturn(true)

        when: 'rest propose url is hit'
        def response = mockMvc.perform(get(PROPOSER_URL)).andReturn().response

        then: 'proposer controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == '{"id":0,"value":null}'
    }

    def testProposerReturnNullIfIsNotLeader() {
        given:
        when(leaderService.isLeader(any())).thenReturn(false)

        when: 'rest propose url is hit'
        def response = mockMvc.perform(get(PROPOSER_URL)).andReturn().response

        then: 'proposer controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == ''
    }

}
