package com.lalit.devpilot;
import com.lalit.devpilot.config.appConfig;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.repository.UserRepository;
import com.lalit.devpilot.model.Role;
public class main {
public static void main(String[] args){
    AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(appConfig.class);

UserRepository repository =
        context.getBean(UserRepository.class);
        System.out.println(repository);
    //User user = new User("anki","ankt07@gmail",Role.ADMIN);
    //repository.save(user);
    //System.out.println(user.getId() + "<here is the id");
    testUserRepository(repository);
}
private static void testUserRepository(UserRepository repository) {

    // 1. Find by ID
    Optional<User> userById = repository.findById(3);
    System.out.println("findById: " + userById);

    // 2. Find all users
    List<User> users = repository.findAll();
    System.out.println("findAll: " + users);

    // 3. Find by email
    Optional<User> userByEmail =
            repository.findByEmail("rohi07@gmail");
    System.out.println("findByEmail: " + userByEmail);

    // 4. Update
    if (userById.isPresent()) {
        User user = userById.get();

        user.setName("Rohit Updated");

        boolean updated = repository.update(user);

        System.out.println("update: " + updated);
    }

    // 5. Find again to verify the update
    Optional<User> updatedUser = repository.findById(3);
    System.out.println("after update: " + updatedUser);

    // 6. Soft delete / deactivate
    boolean deleted = repository.deactivate(3);
    System.out.println("delete/deactivate: " + deleted);

    // 7. Find again to verify soft delete
    Optional<User> deactivatedUser = repository.findById(3);
    System.out.println("after delete: " + deactivatedUser);
}
}
