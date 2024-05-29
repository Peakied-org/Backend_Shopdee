package com.peak.Service;

import com.peak.main.model.User;
import com.peak.main.repository.UserRepository;
import com.peak.main.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final List<User> userList =
            new ArrayList<>(List.of(
                    User.builder().id(1L).name("name").build(),
                    User.builder().id(2L).name("name1").build()
            ));

    @Test
    void testGetAllUsers() {

        when(userRepository.findAll()).thenReturn(userList);
        List<User> result = userService.getAllUsers();

        assertEquals(userList.size(), result.size());

        // call 1 time
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {

        when(userRepository.findById(any(long.class))).thenReturn(
                userList.stream().filter(user -> user.getId().equals(1L)).findFirst());

        User user = userService.findById(1L);

        assertEquals(1L, user.getId());
        assertEquals("name", user.getName());

        verify(userRepository, times(1)).findById(any(long.class));
    }

    @Test
    void testDeleteUser() {
        doAnswer(invocationOnMock -> {
                    userList.remove(userList.get(0));
                    return userList;
                }
        ).when(userRepository).delete(any(User.class));

        userService.delete(userList.get(0));

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testDeleteUserById() {
        doAnswer(invocationOnMock -> {
                    userList.removeIf(user -> user.getId().equals(1L));
                    return userList;
                }
        ).when(userRepository).deleteById(any(long.class));

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(any(long.class));
    }
}
