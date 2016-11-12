package pl.edu.agh.iosr.controller

import com.google.gson.Gson
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.edu.agh.iosr.cdm.Proposal
import pl.edu.agh.iosr.cdm.ProposalRepository
import pl.edu.agh.iosr.utils.ApplicationEndpoints
import spock.lang.Specification
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class AcceptorControllerTest extends Specification {

    public static final String ACCEPTOR_PROPOSE_URL = ApplicationEndpoints.ACCEPTOR_PROPOSE_URL.getEndpoint()
    public static final String ACCEPTOR_ACCEPT_URL = ApplicationEndpoints.ACCEPTOR_ACCEPT_URL.getEndpoint()
    public static final String ACCEPTOR_PROPOSE_GET_URL = ApplicationEndpoints.ACCEPTOR_PROPOSE_URL.getEndpoint() + "/abc"
    def Gson gson = new Gson()

    @Mock
    ProposalRepository proposalRepository

    @InjectMocks
    AcceptorController controllerUnderTest;

    private MockMvc mockMvc;

    def setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(controllerUnderTest).build()
    }

    def testShouldReturnEmptyProposalWhenNoValueAccepted() {
        given:
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.emptyList())

        when: 'rest accept url is hit'
        def response = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response

        verify(proposalRepository).getProposalsForKey('abc')

        then: 'acceptor controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == '[]'
    }

    def testProposeShouldAcceptNewProposalAndReturnEmptyProposal() {
        given:
        Proposal proposal = Proposal.builder().id(1).key('abc').value(10).build()
        Gson gson = new Gson()
        String json = gson.toJson(proposal)
        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty())
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(1)).getByMaxIdForKey('abc');
        verify(proposalRepository, times(1)).getProposalsForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":1,"key":"abc","value":10,"server":null,"highestAcceptedProposalId":null}]'

    }

    def testProposeShouldNotAcceptNewProposalWhenPreviouslyAcceptedProposalWithHigherId() {
        given:
        Proposal proposal = Proposal.builder().id(2).key("abc").value(5).build()
        Proposal secondProposal = Proposal.builder().id(1).key("abc").value(10).build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty()).thenReturn(Optional.empty())
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal)).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(2)).getByMaxIdForKey('abc');
        verify(proposalRepository, times(2)).getProposalsForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":2,"key":"abc","value":5,"server":null,"highestAcceptedProposalId":null}]'
        secondResponse.status == OK.value()
        secondResponse.contentAsString == ''
        secondGetAcceptedValueResponse.status == OK.value()
        secondGetAcceptedValueResponse.contentAsString == '[{"id":2,"key":"abc","value":5,"server":null,"highestAcceptedProposalId":null}]'

    }

    def testProposeShouldNotAcceptNewProposalWhenPreviouslyAcceptedProposalWithSameId() {
        given:
        Proposal proposal = Proposal.builder().id(1).key("abc").value(7).build()
        Proposal secondProposal = Proposal.builder().id(1).key("abc").value(10).build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty()).thenReturn(Optional.empty())
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal)).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(2)).getByMaxIdForKey('abc');
        verify(proposalRepository, times(2)).getProposalsForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":1,"key":"abc","value":7,"server":null,"highestAcceptedProposalId":null}]'
        secondResponse.status == OK.value()
        secondResponse.contentAsString == ''
        secondGetAcceptedValueResponse.status == OK.value()
        secondGetAcceptedValueResponse.contentAsString == '[{"id":1,"key":"abc","value":7,"server":null,"highestAcceptedProposalId":null}]'
    }

    def testProposeShouldAcceptNewProposalWhenPreviouslyAcceptedProposalWithLowerId() {
        given:
        Proposal proposal = Proposal.builder().id(1).key("abc").value(20).build()
        Proposal secondProposal = Proposal.builder().id(2).key("abc").value(18).build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxIdForKey('abc')).thenReturn(null).thenReturn(proposal)
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal)).thenReturn(Arrays.asList(proposal, secondProposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_PROPOSE_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_PROPOSE_GET_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(1)).save(secondProposal);
        verify(proposalRepository, times(2)).getByMaxIdForKey('abc');
        verify(proposalRepository, times(2)).getProposalsForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":1,"key":"abc","value":20,"server":null,"highestAcceptedProposalId":null}]'
        secondResponse.status == OK.value()
        secondResponse.contentAsString == '{"id":1,"key":"abc","value":20,"server":null,"highestAcceptedProposalId":null}'
        secondGetAcceptedValueResponse.status == OK.value()
        secondGetAcceptedValueResponse.contentAsString == '[{"id":1,"key":"abc","value":20,"server":null,"highestAcceptedProposalId":null},{"id":2,"key":"abc","value":18,"server":null,"highestAcceptedProposalId":null}]'
    }

    def testAcceptShouldAcceptNewProposalAndReturnEmptyProposal() {
        given:
        Proposal proposal = Proposal.builder().id(1).key("abc").value(10).build()
        Gson gson = new Gson()
        String json = gson.toJson(proposal)
        when(proposalRepository.getByMaxIdForKey('abc')).thenReturn(null)
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().response

        verify(proposalRepository, times(1)).getByMaxIdForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
    }

    def testAcceptShouldNotAcceptNewProposalWhenPreviouslyAcceptedProposalWithHigherId() {
        given:
        Proposal proposal = Proposal.builder().id(2).value(5).key('abc').build()
        Proposal secondProposal = Proposal.builder().id(1).value(10).key('abc').build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxIdForKey('abc')).thenReturn(null).thenReturn(null)
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal)).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response

        verify(proposalRepository, times(2)).getByMaxIdForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        secondResponse.status == OK.value()
        secondResponse.contentAsString == ''

    }

    def testAcceptShouldNotAcceptNewProposalWhenPreviouslyAcceptedProposalWithSameId() {
        given:
        Proposal proposal = Proposal.builder().id(1).value(7).key('abc').build()
        Proposal secondProposal = Proposal.builder().id(1).value(10).key('abc').build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxIdForKey('abc')).thenReturn(null).thenReturn(null)
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal)).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response

        verify(proposalRepository, times(2)).getByMaxIdForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        secondResponse.status == OK.value()
        secondResponse.contentAsString == ''
    }

    def testAcceptShouldAcceptNewProposalWhenPreviouslyAcceptedProposalWithLowerId() {
        given:
        Proposal proposal = Proposal.builder().id(1).key("abc").value(20).build()
        Proposal secondProposal = Proposal.builder().id(2).value(18).key('abc').build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxIdForKey('abc')).thenReturn(null).thenReturn(proposal)
        when(proposalRepository.getProposalsForKey('abc')).thenReturn(Collections.singletonList(proposal)).thenReturn(Arrays.asList(proposal, secondProposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_ACCEPT_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response

        verify(proposalRepository, times(2)).getByMaxIdForKey('abc');

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        secondResponse.status == OK.value()
        secondResponse.contentAsString == '{"id":1,"key":"abc","value":20,"server":null,"highestAcceptedProposalId":null}'
    }

}
