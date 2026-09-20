# Store Management System
Store Management System is a web application I built to manage basic store activities such as products, billing, inventory, restocking, and reports.
The main idea was to bring these operations into one place instead of managing them separately.
## What the application does
- Add and view products
- Manage available stock
- Create customer bills
- Automatically update stock after billing
- Add received stock through the restocking section
- Track stock movements
- View sales and inventory reports
- Register and log in as a store owner
## Reports
The Reports section uses the data stored in PostgreSQL to give a simple view of the store's performance.
It includes:
- Daily revenue
- Weekly revenue
- Most sold products
- Current stock
- Average daily sales
- Estimated number of days the available stock can last
The stock estimation is calculated using the recorded sales data, which can help identify products that may need to be restocked.
## Technologies Used
- Java
- Java Servlets
- JDBC
- PostgreSQL
- HTML
- CSS
- Apache Tomcat
- Docker
- Render
## How it works
The frontend is built using HTML and CSS. User requests are handled by Java Servlets, which communicate with the PostgreSQL database through JDBC.
```text
HTML/CSS
   ↓
Java Servlets
   ↓
JDBC
   ↓
PostgreSQL
