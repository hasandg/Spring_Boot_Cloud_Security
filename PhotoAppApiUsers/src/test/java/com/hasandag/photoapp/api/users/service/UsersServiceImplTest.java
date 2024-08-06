package com.hasandag.photoapp.api.users.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hasandag.photoapp.api.users.data.*;
import com.hasandag.photoapp.api.users.shared.UserDto;

class UsersServiceImplTest {

    @InjectMocks
    UsersServiceImpl usersService;

    @Mock
    UsersRepository usersRepository;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AlbumsServiceClient albumsServiceClient;

    @Mock
    Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usersService = new UsersServiceImpl(usersRepository, authorityRepository, roleRepository, bCryptPasswordEncoder, albumsServiceClient, environment);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setPassword("password");

        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encryptedPassword");
        when(usersRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        UserDto createdUser = usersService.createUser(userDto);

        assertNotNull(createdUser);
        assertEquals("encryptedPassword", createdUser.getEncryptedPassword());
        verify(usersRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(usersRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.loadUserByUsername("test@test.com");
        });
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@test.com");
        userEntity.setEncryptedPassword("encryptedPassword");
        userEntity.setRoles(Collections.emptyList());

        when(usersRepository.findByEmail(anyString())).thenReturn(userEntity);

        var userDetails = usersService.loadUserByUsername("test@test.com");

        assertNotNull(userDetails);
        assertEquals("test@test.com", userDetails.getUsername());
    }

    @Test
    void testGetUserDetailsByEmail_UserNotFound() {
        when(usersRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.getUserDetailsByEmail("test@test.com");
        });
    }

    @Test
    void testGetUserDetailsByEmail_UserFound() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@test.com");

        when(usersRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = usersService.getUserDetailsByEmail("test@test.com");

        assertNotNull(userDto);
        assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void testGetUserByUserId_UserNotFound() {
        when(usersRepository.findByUserId(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.getUserByUserId("userId", "authToken");
        });
    }

    @Test
    void testGetUserByUserId_UserFound() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId("userId");

        when(usersRepository.findByUserId(anyString())).thenReturn(userEntity);
        when(albumsServiceClient.getAlbums(anyString(), anyString())).thenReturn(Collections.emptyList());

        UserDto userDto = usersService.getUserByUserId("userId", "authToken");

        assertNotNull(userDto);
        assertEquals("userId", userDto.getUserId());
    }
}