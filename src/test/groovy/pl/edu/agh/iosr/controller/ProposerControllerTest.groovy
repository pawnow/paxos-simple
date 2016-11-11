package pl.edu.agh.iosr.controller

import com.google.gson.Gson
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
import pl.edu.agh.iosr.service.QuorumProviderService
import pl.edu.agh.iosr.utils.ApplicationEndpoints
import spock.lang.Specification

import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class ProposerControllerTest extends Specification {

    public static final String PROPOSER_PROPOSE_URL = ApplicationEndpoints.PROPOSER_PROPOSE_URL.getEndpoint() + "?value=5"
    public static final String PROPOSER_ACCEPT_URL = ApplicationEndpoints.PROPOSER_ACCEPT_URL.getEndpoint()

    @Mock
    LeaderService leaderService

    @Mock
    ProposerService proposerService

    @Mock
    NodesRegistryRepository nodesRegistryRepository

    @Mock
    QuorumProviderService quorumProviderService

    @InjectMocks
    ProposerController controllerUnderTest

    def Gson gson = new Gson()

    def MockMvc mockMvc

    def setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(controllerUnderTest).build()
    }

    def testProposerReturnNullIfIsNotLeader() {
        given:
        when(leaderService.isLeader(any())).thenReturn(false)

        when: 'rest propose url is hit'
        def response = mockMvc.perform(get(PROPOSER_PROPOSE_URL)).andReturn().response

        then: 'proposer controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == ''
    }

    def testProposeQuorum() {
        given:
        Node firstNode = Node.builder().id(1).nodeUrl("1001").build();
        Node secondNode = Node.builder().id(2).nodeUrl("1002").build();
        Node thirdNode = Node.builder().id(3).nodeUrl("1003").build();
        def nodes = Arrays.asList(firstNode, secondNode, thirdNode)
        def quorum = new HashMap<Node, Boolean>()
        quorum.put(firstNode, false)
        quorum.put(thirdNode, false)

        when(leaderService.isLeader(any(String.class))).thenReturn(true)
        when(nodesRegistryRepository.findAll()).thenReturn(nodes)
        when(leaderService.getServerId(eq(nodes), any(String.class))).thenReturn(Optional.of(1l))
        when(quorumProviderService.getMinimalQuorum()).thenReturn(quorum)
        when(proposerService.generateProposalId(any(String.class))).thenReturn(7l)

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(PROPOSER_PROPOSE_URL)).andReturn().response
        verify(proposerService).sendProposalToQuorum(eq(quorum), any(Proposal.class))

        then: 'proposer controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == '{"id":7,"key":"key-7","value":null,"server":"http://localhost/proposer/propose","highestAcceptedProposalId":null}'
    }

    def testAcceptWithHighedIdFromQuorum() {
        given:
        Node firstNode = Node.builder().id(1).nodeUrl("1001").build();
        Node secondNode = Node.builder().id(2).nodeUrl("1002").build();
        Node thirdNode = Node.builder().id(3).nodeUrl("1003").build();
        def nodes = Arrays.asList(firstNode, secondNode, thirdNode)
        def quorum = new HashMap<Node, Boolean>()
        quorum.put(firstNode, false)
        quorum.put(thirdNode, false)

        Proposal proposalFirst = Proposal.builder().id(5).key("key-5").value(8).server("1001").highestAcceptedProposalId(3).build()
        String proposalJsonFirst = gson.toJson(proposalFirst)
        Proposal proposalThird = Proposal.builder().id(5).key("key-5").value(6).server("1003").highestAcceptedProposalId(1).build()
        String proposalJsonThird = gson.toJson(proposalThird)

        when(leaderService.isLeader(any(String.class))).thenReturn(true)
        when(nodesRegistryRepository.findAll()).thenReturn(nodes)
        when(leaderService.getServerId(eq(nodes), any(String.class))).thenReturn(Optional.of(1l))
        when(quorumProviderService.getMinimalQuorum()).thenReturn(quorum)
        when(proposerService.checkForQuorum(any())).thenReturn(false).thenReturn(true)
        when(proposerService.generateProposalId(any(String.class))).thenReturn(5l)

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(PROPOSER_PROPOSE_URL)).andReturn().response
        def responseAccept = mockMvc.perform(post(PROPOSER_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJsonFirst)).andReturn().response
        def responseAccept2 = mockMvc.perform(post(PROPOSER_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJsonThird)).andReturn().response
        verify(proposerService).sendAccept(any(), any())

        then: 'proposer controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == '{"id":5,"key":"key-5","value":null,"server":"http://localhost/proposer/propose","highestAcceptedProposalId":null}'
        responseAccept.status == OK.value()
        responseAccept.contentAsString == ''
        responseAccept2.status == OK.value()
        responseAccept2.contentAsString == '{"id":5,"key":"key-5","value":8,"server":"1001","highestAcceptedProposalId":3}'
    }

    def testAcceptWithWithProposalValueFromClient() {
        given:
        Node firstNode = Node.builder().id(1).nodeUrl("1001").build();
        Node secondNode = Node.builder().id(2).nodeUrl("1002").build();
        Node thirdNode = Node.builder().id(3).nodeUrl("1003").build();
        def nodes = Arrays.asList(firstNode, secondNode, thirdNode)
        def quorum = new HashMap<Node, Boolean>()
        quorum.put(firstNode, false)
        quorum.put(thirdNode, false)

        Proposal proposalFirst = Proposal.builder().id(5).key("key-5").server("1001").build()
        String proposalJsonFirst = gson.toJson(proposalFirst)
        Proposal proposalThird = Proposal.builder().id(5).key("key-5").server("1003").build()
        String proposalJsonThird = gson.toJson(proposalThird)

        when(leaderService.isLeader(any(String.class))).thenReturn(true)
        when(nodesRegistryRepository.findAll()).thenReturn(nodes)
        when(leaderService.getServerId(eq(nodes), any(String.class))).thenReturn(Optional.of(1l))
        when(quorumProviderService.getMinimalQuorum()).thenReturn(quorum)
        when(proposerService.checkForQuorum(any())).thenReturn(false).thenReturn(true)
        when(proposerService.generateProposalId(any(String.class))).thenReturn(5l)

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(PROPOSER_PROPOSE_URL)).andReturn().response
        def responseAccept = mockMvc.perform(post(PROPOSER_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJsonFirst)).andReturn().response
        def responseAccept2 = mockMvc.perform(post(PROPOSER_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJsonThird)).andReturn().response
        verify(proposerService).sendAccept(any(), any())

        then: 'proposer controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == '{"id":5,"key":"key-5","value":null,"server":"http://localhost/proposer/propose","highestAcceptedProposalId":null}'
        responseAccept.status == OK.value()
        responseAccept.contentAsString == ''
        responseAccept2.status == OK.value()
        //tu coś nie bangla, key = null
        responseAccept2.contentAsString == '{"id":5,"key":"key-5","value":5,"server":null,"highestAcceptedProposalId":null}'
    }

}
