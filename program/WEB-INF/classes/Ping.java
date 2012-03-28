/*
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 *
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Servlet Ping returns Pong.
 *
 */
public class Ping extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        out.println("Pong");
    }
}



