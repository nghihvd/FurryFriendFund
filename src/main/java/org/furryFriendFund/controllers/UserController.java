package org.furryFriendFund.controllers;

import org.furryFriendFund.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    private UserService usersDAO;

    // show register page
    @GetMapping("/register")
    public String showRegisterPage() {

    }

}
