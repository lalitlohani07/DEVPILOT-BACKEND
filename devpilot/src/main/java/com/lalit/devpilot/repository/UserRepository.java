package com.lalit.devpilot.repository;

import java.util.Optional;

import com.lalit.devpilot.model.User;
import java.util.List;
public interface UserRepository {
       User save(User user);
       Optional<User> findById(int id);
       List<User> findAll();
       Optional<User> findByEmail(String email);
       boolean update(User user);
       public boolean deactivate(int id);
}
