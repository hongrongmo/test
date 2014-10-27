package org.ei.system;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckDataService extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();

        CheckEV cEV = new CheckEV();
        cEV.init();
        String server = request.getServerName();
        cEV.setServer(server);

        boolean flag = cEV.checkSearchDatabase();
        if (flag) {
            out.println("UP");
        } else {
            out.println("DOWN");
        }

    }

}
