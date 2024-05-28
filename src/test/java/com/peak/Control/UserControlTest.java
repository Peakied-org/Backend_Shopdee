package com.peak.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.model.User;
import com.peak.main.service.UserService;
import com.peak.security.model.RegisterRequest;
import com.peak.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUnAuthorization() throws Exception {
        mockMvc.perform(get("/api/v1/customers/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    void testGetMe() throws Exception {
        mockMvc.perform(get("/api/v1/customers/me"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @WithUserDetails
    void testUpdateMe() throws Exception {

        User user = User.builder().name("name").tel("123").role(Role.USER).build();

        RegisterRequest registerRequest = RegisterRequest.builder().tel("000").build();

        String request = objectMapper.writeValueAsString(registerRequest);

        when(userService.update(any(User.class), any(RegisterRequest.class))).then(
                invocationOnMock -> {
                    user.setTel("000");
                    return user;
                }
        );

        mockMvc.perform(put("/api/v1/customers/me")
                .contentType("application/json")
                .content(request))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body.name").value("name"),
                        jsonPath("$.body.tel").value("000"),
                        jsonPath("$.body.role").value("USER")
                );
        verify(userService, times(1)).update(any(User.class), any(RegisterRequest.class));
    }

    @Test
    @WithUserDetails
    void testDeleteMe() throws Exception {

        doNothing().when(userService).delete(any(User.class));

        mockMvc.perform(delete("/api/v1/customers/me"))
                .andExpectAll(
                        status().isOk()
                );
        verify(userService, times(1)).delete(any(User.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUser() throws Exception {

        ArrayList<User> users = new ArrayList<>();
        users.add(User.builder().name("name").tel("123").role(Role.USER).build());

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body[0].name").value("name"),
                        jsonPath("$.body[0].tel").value("123"),
                        jsonPath("$.body[0].role").value("USER")
                );
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateUser() throws Exception {

        User user = User.builder().id(1L).name("name").tel("123").role(Role.USER).build();

        RegisterRequest registerRequest = RegisterRequest.builder().tel("000").build();

        String request = objectMapper.writeValueAsString(registerRequest);

        when(userService.findById(1L)).thenReturn(user);
        when(userService.update(any(User.class), any(RegisterRequest.class))).then(
                invocationOnMock -> {
                    user.setTel("000");
                    return user;
                }
        );

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType("application/json")
                        .content(request))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body.name").value("name"),
                        jsonPath("$.body.tel").value("000"),
                        jsonPath("$.body.role").value("USER")
                );
        verify(userService, times(1)).findById(any(long.class));
        verify(userService, times(1)).update(any(User.class), any(RegisterRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(any(User.class));
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testDeleteUserNotAdmin() throws Exception {
        doNothing().when(userService).delete(any(User.class));
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpectAll(
                        status().isForbidden()
                );
    }
}
