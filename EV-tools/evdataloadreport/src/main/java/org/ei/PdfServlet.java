package org.ei;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class PdfServlet
 */
public class PdfServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PdfServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/*File file = new File("C:\\ws\\EV-tools\\evdataloadreport\\newFileName.pdf");
        response.setHeader("Content-Type", getServletContext().getMimeType(file.getName()));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        //response.setHeader("Content-Disposition", "inline; filename=\"newFileName.pdf\"");   // open pdf direct without prompt to open
        response.setHeader("Content-Disposition", "attachment; filename=\"newFileName.pdf\"");  // open pdf through user prompet to open/cancel
        Files.copy(file.toPath(), response.getOutputStream());*/
        
        response.getWriter().println("Yes here i am in the servlet doGet");
        
      
        
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("Yes here i am in the servlet doPost");
	}

}
