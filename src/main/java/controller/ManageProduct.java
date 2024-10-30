/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.HoaDAO;
import dao.LoaiDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Date;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Hoa;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ManageProduct", urlPatterns = {"/ManageProduct", "/quan-tri"})
@MultipartConfig
public class ManageProduct extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HoaDAO hoaDAO = new HoaDAO();
        LoaiDAO loaiDAO= new LoaiDAO();
        String method = request.getMethod();
        
        String action = "LIST";
        if (request.getParameter("action") != null) {
            action = request.getParameter("action");
        }

        switch (action) {
            case "LIST":
                //tra ve giao dien lien ket danh sach san pham quan tri
                int pageSize =3;
                int pageIndex =1;
                if (request.getParameter("page")!=null)
                {
                    pageIndex = Integer.parseInt(request.getParameter("page"));
                }
                
                //Tính tổng số trang có thể có
                int sumOfPage = (int) Math.ceil((double)hoaDAO.getAll().size()/pageSize);              
                request.setAttribute("dsHoa", hoaDAO.getByPage(pageIndex, pageSize));
                request.setAttribute("sumOfPage", sumOfPage); // Chuyển dữ liệu cho JSP (VIEW)
                request.setAttribute("pageIndex", pageIndex);
                request.getRequestDispatcher("admin/list_product.jsp").forward(request, response);
                break;
            case "ADD":
                
                if(method.equals("GET")){
                //tra ve giao dien lien ket danh sach san pham quan tri
                request.setAttribute("dsLoai", loaiDAO.getAll());
                request.getRequestDispatcher("admin/add_product.jsp").forward(request, response);
                }else if(method.equals("POST")){
                    //xu ly them moi san pham
                    //b1. Lay thong tin san pham can them
                    String tenhoa = request.getParameter("tenhoa");
                    double gia = Double.parseDouble(request.getParameter("gia"));
                    Part part  = request.getPart("hinh");
                    int maloai= Integer.parseInt(request.getParameter("maloai"));
                    //b2. xu ly upload file (ảnh sản phẩm)
                    String realPath = request.getServletContext().getRealPath("assets/images/products");
                    String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    part.write(realPath + "/" + filename);
                    //b3. Them san pham vao CSDL
                    Hoa objInsert = new Hoa(0, tenhoa, gia, filename, maloai, new java.sql.Date(new java.util.Date().getTime()));
                    if(hoaDAO.Insert(objInsert))
                    {
                        // thong bao them thanh cong
                        request.setAttribute("success", "Thao tác thêm sản phẩm thành công");
                    }else
                    {
                        // thông báo thêm thất bại
                        request.setAttribute("error", "Thông báo thêm sản phẩm thất bại");
                    }
                    //chuyển tiếp người dùng về action=LIST để liệt kê lại danh sách sản phẩm
                    request.getRequestDispatcher("ManageProduct?action=LIST").forward(request, response);
                }
                break;
            case "EDIT":
                //Tra ve giao dien cap nhat san pham
                if (method.equalsIgnoreCase("get")) {
                    int mahoa = Integer.parseInt(request.getParameter("mahoa"));
                    request.setAttribute("hoa", hoaDAO.getById(mahoa));
                    request.setAttribute("dsLoai", loaiDAO.getAll());
                    request.getRequestDispatcher("admin/edit_product.jsp").forward(request, response);
                } else {
                    //xu ly cap nhat san pham
                    //b1 Lay thong tin san pham
                    int mahoa = Integer.parseInt(request.getParameter("mahoa"));
                    String tenhoa = request.getParameter("tenhoa");
                    double gia = Double.parseDouble(request.getParameter("gia"));
                    Part part = request.getPart("hinh");
                    int maloai = Integer.parseInt(request.getParameter("maloai"));
                    String filename = request.getParameter("oldImg");

                    //b2 Xu ly upload file
                    if (part.getSize() > 0) {
                        String realpath = request.getServletContext().getRealPath("/assets/images/products");
                        filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                        part.write(realpath + "/" + filename);
                    }

                    //3. Cap nhat san pham vao CSDL
                    Hoa objUpdate = new Hoa(mahoa, tenhoa, gia, filename, maloai, new java.sql.Date(new java.util.Date().getTime()));
                    if (hoaDAO.Update(objUpdate)) {
                        //thong bao them thanh cong
                        request.setAttribute("success", "Thao tac cap nhat san pham thanh cong");
                    } else {
                        //thong bao them that bai
                        request.setAttribute("error", "Thao tac cap nhat san pham that bai");
                    }
                    request.getRequestDispatcher("ManagerProduct?action=LIST").forward(request, response);
                }
                break;
            case "DELETE":
                //xử lý xoá sản phẩm
                //b1. lấy mã sản phẩm
                int mahoa = Integer.parseInt(request.getParameter("mahoa"));
                //b2. Xoa san pham khoi CSDL
                if(hoaDAO.Delete(mahoa))
                {
                    //thong bao them thanh cong
                    request.setAttribute("success","Thao tác xoá sản phẩm thành công");
                }else
                {
                     // thông báo thêm thất bại
                        request.setAttribute("error", "Thông báo thêm sản phẩm thất bại");
                }
                 //chuyển tiếp người dùng về action=LIST để liệt kê lại danh sách sản phẩm
                    request.getRequestDispatcher("ManageProduct?action=LIST").forward(request, response);
                break;
        }

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ManageProduct</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ManageProduct at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
