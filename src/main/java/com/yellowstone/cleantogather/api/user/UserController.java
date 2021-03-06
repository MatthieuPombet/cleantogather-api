package com.yellowstone.cleantogather.api.user;

import com.yellowstone.cleantogather.api.common.exception.NotFoundException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController // This means this class is a rest controller
@RequestMapping(path="/users") // This means URL's start with /events (after Application path)
public class UserController {
	
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, ModelMapper mapper,UserService userService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping("/signin")
    @ApiOperation(value = "Authenticates user and returns its JWT token")
    public String login(@ApiParam("Name") @RequestParam String name, @ApiParam("Password") @RequestParam String password) {
        return userService.signin(name, password);
    }

    @ApiOperation("Get all the users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
    
    @ApiOperation("Create a user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public User postUser(@RequestBody User user) {
    	user.setName(user.getEmail());
		user.setPassword(user.getEmail());
		user.setRoles(new ArrayList<Role>(Arrays.asList(Role.ROLE_USER)));
		userService.signup(user);
        return user;
    }
    
    @ApiOperation("Get an user by his id")
    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }
    
    @ApiOperation("Patch an existent user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public User patchUser(@RequestBody User user, @PathVariable("id") Long id) {
        User userToUpdate = userRepository.findById(id).orElseThrow(NotFoundException::new);
        mapper.map(user, userToUpdate);
        return userRepository.save(userToUpdate);
    }

    @ApiOperation("Delete an user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable("id") Long id) {
        User deletedUser = userRepository.findById(id).orElseThrow(NotFoundException::new);
        userRepository.deleteById(id);
        return deletedUser;
    }
    
    @ApiOperation("Get a user by email")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/mail/{mail}")
    public User getUserByMail(@PathVariable("mail") String mail) {
        return userRepository.findByEmail(mail);
    }
    
    @ApiOperation("Get a user by name")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/name/{name}")
    public User getUserByName(@PathVariable("name") String name) {
        return userRepository.findByName(name);
    }
}
