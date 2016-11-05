package pl.edu.agh.iosr.controller

import com.google.gson.Gson
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.edu.agh.iosr.cdm.Proposal
import pl.edu.agh.iosr.cdm.ProposalRepository
import spock.lang.Specification
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when
import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class AcceptorControllerTest extends Specification {

    public static final String ACCEPTOR_URL = '/acceptor/accept'
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
        when(proposalRepository.findAll()).thenReturn(Collections.emptyList())

        when: 'rest accept url is hit'
        def response = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(proposalRepository).findAll()

        then: 'acceptor controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == '[]'
    }

    def testShouldAcceptNewProposalAndReturnEmptyProposal() {
        given:
        Proposal proposal = Proposal.builder().id(1).value(10).build()
        Gson gson = new Gson()
        String json = gson.toJson(proposal)
        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty())
        when(proposalRepository.findAll()).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(1)).getByMaxId();
        verify(proposalRepository, times(1)).findAll();

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":1,"value":10}]'

    }

    def testShouldNotAcceptNewProposalWhenPreviouslyAcceptedProposalWithHigherId() {
        given:
        Proposal proposal = Proposal.builder().id(2).value(5).build()
        Proposal secondProposal = Proposal.builder().id(1).value(10).build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty()).thenReturn(Optional.empty())
        when(proposalRepository.findAll()).thenReturn(Collections.singletonList(proposal)).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(2)).getByMaxId();
        verify(proposalRepository, times(2)).findAll();

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":2,"value":5}]'
        secondResponse.status == OK.value()
        secondResponse.contentAsString == ''
        secondGetAcceptedValueResponse.status == OK.value()
        secondGetAcceptedValueResponse.contentAsString == '[{"id":2,"value":5}]'

    }

    def testShouldNotAcceptNewProposalWhenPreviouslyAcceptedProposalWithSameId() {
        given:
        Proposal proposal = Proposal.builder().id(1).value(7).build()
        Proposal secondProposal = Proposal.builder().id(1).value(10).build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty()).thenReturn(Optional.empty())
        when(proposalRepository.findAll()).thenReturn(Collections.singletonList(proposal)).thenReturn(Collections.singletonList(proposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(2)).getByMaxId();
        verify(proposalRepository, times(2)).findAll();

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":1,"value":7}]'
        secondResponse.status == OK.value()
        secondResponse.contentAsString == ''
        secondGetAcceptedValueResponse.status == OK.value()
        secondGetAcceptedValueResponse.contentAsString == '[{"id":1,"value":7}]'
    }

    def testShouldAcceptNewProposalWhenPreviouslyAcceptedProposalWithLowerId() {
        given:
        Proposal proposal = Proposal.builder().id(1).value(20).build()
        Proposal secondProposal = Proposal.builder().id(2).value(18).build()
        String proposalJson = gson.toJson(proposal)
        String secondProposalJson = gson.toJson(secondProposal)

        when(proposalRepository.getByMaxId()).thenReturn(Optional.empty()).thenReturn(Optional.of(proposal))
        when(proposalRepository.findAll()).thenReturn(Collections.singletonList(proposal)).thenReturn(Arrays.asList(proposal, secondProposal))

        when: 'rest accept url is hit'
        def response = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(proposalJson)).andReturn().response
        def getAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response
        def secondResponse = mockMvc.perform(post(ACCEPTOR_URL).contentType(MediaType.APPLICATION_JSON).content(secondProposalJson)).andReturn().response
        def secondGetAcceptedValueResponse = mockMvc.perform(get(ACCEPTOR_URL)).andReturn().response

        verify(proposalRepository, times(1)).save(proposal);
        verify(proposalRepository, times(1)).save(secondProposal);
        verify(proposalRepository, times(2)).getByMaxId();
        verify(proposalRepository, times(2)).findAll();

        then: 'acceptor controller should return ok status and appropriate value'
        response.status == OK.value()
        response.contentAsString == ''
        getAcceptedValueResponse.status == OK.value()
        getAcceptedValueResponse.contentAsString == '[{"id":1,"value":20}]'
        secondResponse.status == OK.value()
        secondResponse.contentAsString == '{"id":1,"value":20}'
        secondGetAcceptedValueResponse.status == OK.value()
        secondGetAcceptedValueResponse.contentAsString == '[{"id":1,"value":20},{"id":2,"value":18}]'
    }

}
