package com.peak.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.request.Response;
import com.peak.security.model.AuthenticationRequest;
import com.peak.security.model.AuthenticationResponse;
import com.peak.security.model.RegisterRequest;
import com.peak.security.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControlTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password", "1234567890", "testaddress", null, null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterFailureInvalidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(null, "password", "1234567890", "testaddress", null, null); // Missing name

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password");
        AuthenticationResponse response = new AuthenticationResponse("fakeToken");

        when(authenticationService.authenticate(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new Response(response))));
    }

    @Test
    void testAuthenticateFailureInvalidCredentials() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "wrongpassword");

        when(authenticationService.authenticate(request))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    @Test
    void testAuthenticateFailureUserNotFound() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("nonexistentuser", "password");

        when(authenticationService.authenticate(request))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
