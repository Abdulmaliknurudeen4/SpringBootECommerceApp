package com.shopme.admin.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.admin.setting.state.StateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfiguration
public class CategoryServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    StateRepository stateRepository;

    @Test
    @WithMockUser(username = "abdulmaliknurudeen4@gmail.com", password = "password", roles = "Admin")
    public void testListByCountries() throws Exception {
        Integer countryId = 4;
        String url = "/states/list_by_country/" + countryId;
       }

    @Test
    public void testCheckUniqueDuplicateAlias() {
       }

    @Test
    public void testCheckUniqueInEditModeReturn() {
         }

    @Test
    public void testCheckUniqueInEditModeReturnAlias() {
    }
}
