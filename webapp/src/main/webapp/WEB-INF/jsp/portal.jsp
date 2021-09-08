<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Campus</title>
        <meta charset="UTF-8"/>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
        <link href="<c:url value = "${page.Context.request.contextPath}/resources/css/style.css" />" rel="stylesheet" >
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;700&family=Righteous&display=swap" rel="stylesheet">
    </head>
    <body>
    <div class="page-organizer">
        <jsp:include page="navbar.jsp">
            <jsp:param name="navItem" value="${1}"/>
        </jsp:include>
        <div class="page-container">
            <h2 class="section-heading">Mis Cursos</h2>
            <div class="courses-container">
                <c:forEach var="courseItem" items="${courseList}">
                    <div class="course">
                        <p class="course-name">
                            <a href="<c:url value="course/${courseItem.subject.subjectId}"/>" class="styleless-anchor">
                                    <c:out value="${courseItem.subject.name}"/>
                            </a>
                        </p>
                        <p class="course-extra-info"><c:out value="${courseItem.year}/${courseItem.quarter}Q"/></p>
                    </div>
                </c:forEach>

            </div>
        </div>
        <jsp:include page="footer.jsp"/>
    </div>
    </body>
</html>