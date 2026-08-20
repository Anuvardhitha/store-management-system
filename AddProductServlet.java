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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Step A: read the form values sent from the browser
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int minStock = Integer.parseInt(request.getParameter("minStock"));

        String url = "jdbc:postgresql://localhost:5432/store_db";
        String user = "postgres";
        String password = "Anuvardhitha@0608";

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "INSERT INTO product (name, price, quantity, min_stock_level) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, quantity);
            ps.setInt(4, minStock);

            int rows = ps.executeUpdate();

            out.println("<h2>Product added successfully!</h2>");
            out.println("<p><a href='index.html'>Add another</a> | <a href='viewproducts'>View all products</a></p>");

            conn.close();
        } catch (Exception e) {
            out.println("<h2>Error adding product</h2>");
            e.printStackTrace();
        }
    }
}