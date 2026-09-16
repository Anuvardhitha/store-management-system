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
public class RestockServlet extends HttpServlet {private static final String CSS =
    "<style>* { box-sizing: border-box; } body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: linear-gradient(135deg, #eef4ff 0%, #f7f7f8 40%); color: #1a1a1a; margin: 0; padding: 40px 20px; line-height: 1.5; } .container { max-width: 700px; margin: 0 auto; } h2,h3 { color: #111; } .section { background: #ffffff; border: 1px solid #e5e5e5; border-left: 4px solid #4f7cff; border-radius: 10px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.04); } table { width: 100%; border-collapse: collapse; } th, td { padding: 8px 10px; text-align: left; border-bottom: 1px solid #eee; } th { color: #555; font-size: 13px; } input[type='text'] { padding: 8px 10px; font-size: 14px; border: 1px solid #d5d5d5; border-radius: 6px; margin: 4px 0; } input[type='submit'], button { padding: 10px 16px; background-color: #4f7cff; color: #fff; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; } a { color: #4f7cff; text-decoration: none; font-weight: 500; border-bottom: 1px solid #cddcff; } </style>";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        int productId = Integer.parseInt(request.getParameter("productId"));
        int qty = Integer.parseInt(request.getParameter("quantity"));

        String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";
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
            out.println("<p><a href='index.html'>Back to menu</a></p>");
            out.println("</div></div></body></html>");

            conn.close();
                } catch (Exception e) {
           out.println("<h2>Error restocking: " + e.getMessage() + "</h2>");
           out.println("</div></div></body></html>");
            e.printStackTrace();
        }    }
}