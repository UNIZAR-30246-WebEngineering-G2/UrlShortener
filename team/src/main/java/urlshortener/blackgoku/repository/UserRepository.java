package urlshortener.blackgoku.repository;

import urlshortener.blackgoku.domain.User;

public interface UserRepository {

    User findByEmail(String email);

    boolean save(User usuario);
}
