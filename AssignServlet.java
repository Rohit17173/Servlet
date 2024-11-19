import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import com.google.gson.*;

@WebServlet("/crud")
public class CRUDServlet extends HttpServlet {

    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/crud_app";
        String user = "root";
        String password = "yourpassword";
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM items")) {

            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                JsonObject item = new JsonObject();
                item.addProperty("id", rs.getInt("id"));
                item.addProperty("name", rs.getString("name"));
                jsonArray.add(item);
            }
            response.getWriter().println(jsonArray.toString());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving items: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        if (name == null || name.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Name is required");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO items (name) VALUES (?)")) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            response.getWriter().write("Item added successfully");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error adding item: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID is required");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM items WHERE id = ?")) {
            pstmt.setInt(1, Integer.parseInt(id));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                response.getWriter().write("Item deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Item not found");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error deleting item: " + e.getMessage());
        }
    }
}
