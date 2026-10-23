// package com.lalit.devpilot.repository;

// import java.util.Optional;

// import com.lalit.devpilot.model.User;
// import java.util.List;
// public interface UserRepository {
//        User save(User user);
//        Optional<User> findById(int id);
//        List<User> findAll();
//        Optional<User> findByEmail(String email);
//        boolean update(User user);
//        public boolean deactivate(int id);
// }
package com.lalit.devpilot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lalit.devpilot.model.EmploymentStatus;
import com.lalit.devpilot.model.User;

// "A repository for User entities whose id is an Integer."
// Spring generates the implementation at startup. We write only what it can't infer.
//
// Inherited, so no longer written by us:
//   save(User)      -> INSERT when id == 0, UPDATE otherwise
//   findById(int)   -> int autoboxes to Integer, so existing callers still compile
//   findAll()
public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Data builds "SELECT ... WHERE email = ?" from the method name.
    Optional<User> findByEmail(String email);

    // One UPDATE statement, written against the entity (User / employmentStatus),
    // not against table or column names.
    // @Modifying = this query changes data. @Transactional = writes need a transaction.
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update User u set u.employmentStatus = :status where u.id = :id")
    int updateEmploymentStatus(@Param("id") int id, @Param("status") EmploymentStatus status);

    // Same signature as the JDBC version: true if a row was actually changed.
    @Transactional
    default boolean deactivate(int id) {
        return updateEmploymentStatus(id, EmploymentStatus.INACTIVE) > 0;
    }

    // Same signature as the JDBC version: false if the user doesn't exist.
    @Transactional
    default boolean update(User user) {
        if (!existsById(user.getId())) {
            return false;
        }
        save(user);
        return true;
    }
}