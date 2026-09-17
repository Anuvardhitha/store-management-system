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

@WebServlet("/createbill")
public class CreateBillServlet extends HttpServlet {
private static final String CSS =
    "<style>* { box-sizing: border-box; } body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: linear-gradient(135deg, #eef4ff 0%, #f7f7f8 40%); color: #1a1a1a; margin: 0; padding: 40px 20px; line-height: 1.5; } .container { max-width: 700px; margin: 0 auto; } h2,h3 { color: #111; } .section { background: #ffffff; border: 1px solid #e5e5e5; border-left: 4px solid #4f7cff; border-radius: 10px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.04); } table { width: 100%; border-collapse: collapse; } th, td { padding: 8px 10px; text-align: left; border-bottom: 1px solid #eee; } th { color: #555; font-size: 13px; } input[type='text'] { padding: 8px 10px; font-size: 14px; border: 1px solid #d5d5d5; border-radius: 6px; margin: 4px 0; } input[type='submit'], button { padding: 10px 16px; background-color: #4f7cff; color: #fff; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; } a { color: #4f7cff; text-decoration: none; font-weight: 500; border-bottom: 1px solid #cddcff; } </style>";

    // GET → show the product list + billing form
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

       String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";        
out.println("<html><head><title>Create Bill</title>" + CSS + "</head><body>");
out.println("<div class='container'><div class='section'>");
        out.println("<h2>Available Products</h2>");
        out.println("<table border='1' cellpadding='6'>");
        out.println("<tr><th>ID</th><th>Name</th><th>Price</th><th>Available Qty</th></tr>");

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "SELECT * FROM product ORDER BY product_id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("product_id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getDouble("price") + "</td>");
                out.println("<td>" + rs.getInt("quantity") + "</td>");
                out.println("</tr>");
            }
            conn.close();
        } catch (Exception e) {
            out.println("<tr><td colspan='4'>Error loading products: " + e.getMessage() + "</td></tr>");
        }

        out.println("</table>");

        out.println("<h2>Create Bill</h2>");
out.println("<form action='createbill' method='post'>");
out.println("<table id='itemsTable' border='1' cellpadding='5'>");
out.println("<tr><th>Product ID</th><th>Quantity</th></tr>");
out.println("<tr><td><input type='text' name='productId'></td><td><input type='text' name='quantity'></td></tr>");
out.println("</table><br>");
out.println("<button type='button' onclick='addRow()'>Add Another Item</button><br><br>");
out.println("<input type='submit' value='Create Bill'>");
out.println("</form>");

out.println("<script>");
out.println("function addRow() {");
out.println("  var table = document.getElementById('itemsTable');");
out.println("  var row = table.insertRow(-1);");
out.println("  row.innerHTML = \"<td><input type='text' name='productId'></td><td><input type='text' name='quantity'></td></tr>\";");
out.println("}");
out.println("</script>");        
out.println("<p><a href='index.html'>Back to menu</a></p>");
        out.println("</div></div></body></html>");    }

    // POST → actually process the bill (unchanged from before)
   

        protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    String[] productIdParams = request.getParameterValues("productId");
    String[] quantityParams = request.getParameterValues("quantity");

      String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";
    out.println("<html><head><title>Create Bill</title>" + CSS + "</head><body>");
    out.println("<div class='container'><div class='section'>");
    try {Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);

        double billTotal = 0;
        java.util.List<Integer> validProductIds = new java.util.ArrayList<>();
        java.util.List<Integer> validQuantities = new java.util.ArrayList<>();
        java.util.List<Double> validPrices = new java.util.ArrayList<>();

        for (int i = 0; i < productIdParams.length; i++) {
            if (productIdParams[i].trim().isEmpty() || quantityParams[i].trim().isEmpty()) {
                continue;
            }

            int productId = Integer.parseInt(productIdParams[i]);
            int qty = Integer.parseInt(quantityParams[i]);

            String checkSql = "SELECT price, quantity FROM product WHERE product_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, productId);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                out.println("<p>Product ID " + productId + " not found, skipped.</p>");
                continue;
            }

            double price = rs.getDouble("price");
            int availableQty = rs.getInt("quantity");

            if (qty > availableQty) {
                out.println("<p>Not enough stock for product " + productId + ", skipped.</p>");
                continue;
            }

            billTotal += price * qty;
            validProductIds.add(productId);
            validQuantities.add(qty);
            validPrices.add(price);
        }

               if (validQuantities.isEmpty()) {
            out.println("<h2>No valid items. Bill cancelled.</h2>");
            out.println("</div></div></body></html>");
            conn.rollback();
            return;
        }
        String billSql = "INSERT INTO bill (total_amount) VALUES (?) RETURNING bill_id";
        PreparedStatement billPs = conn.prepareStatement(billSql);
        billPs.setDouble(1, billTotal);
        ResultSet billRs = billPs.executeQuery();
        billRs.next();
        int billId = billRs.getInt("bill_id");

        for (int i = 0; i < validQuantities.size(); i++) {
            int productId = validProductIds.get(i);
            int qty = validQuantities.get(i);
            double price = validPrices.get(i);
            double subtotal = price * qty;

            String itemSql = "INSERT INTO bill_item (bill_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            itemPs.setInt(1, billId);
            itemPs.setInt(2, productId);
            itemPs.setInt(3, qty);
            itemPs.setDouble(4, price);
            itemPs.setDouble(5, subtotal);
            itemPs.executeUpdate();

            String updateSql = "UPDATE product SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, qty);
            updatePs.setInt(2, productId);
            updatePs.executeUpdate();

            String movementSql = "INSERT INTO stock_movement (product_id, change_type, quantity, reference_id) VALUES (?, ?, ?, ?)";
            PreparedStatement movementPs = conn.prepareStatement(movementSql);
            movementPs.setInt(1, productId);
            movementPs.setString(2, "SALE");
            movementPs.setInt(3, -qty);
            movementPs.setInt(4, billId);
            movementPs.executeUpdate();
        }

        conn.commit();
               out.println("<h2>Bill created! Bill ID: " + billId + " | Total: " + billTotal + "</h2>");
        out.println("<p><a href='createbill'>Create another bill</a> | <a href='index.html'>Back to menu</a></p>");
        out.println("</div></div></body></html>");

        conn.close();
        } catch (Exception e) {
        out.println("<h2>Error creating bill: " + e.getMessage() + "</h2>");
        out.println("</div></div></body></html>");
    }}
}