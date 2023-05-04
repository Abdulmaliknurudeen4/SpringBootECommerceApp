package com.shopme.admin.setting.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.entity.Country;
import com.shopme.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "Admin")
    public void testListByCountries() throws Exception {
        String url = "/states/list_by_country/4";
        MvcResult result =
                mockMvc.perform(get(url))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        State[] states = objectMapper.readValue(jsonResponse, State[].class);
        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "Admin")
    public void testCreateState() throws Exception {
        String url = "/states/save";
        Integer countryId = 4;
        Country country = countryRepository.findById(countryId).get();
        State state = new State("Arizona", country);
        MvcResult result =
                mockMvc.perform(post(url).contentType("application/json").content(objectMapper.writeValueAsString(state)).with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Integer id = Integer.parseInt(jsonResponse);
        Optional<State> findbyId = stateRepository.findById(id);
        assertThat(findbyId).isPresent();
    }


}
