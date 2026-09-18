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
        "body{margin:0;min-height:100vh;font-family:'Segoe UI',Arial,sans-serif;color:#4b2e24;background:linear-gradient(135deg,#fff8f2,#ffe9e3 55%,#fff3ec);}" +
        ".page{min-height:100vh;display:grid;grid-template-columns:220px 1fr;}" +
        ".sidebar{background:rgba(255,255,255,.82);border-right:1px solid #f3d8d0;padding:24px 16px;position:sticky;top:0;height:100vh;}" +
        ".brand{display:flex;align-items:center;gap:10px;margin:0 8px 35px;font-weight:800;color:#4b2e24;}" +
        ".brand-icon{width:40px;height:40px;border-radius:12px;background:#ffd8d3;display:flex;align-items:center;justify-content:center;font-size:22px;}" +
        ".brand small{display:block;color:#9b7165;font-size:12px;font-weight:600;margin-top:3px;}" +
        ".nav a{display:block;text-decoration:none;color:#633d32;font-weight:600;padding:13px 14px;border-radius:12px;margin:5px 0;}" +
        ".nav a:hover{background:#fff0ed;}" +
        ".nav a.active{background:#ffdcd7;color:#5a2118;border-left:4px solid #ef6b4e;padding-left:10px;}" +
        ".content{padding:22px 34px 30px;position:relative;overflow:hidden;}" +
        ".topbar{background:rgba(255,255,255,.68);border:1px solid rgba(255,255,255,.8);border-radius:20px;padding:15px 22px;margin-bottom:18px;display:flex;justify-content:space-between;align-items:center;}" +
        ".top-title{font-size:26px;font-weight:800;color:#3f241c;margin:0;}" +
        ".subtitle{margin:4px 0 0;color:#8a6156;font-size:14px;}" +
        ".admin{background:#fff;border:2px solid #f4ddd8;border-radius:24px;padding:10px 17px;font-weight:700;color:#5b2b20;}" +
        ".decor{position:absolute;right:-30px;top:80px;width:220px;height:700px;pointer-events:none;opacity:.95;}" +
        ".drip{position:absolute;right:0;top:0;width:110px;height:150px;background:#5a2d20;border-radius:0 0 55px 55px;box-shadow:-55px 20px 0 -18px #6a3527;}" +
        ".choco{position:absolute;width:58px;height:38px;background:#713b2b;border:4px solid #4b271d;border-radius:7px;transform:rotate(-18deg);box-shadow:inset 8px 6px 0 #8e4d37;}" +
        ".choco.one{right:28px;top:210px}.choco.two{right:105px;top:510px;transform:rotate(15deg)}" +
        ".cookie{position:absolute;width:92px;height:92px;border-radius:50%;background:#d89a5e;border:5px solid #b9783e;box-shadow:inset 0 0 0 5px #e5aa6d;}" +
        ".cookie:before,.cookie:after{content:'';position:absolute;width:12px;height:12px;border-radius:50%;background:#613522;box-shadow:35px 14px 0 #613522,12px 45px 0 #613522,55px 55px 0 #613522;}" +
        ".cookie:before{left:12px;top:14px}.cookie:after{left:4px;top:32px;transform:scale(.7)}" +
        ".cookie.one{right:24px;top:620px}.cookie.two{left:-18px;bottom:25px;transform:scale(.75)}" +
        ".quote{position:absolute;right:12px;top:310px;width:125px;text-align:center;color:#a85c48;font-family:cursive;font-size:20px;line-height:1.15;}" +
        ".grid{width:min(100%,1040px);margin:0 auto;display:grid;gap:16px;position:relative;z-index:1;}" +
        ".section{background:rgba(255,255,255,.92);border:1px solid #f4ddd8;border-radius:22px;padding:20px 22px;box-shadow:0 12px 30px rgba(102,57,40,.09);}" +
        ".section.full{grid-column:auto;}" +
        ".section-title{display:flex;align-items:center;gap:14px;margin-bottom:14px;}" +
        ".icon{width:50px;height:50px;border-radius:50%;background:#ffdcd8;color:#a62f20;display:flex;align-items:center;justify-content:center;font-weight:900;font-size:21px;flex:0 0 50px;}" +
        "h2{font-size:28px;color:#3f241c;margin:0 0 3px;text-align:left;}" +
        ".section-subtitle{font-size:14px;color:#8b5d52;}" +
        ".report-list{border:1px solid #f0e0db;border-radius:12px;overflow:hidden;background:#fff;}" +
        ".report-item{display:grid;grid-template-columns:1fr 1fr;padding:9px 14px;border-bottom:1px solid #f1e7e3;font-size:14px;}" +
        ".report-item:first-child{background:#ffe9e6;font-weight:700;color:#6b241b;}" +
        ".report-item:last-child{border-bottom:0;}" +
        ".report-item:hover{background:#fff7f5;}" +
        ".item-name{text-align:left}.item-value{text-align:right;font-weight:600;}" +
        ".stock-item{padding:13px 15px;border-bottom:1px solid #f1e7e3;}.stock-item:last-child{border-bottom:0}.stock-name{font-weight:800;margin-bottom:5px}.stock-info{font-size:13px;color:#8a6258;}" +
        ".empty{padding:15px;color:#8a6258;}" +
        ".bottom{display:flex;gap:10px;justify-content:center;margin:20px 0;position:relative;z-index:1;}" +
        ".button{display:inline-block;text-decoration:none;background:#ef6b4e;color:#fff;padding:11px 18px;border-radius:12px;font-weight:700;box-shadow:0 7px 18px rgba(239,107,78,.18);}" +
        ".button.secondary{background:#fff;color:#8b3e2e;border:1px solid #efcfc7;}" +
        ".footer{text-align:center;color:#a47a6e;font-size:12px;margin-top:10px;position:relative;z-index:1;}" +
        "@media(max-width:850px){.page{grid-template-columns:1fr}.sidebar{position:relative;height:auto;padding:12px}.nav{display:flex;flex-wrap:wrap}.nav a{padding:9px 10px}.content{padding:16px}.decor{display:none}.topbar{align-items:flex-start;gap:12px}.admin{display:none}}" +
        "</style>";

    private Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Store Reports</title>");
        out.println(CSS);
        out.println("</head><body>");

        out.println("<div class='page'>");

        out.println("<aside class='sidebar'>");
        out.println("<div class='brand'><div class='brand-icon'>S</div><div>Store Management<small>Manage Smarter</small></div></div>");
        out.println("<nav class='nav'>");
        out.println("<a href='dashboard.html'>Dashboard</a>");
        out.println("<a href='addproduct.html'>Add Product</a>");
        out.println("<a href='viewproducts'>View Products</a>");
        out.println("<a href='restock.html'>Restock</a>");
        out.println("<a href='createbill.html'>Create Bill</a>");
        out.println("<a class='active' href='reports'>Reports</a>");
        out.println("<a href='index.html'>Logout</a>");
        out.println("</nav>");
        out.println("</aside>");

        out.println("<main class='content'>");
        out.println("<div class='topbar'>");
        out.println("<div><div class='top-title'>Store Reports</div><div class='subtitle'>Track revenue, products and inventory performance.</div></div>");
        out.println("<div class='admin'>Welcome, Admin</div>");
        out.println("</div>");

        out.println("<div class='decor'><div class='drip'></div><div class='choco one'></div><div class='choco two'></div><div class='cookie one'></div><div class='cookie two'></div><div class='quote'>Life is<br>Sweeter<br>with<br>Good Snacks</div></div>");

        out.println("<div class='grid'>");

        try {
            Connection conn = getConnection();

            out.println("<div class='section'>");
            out.println("<div class='section-title'><div class='icon'>$</div><div><h2>Daily Revenue</h2><div class='section-subtitle'>Revenue generated each day</div></div></div>");
            out.println("<div class='report-list'>");
            out.println("<div class='report-item'><span class='item-name'>Date</span><span class='item-value'>Revenue (&#8377;)</span></div>");

            String dailySql =
                "SELECT DATE(bill_date) AS sale_day, SUM(total_amount) AS revenue " +
                "FROM bill GROUP BY DATE(bill_date) ORDER BY sale_day";
            PreparedStatement dailyPs = conn.prepareStatement(dailySql);
            ResultSet dailyRs = dailyPs.executeQuery();
            boolean dailyFound = false;

            while (dailyRs.next()) {
                dailyFound = true;
                String date = dailyRs.getDate("sale_day").toString();
                double revenue = dailyRs.getDouble("revenue");
                out.println("<div class='report-item'><span class='item-name'>" +
                    date + "</span><span class='item-value'>" +
                    String.format("%.2f", revenue) + "</span></div>");
            }
            if (!dailyFound) out.println("<div class='empty'>No daily sales data available.</div>");
            out.println("</div></div>");

            out.println("<div class='section'>");
            out.println("<div class='section-title'><div class='icon'>W</div><div><h2>Weekly Revenue</h2><div class='section-subtitle'>Revenue grouped by week</div></div></div>");
            out.println("<div class='report-list'>");
            out.println("<div class='report-item'><span class='item-name'>Week</span><span class='item-value'>Revenue (&#8377;)</span></div>");

            String weeklySql =
                "SELECT DATE_TRUNC('week', bill_date) AS week_start, SUM(total_amount) AS revenue " +
                "FROM bill GROUP BY DATE_TRUNC('week', bill_date) ORDER BY week_start";
            PreparedStatement weeklyPs = conn.prepareStatement(weeklySql);
            ResultSet weeklyRs = weeklyPs.executeQuery();
            boolean weeklyFound = false;

            while (weeklyRs.next()) {
                weeklyFound = true;
                String week = weeklyRs.getDate("week_start").toString();
                double revenue = weeklyRs.getDouble("revenue");
                out.println("<div class='report-item'><span class='item-name'>Week of " +
                    week + "</span><span class='item-value'>" +
                    String.format("%.2f", revenue) + "</span></div>");
            }
            if (!weeklyFound) out.println("<div class='empty'>No weekly sales data available.</div>");
            out.println("</div></div>");

            out.println("<div class='section'>");
            out.println("<div class='section-title'><div class='icon'>P</div><div><h2>Most Sold Products</h2><div class='section-subtitle'>Products ranked by units sold</div></div></div>");
            out.println("<div class='report-list'>");
            out.println("<div class='report-item'><span class='item-name'>Product</span><span class='item-value'>Quantity Sold</span></div>");

            String soldSql =
                "SELECT p.name, SUM(bi.quantity) AS total_sold " +
                "FROM bill_item bi JOIN product p ON bi.product_id = p.product_id " +
                "GROUP BY p.name ORDER BY total_sold DESC";
            PreparedStatement soldPs = conn.prepareStatement(soldSql);
            ResultSet soldRs = soldPs.executeQuery();
            boolean soldFound = false;

            while (soldRs.next()) {
                soldFound = true;
                String name = escapeHtml(soldRs.getString("name"));
                int totalSold = soldRs.getInt("total_sold");
                out.println("<div class='report-item'><span class='item-name'>" +
                    name + "</span><span class='item-value'>" +
                    totalSold + " units</span></div>");
            }
            if (!soldFound) out.println("<div class='empty'>No product sales recorded yet.</div>");
            out.println("</div></div>");

            out.println("<div class='section'>");
            out.println("<div class='section-title'><div class='icon'>R</div><div><h2>Restocking Recommendations</h2><div class='section-subtitle'>Estimated inventory availability</div></div></div>");
            out.println("<div class='report-list'>");

            String stockoutSql =
                "SELECT p.product_id, p.name, p.quantity, COALESCE(SUM(bi.quantity), 0) AS total_sold, " +
                "COUNT(DISTINCT DATE(b.bill_date)) AS days_active " +
                "FROM product p LEFT JOIN bill_item bi ON p.product_id = bi.product_id " +
                "LEFT JOIN bill b ON bi.bill_id = b.bill_id " +
                "GROUP BY p.product_id, p.name, p.quantity";
            PreparedStatement stockoutPs = conn.prepareStatement(stockoutSql);
            ResultSet stockoutRs = stockoutPs.executeQuery();
            boolean stockFound = false;

            while (stockoutRs.next()) {
                stockFound = true;
                String name = escapeHtml(stockoutRs.getString("name"));
                int quantity = stockoutRs.getInt("quantity");
                int totalSold = stockoutRs.getInt("total_sold");
                int daysActive = stockoutRs.getInt("days_active");

                out.println("<div class='stock-item'><div class='stock-name'>" + name + "</div>");

                if (totalSold == 0 || daysActive == 0) {
                    out.println("<div class='stock-info'>No sales yet &mdash; an estimate cannot be calculated.</div>");
                } else {
                    double avgDailySales = (double) totalSold / daysActive;
                    double daysLeft = quantity / avgDailySales;
                    out.println("<div class='stock-info'>Current stock: <strong>" + quantity +
                        "</strong> &nbsp; &bull; &nbsp; Average daily sales: <strong>" +
                        String.format("%.2f", avgDailySales) + "</strong> &nbsp; &bull; &nbsp; " +
                        "Estimated days remaining: <strong>" +
                        String.format("%.1f", daysLeft) + "</strong></div>");
                }
                out.println("</div>");
            }

            if (!stockFound) out.println("<div class='empty'>No products are available for stock analysis.</div>");
            out.println("</div></div>");

            conn.close();

        } catch (Exception e) {
            out.println("<div class='section'><div class='empty'>Unable to load reports right now. Please try again.</div></div>");
            e.printStackTrace();
        }

        out.println("</div>");
        out.println("<div class='bottom'>");
        out.println("<a class='button' href='dashboard.html'>Back to Store</a>");
        out.println("<a class='button secondary' href='viewproducts'>View Products</a>");
        out.println("</div>");
        out.println("<div class='footer'>Store Management System &nbsp;|&nbsp; Made with care</div>");
        out.println("</main></div></body></html>");
    }
}
