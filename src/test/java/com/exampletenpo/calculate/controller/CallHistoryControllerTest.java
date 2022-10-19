package com.exampletenpo.calculate.controller;

import com.exampletenpo.calculate.AuthTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CallHistoryControllerTest {
    @Autowired
    AuthTest authTest;

    @Autowired
    private MockMvc mockMvc;

    private final String url = "/call-history";
    private final String post_url = "?page=0&size=10&sort=date";

    @Test
    public void getAllCallHistory_whenNoAuth_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url + "/all" + post_url))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCallHistoryByMethod_whenNoAuth_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url + "/method/GET" + post_url))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCallHistoryByUsername_whenNoAuth_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url + "/username/name_username" + post_url))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAllCallHistory_whenValidJwtTokenNoAdminUser_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url + "/all" + post_url)
                        .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCallHistoryByMethod_whenValidJwtTokenNoAdminUser_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url + "/method/GET" + post_url)
                        .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCallHistoryByUsername_whenValidJwtTokenNoAdminUser_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url + "/username/name_username" + post_url)
                        .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAllCallHistory_whenValidJwtTokenAdminUser_thenReturnsOk() throws Exception {
        mockMvc.perform(get(url + "/all" + post_url)
                        .with(user(authTest.getUserDetails(authTest.getAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getCallHistoryByMethod_whenValidJwtTokenAdminUser_thenReturnsOk() throws Exception {
        mockMvc.perform(get(url + "/method/GET" + post_url)
                        .with(user(authTest.getUserDetails(authTest.getAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getCallHistoryByUsername_whenValidJwtTokenAdminUser_thenReturnsOk() throws Exception {
        mockMvc.perform(get(url + "/username/name_username" + post_url)
                        .with(user(authTest.getUserDetails(authTest.getAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}
