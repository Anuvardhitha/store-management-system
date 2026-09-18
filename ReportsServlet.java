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
"<style>" +
"*{box-sizing:border-box}" +
"body{margin:0;min-height:100vh;font-family:'Segoe UI',Arial,sans-serif;color:#3d2922;background:radial-gradient(circle at 5% 5%,#ffe5b8 0,transparent 24%),radial-gradient(circle at 95% 10%,#ffdcd8 0,transparent 26%),radial-gradient(circle at 80% 90%,#e9ddff 0,transparent 25%),linear-gradient(135deg,#fffaf3,#fff0e8);padding:35px 24px 60px}" +
".container{width:94%;max-width:1150px;margin:auto}" +
".section{background:rgba(255,255,255,.96);border-radius:26px;padding:32px;box-shadow:0 18px 50px rgba(80,45,20,.11);border-top:5px solid #ef6b4e}" +
"h2{font-size:42px;text-align:center;color:#4b2e24;margin:0 0 8px}" +
"h2:after{content:'Store Analytics';display:block;font-size:14px;font-weight:700;color:#ef6b4e;letter-spacing:2px;text-transform:uppercase;margin-top:8px}" +
"h3{color:#4b2e24;font-size:21px;margin:32px 0 14px;padding-left:14px;border-left:5px solid #ef6b4e}" +
"ul{list-style:none;padding:0;margin:0 0 25px;display:grid;gap:10px}" +
"li{padding:16px 20px;background:#fffaf6;border:1px solid #f0e0d6;border-radius:14px;color:#503b32;box-shadow:0 5px 15px rgba(80,45,20,.04);transition:.2s}" +
"li:hover{transform:translateX(4px);background:#fff3ec;border-color:#f1c6b5}" +
"a{display:inline-block;margin-top:10px;padding:13px 22px;background:linear-gradient(135deg,#ef6b4e,#f58a68);color:white;text-decoration:none;font-weight:800;border-radius:13px;box-shadow:0 8px 20px rgba(239,107,78,.2)}" +
"a:hover{transform:translateY(-2px)}" +
"</style>";
    private Connection getConnection() throws Exception {

        Class.forName("org.postgresql.Driver");

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Store Reports</title>");
        out.println(CSS);
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='page'>");

        out.println("<div class='header'>");
        out.println("<div class='badge'>STORE ANALYTICS</div>");
        out.println("<h1>Store <span>Reports</span></h1>");
        out.println("<p>Track revenue, products and inventory performance.</p>");
        out.println("</div>");

        out.println("<div class='grid'>");

        try {

            Connection conn = getConnection();

            /*
             * DAILY REVENUE
             */

            out.println("<div class='section'>");

            out.println("<div class='section-title'>");
            out.println("<div class='icon'>$</div>");
            out.println("<div>");
            out.println("<h2>Daily Revenue</h2>");
            out.println("<div class='section-subtitle'>Revenue generated each day</div>");
            out.println("</div>");
            out.println("</div>");

            out.println("<div class='report-list'>");

            String dailySql =
                "SELECT DATE(bill_date) AS sale_day, " +
                "SUM(total_amount) AS revenue " +
                "FROM bill " +
                "GROUP BY DATE(bill_date) " +
                "ORDER BY sale_day";

            PreparedStatement dailyPs =
                conn.prepareStatement(dailySql);

            ResultSet dailyRs =
                dailyPs.executeQuery();

            boolean dailyFound = false;

            while (dailyRs.next()) {

                dailyFound = true;

                String date =
                    dailyRs.getDate("sale_day").toString();

                double revenue =
                    dailyRs.getDouble("revenue");

                out.println(
                    "<div class='report-item'>" +
                    "<span class='item-name'>" +
                    date +
                    "</span>" +
                    "<span class='item-value'>" +
                    String.format("%.2f", revenue) +
                    "</span>" +
                    "</div>"
                );
            }

            if (!dailyFound) {
                out.println(
                    "<div class='empty'>No daily sales data available.</div>"
                );
            }

            out.println("</div>");
            out.println("</div>");

            /*
             * WEEKLY REVENUE
             */

            out.println("<div class='section'>");

            out.println("<div class='section-title'>");
            out.println("<div class='icon'>W</div>");
            out.println("<div>");
            out.println("<h2>Weekly Revenue</h2>");
            out.println("<div class='section-subtitle'>Revenue grouped by week</div>");
            out.println("</div>");
            out.println("</div>");

            out.println("<div class='report-list'>");

            String weeklySql =
                "SELECT DATE_TRUNC('week', bill_date) AS week_start, " +
                "SUM(total_amount) AS revenue " +
                "FROM bill " +
                "GROUP BY DATE_TRUNC('week', bill_date) " +
                "ORDER BY week_start";

            PreparedStatement weeklyPs =
                conn.prepareStatement(weeklySql);

            ResultSet weeklyRs =
                weeklyPs.executeQuery();

            boolean weeklyFound = false;

            while (weeklyRs.next()) {

                weeklyFound = true;

                String week =
                    weeklyRs.getDate("week_start").toString();

                double revenue =
                    weeklyRs.getDouble("revenue");

                out.println(
                    "<div class='report-item'>" +
                    "<span class='item-name'>Week of " +
                    week +
                    "</span>" +
                    "<span class='item-value'>" +
                    String.format("%.2f", revenue) +
                    "</span>" +
                    "</div>"
                );
            }

            if (!weeklyFound) {
                out.println(
                    "<div class='empty'>No weekly sales data available.</div>"
                );
            }

            out.println("</div>");
            out.println("</div>");

            /*
             * MOST SOLD PRODUCTS
             */

            out.println("<div class='section full'>");

            out.println("<div class='section-title'>");
            out.println("<div class='icon'>P</div>");
            out.println("<div>");
            out.println("<h2>Most Sold Products</h2>");
            out.println("<div class='section-subtitle'>Products ranked by units sold</div>");
            out.println("</div>");
            out.println("</div>");

            out.println("<div class='report-list'>");

            String soldSql =
                "SELECT p.name, SUM(bi.quantity) AS total_sold " +
                "FROM bill_item bi " +
                "JOIN product p ON bi.product_id = p.product_id " +
                "GROUP BY p.name " +
                "ORDER BY total_sold DESC";

            PreparedStatement soldPs =
                conn.prepareStatement(soldSql);

            ResultSet soldRs =
                soldPs.executeQuery();

            boolean soldFound = false;

            while (soldRs.next()) {

                soldFound = true;

                String name =
                    escapeHtml(soldRs.getString("name"));

                int totalSold =
                    soldRs.getInt("total_sold");

                out.println(
                    "<div class='report-item'>" +
                    "<span class='item-name'>" +
                    name +
                    "</span>" +
                    "<span class='item-value'>" +
                    totalSold +
                    " units sold</span>" +
                    "</div>"
                );
            }

            if (!soldFound) {
                out.println(
                    "<div class='empty'>No product sales recorded yet.</div>"
                );
            }

            out.println("</div>");
            out.println("</div>");

            /*
             * RESTOCKING RECOMMENDATIONS
             */

            out.println("<div class='section full'>");

            out.println("<div class='section-title'>");
            out.println("<div class='icon'>R</div>");
            out.println("<div>");
            out.println("<h2>Restocking Recommendations</h2>");
            out.println("<div class='section-subtitle'>Estimated inventory availability</div>");
            out.println("</div>");
            out.println("</div>");

            out.println("<div class='report-list'>");

            String stockoutSql =
                "SELECT p.product_id, p.name, p.quantity, " +
                "COALESCE(SUM(bi.quantity), 0) AS total_sold, " +
                "COUNT(DISTINCT DATE(b.bill_date)) AS days_active " +
                "FROM product p " +
                "LEFT JOIN bill_item bi ON p.product_id = bi.product_id " +
                "LEFT JOIN bill b ON bi.bill_id = b.bill_id " +
                "GROUP BY p.product_id, p.name, p.quantity";

            PreparedStatement stockoutPs =
                conn.prepareStatement(stockoutSql);

            ResultSet stockoutRs =
                stockoutPs.executeQuery();

            boolean stockFound = false;

            while (stockoutRs.next()) {

                stockFound = true;

                String name =
                    escapeHtml(stockoutRs.getString("name"));

                int quantity =
                    stockoutRs.getInt("quantity");

                int totalSold =
                    stockoutRs.getInt("total_sold");

                int daysActive =
                    stockoutRs.getInt("days_active");

                out.println("<div class='stock-item'>");

                out.println(
                    "<div class='stock-name'>" +
                    name +
                    "</div>"
                );

                if (totalSold == 0 || daysActive == 0) {

                    out.println(
                        "<div class='stock-info'>" +
                        "No sales yet — an estimate cannot be calculated." +
                        "</div>"
                    );

                } else {

                    double avgDailySales =
                        (double) totalSold / daysActive;

                    double daysLeft =
                        quantity / avgDailySales;

                    out.println(
                        "<div class='stock-info'>" +
                        "Current stock: <strong>" +
                        quantity +
                        "</strong>" +
                        " &nbsp; • &nbsp; " +
                        "Average daily sales: <strong>" +
                        String.format("%.2f", avgDailySales) +
                        "</strong>" +
                        " &nbsp; • &nbsp; " +
                        "Estimated days remaining: <strong>" +
                        String.format("%.1f", daysLeft) +
                        "</strong>" +
                        "</div>"
                    );
                }

                out.println("</div>");
            }

            if (!stockFound) {

                out.println(
                    "<div class='empty'>" +
                    "No products are available for stock analysis." +
                    "</div>"
                );
            }

            out.println("</div>");
            out.println("</div>");

            conn.close();

        } catch (Exception e) {

            out.println(
                "<div class='section full'>" +
                "<div class='empty'>" +
                "Unable to load reports right now. Please try again." +
                "</div>" +
                "</div>"
            );

            e.printStackTrace();
        }

        out.println("</div>");

        out.println("<div class='bottom'>");

        out.println(
            "<a class='button' href='dashboard.html'>" +
            "Back to Store" +
            "</a>"
        );

        out.println(
            "<a class='button secondary' href='viewproducts'>" +
            "View Products" +
            "</a>"
        );

        out.println("</div>");

        out.println(
            "<div class='footer'>" +
            "StoreApp • Smart inventory • Simple insights" +
            "</div>"
        );

        out.println("</div>");

        out.println("</body></html>");
    }
}
