package urlshortener.blackgoku.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import urlshortener.blackgoku.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository{

    private static final Logger log = LoggerFactory
            .getLogger(UserRepositoryImpl.class);

    private static final RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString("email"), rs.getString("password"));
        }
    };

    @Autowired
    protected JdbcTemplate jdbc;

    public UserRepositoryImpl() {
    }

    public UserRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return jdbc.queryForObject("SELECT * FROM users WHERE email=?", rowMapper,
                    email);
        } catch (Exception e) {
            log.error("When select for email " + email);
            return null;
        }
    }

    @Override
    public boolean save(User usuario) {
        try {
            jdbc.update("INSERT INTO users VALUES (?,?)",
                    usuario.getEmail(),usuario.getPassword());
            log.info("User "+ usuario.getEmail() + " saved");
            return true;
        } catch (DuplicateKeyException e) {
            log.error("When insert for key " + usuario.getEmail());
            return false;
        } catch (Exception e) {
            log.error("When insert", e);
            return false;
        }
    }
}
