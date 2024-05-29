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
import java.util.List;

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

    private ArrayList<User> users = new ArrayList<>(List.of(
            new User(1L, "name1", "password1", Role.USER, "1234", "address1", ""),
            new User(2L, "name2", "password2", Role.SELLER, "12345", "address2", ""),
            new User(3L, "name3", "password3", Role.ADMIN, "123456", "address3", "")
    ));

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
    @WithMockUser(authorities = "USER")
    void testGetUserByUser() throws Exception {

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(userService, times(0)).getAllUsers();
    }

    @Test
    @WithMockUser(authorities = "SELLER")
    void testGetUserBySeller() throws Exception {

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(userService, times(0)).getAllUsers();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUser() throws Exception {

        User user = users.get(0);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body[0].name").value(user.getName()),
                        jsonPath("$.body[0].tel").value(user.getTel()),
                        jsonPath("$.body[0].role").value(user.getRole().name())
                );
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testUpdateUserByUser() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder().tel("000").build();

        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType("application/json")
                        .content(request))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(userService, times(0)).update(any(User.class), any(RegisterRequest.class));
    }

    @Test
    @WithMockUser(authorities = "SELLER")
    void testUpdateUserBySeller() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder().tel("000").build();

        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType("application/json")
                        .content(request))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(userService, times(0)).update(any(User.class), any(RegisterRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateUserByAdmin() throws Exception {

        User user = users.get(0);

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
                        jsonPath("$.body.name").value(user.getName()),
                        jsonPath("$.body.tel").value(user.getTel()),
                        jsonPath("$.body.role").value(user.getRole().name())
                );
        verify(userService, times(1)).update(any(User.class), any(RegisterRequest.class));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testDeleteUserByUser() throws Exception {
        doNothing().when(userService).deleteById(any(long.class));
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(userService, times(0)).deleteById(any(long.class));
    }

    @Test
    @WithMockUser(authorities = "SELLER")
    void testDeleteUserBySeller() throws Exception {
        doNothing().when(userService).deleteById(any(long.class));
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(userService, times(0)).deleteById(any(long.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUserByAdmin() throws Exception {
        doNothing().when(userService).deleteById(any(long.class));
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpectAll(
                        status().isOk()
                );
        verify(userService, times(1)).deleteById(any(long.class));
    }

}
