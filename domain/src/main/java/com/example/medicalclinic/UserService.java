package com.example.medicalclinic;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
    private final UserJpaRepositoryPort userJpaRepositoryPort;

    public Page<User> getAllUsers(Pageable pageable) {
        return userJpaRepositoryPort.findAll(pageable);
    }

    public User getUserByEmail(String email) {
        return userJpaRepositoryPort
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public User addUser(AddUserCommand command) {
        User user = User.from(command);
        return userJpaRepositoryPort.save(user);
    }

    public void updateUser(String email, UpdateUserCommand command) {
        User user = userJpaRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.updateFrom(command);
        userJpaRepositoryPort.save(user);
    }

    public void updatePassword(String email, String password) {
        User user = userJpaRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setPassword(password);
        userJpaRepositoryPort.save(user);
    }

    public void deleteUser(String email) {
        userJpaRepositoryPort.deleteByEmail(email);
    }
}