import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/addproduct")
public class AddProductServlet extends HttpServlet {
private static final String CSS =
    "<style>" +
    "* { box-sizing: border-box; }" +
    "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; " +
    "background: linear-gradient(135deg, #eef4ff 0%, #f7f7f8 40%); color: #1a1a1a; margin: 0; padding: 40px 20px; line-height: 1.5; }" +
    ".container { max-width: 520px; margin: 0 auto; }" +
    "h1 { font-size: 20px; font-weight: 600; margin: 0 0 16px; color: #111; }" +
    ".section { background: #ffffff; border: 1px solid #e5e5e5; border-left: 4px solid #4f7cff; " +
    "border-radius: 10px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.04); }" +
    "input[type='text'] { width: 100%; padding: 10px 12px; font-size: 14px; border: 1px solid #d5d5d5; " +
    "border-radius: 6px; margin-bottom: 16px; background-color: #fafafa; }" +
    "input[type='submit'] { width: 100%; padding: 11px; background-color: #4f7cff; color: #fff; border: none; " +
    "border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; }" +
    "input[type='submit']:hover { background-color: #3a63e0; }" +
    "a { color: #4f7cff; text-decoration: none; font-weight: 500; border-bottom: 1px solid #cddcff; }" +
    "p { margin: 0; }" +
    "</style>";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Step A: read the form values sent from the browser
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int minStock = Integer.parseInt(request.getParameter("minStock"));

        String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "INSERT INTO product (name, price, quantity, min_stock_level) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, quantity);
            ps.setInt(4, minStock);

            int rows = ps.executeUpdate();

            out.println("<html><head><title>Store App</title>" + CSS + "</head><body>");
out.println("<div class='container'><div class='section'>");
out.println("<h2>Product added successfully!</h2>");
out.println("<p><a href='index.html'>Add another</a> | <a href='viewproducts'>View all products</a></p>");
out.println("</div></div></body></html>");
            conn.close();
        } catch (Exception e) {
            out.println("<h2>Error adding product</h2>");
            e.printStackTrace();
        }
    }
}