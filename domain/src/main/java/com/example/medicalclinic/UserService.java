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
        return userJpaRepositoryPort.save(User.from(command));
    }

    public void updateUser(String email, UpdateUserRequest request) {
        User user = userJpaRepositoryPort
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.update(request);
        userJpaRepositoryPort.save(user);
    }

    public void deleteUser(String email) {
        userJpaRepositoryPort.deleteByEmail(email);
    }
}
