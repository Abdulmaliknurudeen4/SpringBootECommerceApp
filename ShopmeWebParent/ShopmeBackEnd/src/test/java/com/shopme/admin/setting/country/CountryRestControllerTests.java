package com.shopme.admin.setting.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "ADMIN")
    public void getCountries() throws Exception {
        String url = "/countries/list";
        MvcResult result =
                mockMvc
                        .perform(get(url))
                        .andExpect(status().isOk())
                        .andDo(print()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
        Arrays.stream(countries).forEach(System.out::println);
        assertThat(countries).hasSizeGreaterThan(0);

    }

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "ADMIN")
    public void saveCountry() throws Exception {
        String url = "/countries/save";
        Country newZealand = new Country("Denmark", "DM");
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json").content(objectMapper.writeValueAsString(newZealand)).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        Integer savedCountryId = Integer.valueOf(jsonResponse);
        Optional<Country> findCountryById = countryRepository.findById(savedCountryId);
        assertThat(findCountryById).isPresent();
        assertThat(findCountryById.get().getName()).isEqualTo(newZealand.getName());
    }

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "ADMIN")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";
        Integer id = 12;
        Country denmark = new Country("DenmarkUpdated", "DMU");
        denmark.setId(id);
        MvcResult result = mockMvc.perform(
                        post(url)
                                .contentType("application/json").content(objectMapper.writeValueAsString(denmark)).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(id)))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        Integer savedCountryId = Integer.valueOf(jsonResponse);
        Optional<Country> findCountryById = countryRepository.findById(savedCountryId);
        assertThat(findCountryById).isPresent();
        assertThat(findCountryById.get().getName()).isEqualTo(denmark.getName());
    }

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "ADMIN")
    public void testDeleteCountry() throws Exception {
        Integer id = 12;
        String url = "/countries/delete/"+id;
        MvcResult result = mockMvc
                .perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"))
                .andReturn();
        Optional<Country> byId = countryRepository.findById(id);
        assertThat(byId).isNotPresent();
    }
}
