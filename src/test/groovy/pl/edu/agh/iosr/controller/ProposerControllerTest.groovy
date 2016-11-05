package pl.edu.agh.iosr.controller

import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

public class ProposerControllerTest extends Specification {

    def MockMvc mockMvc

    def setup() {
        mockMvc = standaloneSetup(new ProposerController()).build()
    }

    def testProposerReturnEmptyProposal() {
        when: 'rest propose url is hit'
        def response = mockMvc.perform(get('/proposer/propose')).andReturn().response

        then: 'proposer controller should return empty proposal'
        response.status == OK.value()
        response.contentAsString == '{"id":0,"value":null}'
    }

}
