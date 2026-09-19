import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/restock")
public class RestockServlet extends HttpServlet {
    private static final String CSS =
    "<link rel='stylesheet' href='store.css'>";
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int productId = Integer.parseInt(request.getParameter("productId"));
        int qty = Integer.parseInt(request.getParameter("quantity"));

       String url = System.getenv("DB_URL");
String user = System.getenv("DB_USER");
String password = System.getenv("DB_PASSWORD");
out.println("<html><head><title>Restock</title>" + CSS + "</head><body>");
out.println("<div class='container'><div class='section'>");
try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);

            String checkSql = "SELECT name FROM product WHERE product_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, productId);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                out.println("<h2>Product not found!</h2>");
                out.println("</div></div></body></html>");
                conn.rollback();
                return;
            }            String productName = rs.getString("name");

            String updateSql = "UPDATE product SET quantity = quantity + ? WHERE product_id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, qty);
            updatePs.setInt(2, productId);
            updatePs.executeUpdate();

            String movementSql = "INSERT INTO stock_movement (product_id, change_type, quantity, note) VALUES (?, ?, ?, ?)";
            PreparedStatement movementPs = conn.prepareStatement(movementSql);
            movementPs.setInt(1, productId);
            movementPs.setString(2, "PURCHASE");
            movementPs.setInt(3, qty);
            movementPs.setString(4, "Restocked from supplier");
            movementPs.executeUpdate();

            conn.commit();

                        out.println("<h2>Restocked " + qty + " units of " + productName + "!</h2>");
            out.println("<p><a href='dashboard.html'>Back to menu</a></p>");
            out.println("</div></div></body></html>");

            conn.close();
                } catch (Exception e) {
           out.println("<h2>Error restocking: " + e.getMessage() + "</h2>");
           out.println("</div></div></body></html>");
            e.printStackTrace();
        }    }
}
