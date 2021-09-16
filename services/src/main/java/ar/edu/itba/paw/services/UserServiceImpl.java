package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.UserDao;
import ar.edu.itba.paw.interfaces.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User create(Integer fileNumber, String name, String surname, String username, String email, String password,
                       boolean isAdmin) {
        return userDao.create(fileNumber, name, surname, username, email, password, isAdmin);
    }

    @Override
    public boolean update(Long userId, User user) {
        return userDao.update(userId, user);
    }

    @Override
    public boolean delete(Long userId) {
        return userDao.delete(userId);
    }

    @Override
    public Optional<Role> getRole(Long userId, Long courseId) {
        return userDao.getRole(userId, courseId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userDao.findById(userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
