package utils;

import config.DataConfig;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

    private final Connection connection;

    public DatabaseHelper() {
        DataConfig config = ConfigFactory.create(DataConfig.class);
        try {
            connection = DriverManager.getConnection(
                    config.getDbUrl(),
                    config.getDbUser(),
                    config.getDbPassword()
            );
            log.info("Database connection established");
        } catch (SQLException e) {
            log.error("Failed to connect to database", e);
            throw new RuntimeException("Failed to connect to database!", e);
        }
    }

    // Получить случайного пользователя из БД
    public UserCredentials getRandomUser() {
        String query = "SELECT login, password_hash FROM users WHERE role = 'CLIENT' LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return new UserCredentials(
                        rs.getString("login"),
                        rs.getString("password_hash")
                );
            }
        } catch (SQLException e) {
            log.error("getRandomUser failed", e);
        }
        return null;
    }

    // Получить пользователя по логину
    public UserCredentials getUserByLogin(String login) {
        String query = "SELECT login, password_hash FROM users WHERE login = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UserCredentials(
                        rs.getString("login"),
                        rs.getString("password_hash")
                );
            }
        } catch (SQLException e) {
            log.error("getUserByLogin failed", e);
        }
        return null;
    }

    // Получить пользователя по ID
    public UserCredentials getUserById(int userId) {
        String query = "SELECT login, password_hash FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UserCredentials(
                        rs.getString("login"),
                        rs.getString("password_hash")
                );
            }
        } catch (SQLException e) {
            log.error("getUserById failed", e);
        }
        return null;
    }

    // Получить всех пользователей
    public List<UserCredentials> getAllUsers() {
        List<UserCredentials> users = new ArrayList<>();
        String query = "SELECT login, password_hash FROM users WHERE role = 'CLIENT'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new UserCredentials(
                        rs.getString("login"),
                        rs.getString("password_hash")
                ));
            }
        } catch (SQLException e) {
            log.error("getAllUsers failed", e);
        }
        return users;
    }

    // Проверить существование пользователя
    public boolean userExists(String login) {
        String query = "SELECT COUNT(*) FROM users WHERE login = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.error("userExists failed", e);
        }
        return false;
    }

    // Закрыть соединение
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Database connection closed");
            }
        } catch (SQLException e) {
            log.warn("Error closing database connection", e);
        }
    }

    // Вспомогательный класс для хранения учетных данных
    public static class UserCredentials {
        private final String login;
        private final String password;

        public UserCredentials(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "UserCredentials{login='" + login + "'}";
        }
    }
}