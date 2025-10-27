package com.example.service;

import com.example.model.UserBo;
import com.example.dao.UserDao;

import javax.inject.Inject;

public class UserService {

    private final UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(UserBo userBo) {
        userDao.saveUser(userBo);
    }
}
