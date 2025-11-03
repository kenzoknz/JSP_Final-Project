package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple Hello Servlet
 */
public class HelloServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response content type
        response.setContentType("text/html;charset=UTF-8");
        
        // Get the output stream
        PrintWriter out = response.getWriter();
        
        try {
            // Generate HTML response
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Hello Servlet</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; }");
            out.println("h1 { color: #007bff; }");
            out.println("p { font-size: 18px; color: #666; }");
            out.println("a { color: #007bff; text-decoration: none; }");
            out.println("a:hover { text-decoration: underline; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Hello Servlet!</h1>");
            out.println("<p>This is a simple servlet response.</p>");
            out.println("<p>Server time: " + new java.util.Date() + "</p>");
            out.println("<p><a href='" + request.getContextPath() + "'>Back to Home</a></p>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle POST requests the same way as GET for this simple example
        doGet(request, response);
    }
}