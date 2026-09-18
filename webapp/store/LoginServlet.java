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
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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

        if (email == null || password == null ||
            email.trim().isEmpty() || password.isEmpty()) {

            response.getWriter().println(
                "Please enter your Gmail and password."
            );
            return;
        }

        email = email.trim().toLowerCase();

        try (Connection con = getConnection()) {

            String sql =
                "SELECT id, email, password_hash FROM users WHERE email = ?";

            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        String storedHash = rs.getString("password_hash");
                        String enteredHash = hashPassword(password);

                        if (storedHash.equals(enteredHash)) {

                            HttpSession session = request.getSession();

                            session.setAttribute("userEmail", email);
                            session.setAttribute("userId", rs.getInt("id"));

                            response.sendRedirect("dashboard.html");

                        } else {

                            response.getWriter().println(
                                "Incorrect password. Please try again."
                            );
                        }

                    } else {

                        response.getWriter().println(
                            "No account found with this Gmail. " +
                            "Please create an account first."
                        );
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "Login failed. Please try again later."
            );
        }
    }
}
