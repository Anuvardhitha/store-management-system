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

@WebServlet("/reports")
public class ReportsServlet extends HttpServlet {
private static final String CSS =
    "<style>* { box-sizing: border-box; } body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: linear-gradient(135deg, #eef4ff 0%, #f7f7f8 40%); color: #1a1a1a; margin: 0; padding: 40px 20px; line-height: 1.5; } .container { max-width: 700px; margin: 0 auto; } h2,h3 { color: #111; } .section { background: #ffffff; border: 1px solid #e5e5e5; border-left: 4px solid #4f7cff; border-radius: 10px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 6px rgba(0,0,0,0.04); } table { width: 100%; border-collapse: collapse; } th, td { padding: 8px 10px; text-align: left; border-bottom: 1px solid #eee; } th { color: #555; font-size: 13px; } input[type='text'] { padding: 8px 10px; font-size: 14px; border: 1px solid #d5d5d5; border-radius: 6px; margin: 4px 0; } input[type='submit'], button { padding: 10px 16px; background-color: #4f7cff; color: #fff; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; } a { color: #4f7cff; text-decoration: none; font-weight: 500; border-bottom: 1px solid #cddcff; } </style>";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String url = "jdbc:postgresql://dpg-da3cm36k1f9s73ejv0j0-a.ohio-postgres.render.com:5432/store_db_8wgw";
String user = "store_db_8wgw_user";
String password = "xybXiHQe8UpL4DEEpNXjHd8ujdDvEMjJ";
       out.println("<html><head><title>Reports</title>" + CSS + "</head><body>");
out.println("<div class='container'><div class='section'>");
        out.println("<h2>Reports</h2>");

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            out.println("<h3>Daily Revenue</h3><ul>");
            String dailySql = "SELECT DATE(bill_date) AS sale_day, SUM(total_amount) AS revenue " +
                               "FROM bill GROUP BY DATE(bill_date) ORDER BY sale_day";
            PreparedStatement dailyPs = conn.prepareStatement(dailySql);
            ResultSet dailyRs = dailyPs.executeQuery();
            while (dailyRs.next()) {
                out.println("<li>" + dailyRs.getDate("sale_day") + " | Revenue: " + dailyRs.getDouble("revenue") + "</li>");
            }
            out.println("</ul>");

            out.println("<h3>Weekly Revenue</h3><ul>");
            String weeklySql = "SELECT DATE_TRUNC('week', bill_date) AS week_start, SUM(total_amount) AS revenue " +
                                "FROM bill GROUP BY DATE_TRUNC('week', bill_date) ORDER BY week_start";
            PreparedStatement weeklyPs = conn.prepareStatement(weeklySql);
            ResultSet weeklyRs = weeklyPs.executeQuery();
            while (weeklyRs.next()) {
                out.println("<li>Week of " + weeklyRs.getDate("week_start") + " | Revenue: " + weeklyRs.getDouble("revenue") + "</li>");
            }
            out.println("</ul>");

            out.println("<h3>Most Sold Products</h3><ul>");
            String soldSql = "SELECT p.name, SUM(bi.quantity) AS total_sold " +
                              "FROM bill_item bi JOIN product p ON bi.product_id = p.product_id " +
                              "GROUP BY p.name ORDER BY total_sold DESC";
            PreparedStatement soldPs = conn.prepareStatement(soldSql);
            ResultSet soldRs = soldPs.executeQuery();
            while (soldRs.next()) {
                out.println("<li>" + soldRs.getString("name") + " | Units sold: " + soldRs.getInt("total_sold") + "</li>");
            }
            out.println("</ul>");

            out.println("<h3>Restocking Recommendations</h3><ul>");
            String stockoutSql =
                "SELECT p.product_id, p.name, p.quantity, " +
                "       COALESCE(SUM(bi.quantity), 0) AS total_sold, " +
                "       COUNT(DISTINCT DATE(b.bill_date)) AS days_active " +
                "FROM product p " +
                "LEFT JOIN bill_item bi ON p.product_id = bi.product_id " +
                "LEFT JOIN bill b ON bi.bill_id = b.bill_id " +
                "GROUP BY p.product_id, p.name, p.quantity";
            PreparedStatement stockoutPs = conn.prepareStatement(stockoutSql);
            ResultSet stockoutRs = stockoutPs.executeQuery();
            while (stockoutRs.next()) {
                String name = stockoutRs.getString("name");
                int quantity = stockoutRs.getInt("quantity");
                int totalSold = stockoutRs.getInt("total_sold");
                int daysActive = stockoutRs.getInt("days_active");

                if (totalSold == 0 || daysActive == 0) {
                    out.println("<li>" + name + " | No sales yet, cannot estimate.</li>");
                } else {
                    double avgDailySales = (double) totalSold / daysActive;
                    double daysLeft = quantity / avgDailySales;
                    out.println("<li>" + name + " | Avg daily sales: " + String.format("%.2f", avgDailySales) +
                                 " | Est. days until out of stock: " + String.format("%.1f", daysLeft) + "</li>");
                }
            }
            out.println("</ul>");

            conn.close();
        } catch (Exception e) {
            out.println("<p>Error loading reports</p>");
            e.printStackTrace();
        }

        out.println("<p><a href='index.html'>Back to menu</a></p>");
       out.println("</div></div></body></html>");    }
}