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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CalculateControllerTest {
    @Autowired
    AuthTest authTest;

    @Autowired
    private MockMvc mockMvc;

    private final String url = "/calculate?firstNumber=5&secondNumber=5";

    @Test
    public void calculate_whenNoAuth_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url)).andExpect(status().isForbidden());
    }

    @Test
    public void calculate_whenValidJwtTokenNoAdminUser_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get(url)
                        .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void calculate_whenValidJwtToken_thenReturnsOk() throws Exception {
        mockMvc.perform(get(url).with(user(authTest.getUserDetails(authTest.getAdminUser()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("11.00"));
    }
}
