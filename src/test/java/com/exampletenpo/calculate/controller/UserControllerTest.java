package com.exampletenpo.calculate.controller;

import com.exampletenpo.calculate.AuthTest;
import com.exampletenpo.calculate.domain.account.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    AuthTest authTest;

    @Autowired
    private MockMvc mockMvc;

    private final String url = "/user";

    private static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(
                    MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8
            );

    @Test
    public void createUser_whenValidJwtToken_returnsCreated() throws Exception {
        mockMvc.perform(
                        post(url)
                                .contentType(APPLICATION_JSON_UTF8).content(getJsonNoAdminUser())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
        authTest.deleteNoAdminUserTest();
    }

    @Test
    public void updateUser_whenNoAuth_thenReturnsForbidden() throws Exception {
        mockMvc.perform(put(url)
                        .contentType(APPLICATION_JSON_UTF8).content(getJsonNoAdminUser())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUser_whenNoAuth_thenReturnsForbidden() throws Exception {
        mockMvc.perform(delete(url)
                        .contentType(APPLICATION_JSON_UTF8).content(getJsonNoAdminUser())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateUser_whenValidJwtToken_thenReturnsOk() throws Exception {
        authTest.createNoAdminUserForTest();
        mockMvc.perform(
                        put(url)
                                .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                                .contentType(APPLICATION_JSON_UTF8).content(getJsonNoAdminUser())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
        authTest.deleteNoAdminUserTest();
    }

    @Test
    public void updateUser_whenValidJwtTokenUserNotExist_thenReturns4xxError() throws Exception {
        authTest.createNoAdminUserForTest();
        mockMvc.perform(
                        put(url)
                                .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                                .contentType(APPLICATION_JSON_UTF8).content(getJsonUserNotExistNoAdmin())
                )
                .andDo(print())
                .andExpect(status().is4xxClientError());
        authTest.deleteNoAdminUserTest();
    }

    @Test
    public void updateUser_whenValidJwtTokenUpdateOtherUserWithUserNotAdmin_thenReturnsUnauthorized() throws Exception {
        authTest.createNoAdminUserForTest();
        authTest.createUserAdminForTest();
        mockMvc.perform(
                        put(url)
                                .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                                .contentType(APPLICATION_JSON_UTF8).content(getJsonUserAdmin())
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
        authTest.deleteNoAdminUserTest();
        authTest.deleteAdminUserTest();
    }

    @Test
    public void deleteUser_whenValidJwtToken_thenReturnsOk() throws Exception {
        authTest.createNoAdminUserForTest();
        mockMvc.perform(
                        delete(url + "/username/" + AuthTest.NO_ADMIN_USER_TEST)
                                .with(user(authTest.getUserDetails(authTest.getAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_whenValidJwtTokenNoAdminUser_thenReturnsForbidden() throws Exception {
        authTest.createNoAdminUserForTest();
        mockMvc.perform(
                        delete(url + "/username/" + AuthTest.NO_ADMIN_USER_TEST)
                                .with(user(authTest.getUserDetails(authTest.getNoAdminUser())))
                )
                .andDo(print())
                .andExpect(status().isForbidden());
        authTest.deleteNoAdminUserTest();
    }

    private String getJsonUserAdmin() throws JsonProcessingException {
        User user = authTest.getAdminUser();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(user);
    }

    private String getJsonNoAdminUser() throws JsonProcessingException {
        User user = authTest.getNoAdminUser();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(user);
    }

    private String getJsonUserNotExistNoAdmin() throws JsonProcessingException {
        User user = getStandardUserNoExists();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(user);
    }

    private User getStandardUserNoExists() {
        return User
                .builder()
                .username("user")
                .password("1234")
                .enabled(true)
                .roles(authTest.getRoleUserNoAdmin())
                .build();
    }
}
