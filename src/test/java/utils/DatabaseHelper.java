package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:postgresql://155.212.170.64:5432/shlapabank";
    private static final String DB_USER = "shlapabank";
    private static final String DB_PASSWORD = "shlapabank";

    private Connection connection;

    public DatabaseHelper() {
        try {
            // Загружаем драйвер PostgreSQL
            Class.forName("org.postgresql.Driver");
            // Устанавливаем соединение
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            throw new RuntimeException("PostgreSQL JDBC Driver not found!", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }

    // Закрыть соединение
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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