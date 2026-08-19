package com.lalit.devpilot.repository;

import com.lalit.devpilot.model.User;

public interface UserRepository {
       User save(User user);
       User findById(int id);
}
