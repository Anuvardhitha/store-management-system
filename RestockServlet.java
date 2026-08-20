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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        int productId = Integer.parseInt(request.getParameter("productId"));
        int qty = Integer.parseInt(request.getParameter("quantity"));

        String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";      
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
                conn.rollback();
                return;
            }
            String productName = rs.getString("name");

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
            out.println("<p><a href='index.html'>Back to menu</a></p>");

            conn.close();
        } catch (Exception e) {
           out.println("<h2>Error restocking: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}