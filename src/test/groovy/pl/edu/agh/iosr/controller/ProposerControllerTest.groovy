package pl.edu.agh.iosr.controller

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.edu.agh.iosr.cdm.NodesRegistryRepository
import pl.edu.agh.iosr.cdm.Proposal
import pl.edu.agh.iosr.cdm.Node
import pl.edu.agh.iosr.service.LeaderService
import pl.edu.agh.iosr.service.ProposerService
import pl.edu.agh.iosr.utils.ApplicationEndpoints
import spock.lang.Specification

import static org.mockito.Matchers.any
import static org.mockito.Mockito.when
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class ProposerControllerTest extends Specification {

    public static final String PROPOSER_URL = ApplicationEndpoints.PROPOSER_URL.getEndpoint()
    public static final String ACCEPTOR_URL = ApplicationEndpoints.ACCEPTOR_URL.getEndpoint()

    @Mock
    LeaderService leaderService

    @Mock
    ProposerService proposerService

    @Mock
    NodesRegistryRepository nodesRegistryRepository

    @InjectMocks
    ProposerController controllerUnderTest

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

    def testAcceptWithNoQuorum() {
        given:
        Node firstNode = Node.builder().id(1).url("1001");
        Node secondNode = Node.builder().id(2).url("1002");
        Node thirdNode = Node.builder().id(3).url("1003");

        Proposal proposal = Proposal.builder().id(1).value(5).server("1001").build()
        String proposalJson = gson.toJson(proposal)

        when(nodesRegistryRepository .findAll()).thenReturn(Collections.singletonList(firstNode)).thenReturn(Arrays.asList(firstNode, secondNode, thirdNode))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(nodesRegistryRepository, times(1)).findAll();

        then: 'proposer controller should return ok status and appropriate value'

        response.status == OK.value()
        response.contentAsString == ''

    }

    def testAcceptWithQuorum() {
        given:
        Node firstNode = Node.builder().id(1).url("1001");
        Node secondNode = Node.builder().id(2).url("1002");
        Node thirdNode = Node.builder().id(3).url("1003");

        Proposal proposal = Proposal.builder().id(1).value(5).server("1001").build()
        Proposal secondProposal = Proposal.builder().id(2).value(10).server("1002").build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(nodesRegistryRepository .findAll()).thenReturn(Collections.singletonList(firstNode)).thenReturn(Arrays.asList(firstNode, secondNode, thirdNode))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(nodesRegistryRepository, times(1)).findAll();

        then: 'proposer controller should return ok status and appropriate value'

        response.status == OK.value()
        response.contentAsString == ''
        secondResponse.status == OK.value()
        secondResponse.contentAsString == '{"id":1,"value":10}'

    }

}
