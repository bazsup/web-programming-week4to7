/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sit.int303.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import sit.int303.demo.model.Product;
import sit.int303.demo.model.controller.ProductJpaController;

/**
 *
 * @author bas
 */
public class ProductListServelet extends HttpServlet {
  @PersistenceUnit(unitName = "DemoWebAppG2PU")
  EntityManagerFactory emf;
  
  @Resource
  UserTransaction utx;
  
  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(final HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String productName = request.getParameter("productName");
    ProductJpaController productJpaCtrl = new ProductJpaController(utx, emf);
    if (productName == null) {
      List<Product> products = null;
      Cookie cookies[] = request.getCookies();
      products = productJpaCtrl.findProductEntities();
      if(cookies != null) {
        for (Cookie ck : cookies) {
          if (ck.getName().equalsIgnoreCase("lastKey")) {
            products = productJpaCtrl.findByProductName(ck.getValue());
            break;
          }
        }
      }
      request.setAttribute("products", products);
    } else {
      List<Product> products = productJpaCtrl.findByProductName(productName);
      if (products.isEmpty()) {
        request.setAttribute("message", "Product " + productName + " does not exist!!!");
      } else {
        request.setAttribute("products", products);
        Cookie ck = new Cookie("lastKey", productName);
//        ck.setMaxAge(7 * 24 * 60 * 60); //cookie 1 week
        response.addCookie(ck);
      }

    }
    
    getServletContext().getRequestDispatcher("/ProductList.jsp").forward(request, response);
//    getServletContext().getRequestDispatcher("/ProductListUsingDatatable.jsp").forward(request, response);
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
