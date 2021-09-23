<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<body>
<nav class="navbar-container">
    <h1 class="nav-title"><a class="styleless-anchor" href="<c:url value ="/"/>">CAMPUS</a></h1>
    <c:if test="${!param.isAdmin}">
        <ul class="nav-sections-container">
            <li class="${param.navItem == 1? "nav-sections-item nav-sections-item-active" : "nav-sections-item" }">
                <a href="<c:url value ="/portal"/>" class="styleless-anchor">Mis Cursos</a>
            </li>
            <li class="${param.navItem == 2? "nav-sections-item nav-sections-item-active" : "nav-sections-item" }">
                <a href="<c:url value ="/announcements"/>" class="styleless-anchor">Mis Anuncios</a>
            </li>
            <li class="${param.navItem == 3? "nav-sections-item nav-sections-item-active" : "nav-sections-item" }">
                <a href="<c:url value ="/files"/>" class="styleless-anchor">Mi Material</a>
            </li>
            <li class="${param.navItem == 4? "nav-sections-item nav-sections-item-active" : "nav-sections-item" }">
                <a href="<c:url value ="/timetable"/>" class="styleless-anchor">Mis Horarios</a>
            </li>
        </ul>
    </c:if>
    <c:if test="${currentUser != null}">
        <div class="user-nav-wrapper">
            <a href="<c:url value="/user"/>" class="styleless-anchor">
                <h4>${currentUser.name}</h4>
            </a>
            <a class="styleless-anchor" href="<c:url value ="/logout"/>">
                <button class="logout-btn">Salir</button>
            </a>
        </div>
    </c:if>
    <c:if test="${currentUser == null}">
        <div style="width: 120px"></div>
    </c:if>
</nav>
<c:if test="${param.successMessage != null && !param.successMessage.equals('')}">
    <div class="success-box">
        <c:out value="${param.successMessage}"/>
    </div>
</c:if>
</body>
</html>
