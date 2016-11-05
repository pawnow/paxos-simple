package pl.edu.agh.iosr.controller

import com.google.gson.Gson
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.edu.agh.iosr.cdm.Proposal
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class LearnerControllerTest extends Specification {

    public static final String LEARNED_URL = '/learner/learn'
    def Gson gson = new Gson()
    MockMvc mockMvc;

    def setup() {
        mockMvc = standaloneSetup(new LearnerController()).build();
    }

    def testShouldReturnEmptyProposalWhenNoValueLearned() {
        when: 'rest learn url is hit'
        def response = mockMvc.perform(get(LEARNED_URL)).andReturn().response

        then: 'learner controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == '{"id":null,"value":null}'
    }

    def testShouldLearnNewProposal() {
        given:
        Proposal proposal = Proposal.builder().id(1).value(10).build()
        String json = gson.toJson(proposal);

        when: 'rest learn url is hit'
        def response = mockMvc.perform(post(LEARNED_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().response

        then: 'learner controller should return ok status'
        response.status == OK.value()
    }

    def testShouldReturnLearnedProposal() {
        given:
        Proposal proposal = Proposal.builder().id(3).value(15).build()

        String json = gson.toJson(proposal);
        when: 'rest learn url is hit'
        mockMvc.perform(post(LEARNED_URL).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().response
        def response = mockMvc.perform(get(LEARNED_URL)).andReturn().response

        then: 'learner controller should return learned value'
        response.status == OK.value()
        response.contentAsString == '{"id":3,"value":15}'
    }

    def testShouldReturnEmptyProposalWhenNoValueLearned2() {
        when: 'rest learn url is hit'
        def response = mockMvc.perform(get(LEARNED_URL)).andReturn().response

        then: 'learner controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == '{"id":null,"value":null}'
    }

}
