
<%@page import="model.Hoa"%>
<%@page import="java.util.ArrayList"%>
<%-- 
    Document   : list_product
    Created on : Oct 22, 2024, 2:11:45 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:include page="../shared/header.jsp" />
             
<jsp:include page="../shared/nav.jsp" />


<div class="container">
    <h2> Danh sách sản phẩm</h2>
    <div class="mb-2 text-end">
        <a href="ManageProduct?action=ADD" class="btn btn-success"> <i class="bi bi-plus-circle"></i> Thêm mới</a>
    </div>
    
    <table class="table table-bordered table-striped">
        <tr>
            <th>Tên hoa</th>
            <th>Giá</th>
            <th>Hình ảnh</th>
            <th>Loại</th>
            <th>Action</th>
        </tr>  
        <%
            ArrayList<Hoa> dsHoa = (ArrayList<Hoa>) request.getAttribute("dsHoa");
            for(Hoa x : dsHoa)
            {
        %>
        <tr>
            <td><%=x.getTenhoa() %></td>
            <td><%=x.getGia() %></td>
            <td> <img src="assets/images/products/<%=x.getHinh() %>" style="width: 100px">  </td>
            <td><%=x.getMaloai() %></td>
            <td>
                <a href="ManageProduct?action=EDIT&mahoa=<%=x.getMahoa() %>" class="btn btn-secondary"> <i class="bi bi-pencil-square"></i> Sửa</a>
                 <a href="ManageProduct?action=DELTE&mahoa=<%=x.getMahoa() %>" class="btn btn-danger"
                    onClick="return confirm('Bạn có đống ý xoá sản phẩm ?')"
                    > <i class="bi bi-trash"></i> Xoá</a>
            </td>
        </tr>  
        <%
            }
        %>        
    </table>
    <<ul class="pagination justify-content-center">
        <%
            //Lấy tổng số trang từ servlet
            int sumOfPage = (int) request.getAttribute("sumOfPage");
            int pageIndex = (int) request.getAttribute("pageIndex");
            for(int i=1; i<=sumOfPage; i++)
            {
        %>
            <li class="page-item <%=pageIndex==i?"active":"" %>"> <a class="page-link" href="ManageProduct?page=<%=i%>"><%=i%></a></li>
        <%
            }
        %>
        
    </ul>

</div>

<jsp:include page="../shared/footer.jsp" />
