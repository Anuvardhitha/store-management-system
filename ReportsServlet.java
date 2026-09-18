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

        "*{box-sizing:border-box;}" +

        "body{" +
        "margin:0;" +
        "min-height:100vh;" +
        "font-family:'Segoe UI',Arial,sans-serif;" +
        "color:#3d2922;" +
        "background:" +
        "radial-gradient(circle at 5% 5%,#ffe5b8 0,transparent 24%)," +
        "radial-gradient(circle at 95% 10%,#ffdcd8 0,transparent 26%)," +
        "radial-gradient(circle at 80% 90%,#e9ddff 0,transparent 25%)," +
        "linear-gradient(135deg,#fffaf3,#fff0e8);" +
        "padding:35px 24px 60px;" +
        "}" +

        ".page{" +
        "width:94%;" +
        "max-width:1200px;" +
        "margin:auto;" +
        "}" +

        ".header{" +
        "text-align:center;" +
        "margin-bottom:32px;" +
        "}" +

        ".badge{" +
        "display:inline-block;" +
        "padding:8px 18px;" +
        "border-radius:30px;" +
        "background:white;" +
        "color:#ef6b4e;" +
        "font-size:13px;" +
        "font-weight:800;" +
        "box-shadow:0 8px 25px rgba(80,45,20,.08);" +
        "}" +

        ".header h1{" +
        "font-size:clamp(36px,5vw,56px);" +
        "margin:14px 0 8px;" +
        "color:#4b2e24;" +
        "letter-spacing:-1.5px;" +
        "}" +

        ".header h1 span{color:#ef6b4e;}" +

        ".header p{" +
        "margin:0;" +
        "color:#776963;" +
        "font-size:16px;" +
        "}" +

        ".grid{" +
        "display:grid;" +
        "grid-template-columns:1fr 1fr;" +
        "gap:22px;" +
        "}" +

        ".section{" +
        "background:rgba(255,255,255,.95);" +
        "border-radius:24px;" +
        "padding:26px;" +
        "box-shadow:0 18px 45px rgba(80,45,20,.10);" +
        "border-top:5px solid #ef6b4e;" +
        "}" +

        ".section:nth-child(2){border-color:#63b8a7;}" +
        ".section:nth-child(3){border-color:#f4b740;}" +
        ".section:nth-child(4){border-color:#9b72d8;}" +

        ".full{" +
        "grid-column:1/-1;" +
        "}" +

        ".section-title{" +
        "display:flex;" +
        "align-items:center;" +
        "gap:12px;" +
        "margin-bottom:20px;" +
        "}" +

        ".icon{" +
        "width:45px;" +
        "height:45px;" +
        "border-radius:14px;" +
        "display:flex;" +
        "align-items:center;" +
        "justify-content:center;" +
        "background:#fff0e9;" +
        "color:#ef6b4e;" +
        "font-size:22px;" +
        "font-weight:900;" +
        "}" +

        ".section h2{" +
        "margin:0;" +
        "color:#4b2e24;" +
        "font-size:22px;" +
        "}" +

        ".section-subtitle{" +
        "margin:3px 0 0;" +
        "color:#8a7770;" +
        "font-size:13px;" +
        "}" +

        ".report-list{" +
        "display:flex;" +
        "flex-direction:column;" +
        "gap:10px;" +
        "}" +

        ".report-item{" +
        "display:flex;" +
        "justify-content:space-between;" +
        "align-items:center;" +
        "gap:20px;" +
        "padding:15px 17px;" +
        "border:1px solid #f0e2d9;" +
        "border-radius:14px;" +
        "background:#fffaf6;" +
        "transition:.2s;" +
        "}" +

        ".report-item:hover{" +
        "transform:translateX(3px);" +
        "background:#fff4ed;" +
        "border-color:#f2cabb;" +
        "}" +

        ".item-name{" +
        "font-weight:700;" +
        "color:#4b362d;" +
        "}" +

        ".item-value{" +
        "font-weight:800;" +
        "color:#ef6b4e;" +
        "white-space:nowrap;" +
        "}" +

        ".stock-item{" +
        "display:block;" +
        "padding:17px;" +
        "border-radius:15px;" +
        "background:#fffaf6;" +
        "border:1px solid #eee0d7;" +
        "}" +

        ".stock-name{" +
        "font-size:16px;" +
        "font-weight:800;" +
        "color:#4b2e24;" +
        "margin-bottom:7px;" +
        "}" +

        ".stock-info{" +
        "color:#776963;" +
        "font-size:14px;" +
        "line-height:1.6;" +
        "}" +

        ".empty{" +
        "padding:25px;" +
        "text-align:center;" +
        "color:#8a7770;" +
        "background:#fffaf6;" +
        "border-radius:15px;" +
        "border:1px dashed #dfcfc6;" +
        "}" +

        ".bottom{" +
        "display:flex;" +
        "justify-content:center;" +
        "gap:15px;" +
        "margin-top:28px;" +
        "flex-wrap:wrap;" +
        "}" +

        ".button{" +
        "display:inline-block;" +
        "padding:13px 22px;" +
        "border-radius:13px;" +
        "background:linear-gradient(135deg,#ef6b4e,#f58a68);" +
        "color:white;" +
        "font-weight:800;" +
        "text-decoration:none;" +
        "box-shadow:0 9px 20px rgba(239,107,78,.20);" +
        "transition:.2s;" +
        "}" +

        ".button:hover{" +
        "transform:translateY(-2px);" +
        "box-shadow:0 13px 25px rgba(239,107,78,.28);" +
        "}" +

        ".button.secondary{" +
        "background:white;" +
        "color:#ef6b4e;" +
        "border:1px solid #f1cfc2;" +
        "box-shadow:0 6px 18px rgba(80,45,20,.07);" +
        "}" +

        ".footer{" +
        "text-align:center;" +
        "margin-top:28px;" +
        "color:#98877f;" +
        "font-size:13px;" +
        "}" +

        "@media(max-width:800px){" +
        ".grid{grid-template-columns:1fr;}" +
        ".full{grid-column:auto;}" +
        ".page{width:97%;}" +
        "}" +

        "@media(max-width:500px){" +
        "body{padding:20px 12px 45px;}" +
        ".section{padding:20px;}" +
        ".report-item{align-items:flex-start;flex-direction:column;gap:5px;}" +
        "}" +

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
