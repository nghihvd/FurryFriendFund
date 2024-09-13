package org.furryFriendFund.controllers;

import org.furryFriendFund.user.UsersDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    private UsersDAO usersDAO;

    // show register page
    @GetMapping("/register")
    public String showRegisterPage() {

    }

}
