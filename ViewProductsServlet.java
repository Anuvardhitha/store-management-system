import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/viewproducts")
public class ViewProductsServlet extends HttpServlet {private static final String CSS =
    "<style>* { box-sizing: border-box; } body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: linear-gradient(135deg, #eef4ff 0%, #f7f7f8 40%); color: #1a1a1a; margin: 0; padding: 40px 20px; line-height: 1.5; } .container { max-width: 700px; margin: 0 auto; } h2,h3 { color: #111; } .section { background: #ffffff; border: 1px solid #e5e5e5; border-left: 4px solid #4f7cff; border-radius: 10px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.04); } table { width: 100%; border-collapse: collapse; } th, td { padding: 8px 10px; text-align: left; border-bottom: 1px solid #eee; } th { color: #555; font-size: 13px; } input[type='text'] { padding: 8px 10px; font-size: 14px; border: 1px solid #d5d5d5; border-radius: 6px; margin: 4px 0; } input[type='submit'], button { padding: 10px 16px; background-color: #4f7cff; color: #fff; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; } a { color: #4f7cff; text-decoration: none; font-weight: 500; border-bottom: 1px solid #cddcff; } </style>";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

      String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";
        out.println("<html><head><title>Product List</title>" + CSS + "</head><body>");
out.println("<div class='container'><div class='section'>");
        out.println("<h2>Product List</h2>");

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM product");

            out.println("<table border='1' cellpadding='5'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Price</th><th>Quantity</th><th>Min Stock</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                // Using column index instead of column name prevents column name mismatch errors!
                out.println("<td>" + rs.getObject(1) + "</td>"); // Column 1 (ID / Primary Key)
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getDouble("price") + "</td>");
                out.println("<td>" + rs.getInt("quantity") + "</td>");
                out.println("<td>" + rs.getInt("min_stock_level") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<br><p><a href='index.html'>Add Another Product</a></p>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error details: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        }
        out.println("</div></div></body></html>");    }
}