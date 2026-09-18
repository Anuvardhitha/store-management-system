import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private Connection getConnection() throws Exception {

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    private String hashPassword(String password) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(
            password.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (email == null || password == null || confirmPassword == null ||
            email.trim().isEmpty() || password.isEmpty()) {

            response.getWriter().println("Please fill all fields.");
            return;
        }

        email = email.trim().toLowerCase();

        if (!email.endsWith("@gmail.com")) {
            response.getWriter().println("Please use a Gmail address.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            response.getWriter().println("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            response.getWriter().println(
                "Password must contain at least 6 characters."
            );
            return;
        }

        try (Connection con = getConnection()) {
                String createTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

            try (PreparedStatement tableStmt =
                    con.prepareStatement(createTable)) {

                tableStmt.executeUpdate();
            }
            // Check whether account already exists
            String checkSql =
                "SELECT id FROM users WHERE email = ?";

            try (PreparedStatement check =
                    con.prepareStatement(checkSql)) {

                check.setString(1, email);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next()) {
                        response.getWriter().println(
                            "An account with this Gmail already exists."
                        );
                        return;
                    }
                }
            }

            // Hash password before storing it
            String hashedPassword = hashPassword(password);

            String insertSql =
                "INSERT INTO users (email, password_hash) VALUES (?, ?)";

            try (PreparedStatement insert =
                    con.prepareStatement(insertSql)) {

                insert.setString(1, email);
                insert.setString(2, hashedPassword);

                insert.executeUpdate();
            }

            response.sendRedirect("index.html");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "Registration failed. Please try again."
            );
        }
    }
}
