<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title>Campus - ${course.subject.name}</title>
    <c:import url="../config/generalHead.jsp"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script>
        function toggleAll(source) {
            const checkboxes = document.getElementsByName(source.name);
            for(let i=0, n=checkboxes.length;i<n;i++) {
                checkboxes[i].checked = source.checked;
            }
        }

        function unToggle(id){
            const checkbox = document.getElementById(id);
            checkbox.checked = false;
        }

        function toggleFilters(){
            const filters = document.getElementById("filter-container");
            const toggler = document.getElementById("filter-toggle");
            if (filters.style.display === "none") {
                filters.style.display = "flex";
                toggler.style.transform="rotate(-90deg)"
            } else {
                filters.style.display = "none";
                toggler.style.transform="rotate(90deg)"
            }
        }

        function deleteById(fileId){
            $.ajax({
                url: '/deleteFile/' + fileId,
                type: 'DELETE',
                success: function (result) {
                    $("#file-"+ fileId).remove();
                }
            });
        }
    </script>
</head>
<body>
<div class="page-organizer">
    <jsp:include page="../components/navbar.jsp">
        <jsp:param name="successMessage" value="${successMessage}"/>
    </jsp:include>
    <h2 class="course-section-name">${course.subject.name}</h2>
    <div class="page-container" style="padding-top: 0">
        <div class="course-page-wrapper">
            <jsp:include page="../components/courseSectionsCol.jsp">
                <jsp:param name="courseName" value="${course.subject.name}"/>
                <jsp:param name="courseId" value="${course.courseId}"/>
            </jsp:include>
            <c:url value="/course/${courseId}/files" var="postUrl"/>
            <div class="course-data-container">
                <h3 class="section-heading" style="margin: 0 0 20px 20px"> Material </h3>
                <form:form modelAttribute="fileForm" method="post" enctype="multipart/form-data"
                           class="form-wrapper reduced" acceptCharset="utf-8">
                    <h1 class="announcement-title" style="color:#176961; align-self:center">Subir nuevo archivo</h1>
                    <form:label path="file" for="file" class="form-label">Archivo</form:label>
                    <form:input path="file" type="file" class="form-input" style="font-size: 26px"/>
                    <form:errors path="file" element="p" cssStyle="color:red;margin-left: 10px"/>
                    <form:label path="categoryId" for="categoryId" class="form-label">Categoria</form:label>
                    <form:select path="categoryId" class="form-input" style="font-size: 26px">
                        <c:forEach var="category" items="${categories}">
                            <form:option value="${category.categoryId}"><c:out value="${category.categoryName}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <form:errors path="categoryId" element="p" cssStyle="color:red;margin-left: 10px"/>
                    <button class="form-button">Publicar</button>
                </form:form>
                <div class="separator reduced">.</div>
                <div class="big-wrapper">
                    <form action="" class="file-query-container">
                        <div style="display: flex; align-items: center; margin-bottom: 10px">
                            <input class="form-input" name="query"
                                   style="width: 70%; height: 30px; border-top-right-radius: 0;
                                   border-bottom-right-radius: 0; border:none" value="${query}">
                            <button class="form-button"
                                    style="height: 30px; margin:0; width: 120px;
                                    border-top-left-radius: 0; border-bottom-left-radius: 0">
                                Buscar
                            </button>
                            <img src="<c:url value="${page.Context.request.contextPath}/resources/images/page-arrow.png"/>"
                                 class="pagination-arrow"  style="transform: rotate(90deg); margin-left: 10px"
                                 onclick="toggleFilters()" alt="toggle filters" id="filter-toggle">
                        </div>
                        <div class="file-filter-container" id="filter-container" style="display: none">
                            <div style="display: flex; flex-direction: column;">
                                <label for="order-class" class="file-select-label">Buscar por</label>
                                <select name="order-class" id="order-class" class="file-select">
                                    <option value="DATE" <c:if test="${orderClass == 'DATE'}">selected</c:if>>
                                        Fecha de subida
                                    </option>
                                    <option value="NAME" <c:if test="${orderClass == 'NAME'}">selected</c:if>>
                                        Nombre
                                    </option>
                                </select>
                                <label for="order-by" class="file-select-label">De forma</label>
                                <select name="order-by" id="order-by" class="file-select">
                                    <option value="ASC" <c:if test="${orderBy == 'ASC'}">selected</c:if>>
                                        Ascendente
                                    </option>
                                    <option value="DESC" <c:if test="${orderBy == 'DESC'}">selected</c:if>>
                                        Descendente
                                    </option>
                                </select>
                            </div>


                            <div style="display: flex; flex-direction: column;">
                                <label class="file-select-label">Tipo de archivo</label>
                                <span>
                                    <input class="file-checkbox" type="checkbox" id="extension-all" name="extension-type"
                                           value="${0}" onclick="toggleAll(this)"
                                           <c:if test="${extensionType.equals(extensions)}">checked</c:if>>
                                    <label class="file-checkbox-label" for="extension-all">todos</label>
                                </span>
                                <c:forEach var="extension" items="${extensions}">
                                    <span>
                                        <input class="file-checkbox" type="checkbox" id="extension-${extension.fileExtensionId}" name="extension-type"
                                               value="${extension.fileExtensionId}" onclick="unToggle('extension-all')"
                                               <c:if test="${extensionType.contains(extension.fileExtensionId)}">checked</c:if>>
                                        <label class="file-checkbox-label" for="extension-${extension.fileExtensionId}">
                                            <c:out value="${extension.fileExtension}"/>
                                        </label>
                                    </span>
                                </c:forEach>
                            </div>

                            <div style="display: flex; flex-direction: column; ">
                                <label class="file-select-label">Categoria</label>
                                <span>
                                    <input class="file-checkbox" type="checkbox" id="category-all" name="category-type"
                                           value="${0}" onclick="toggleAll(this)"
                                           <c:if test="${categoryType.equals(categories)}">checked</c:if>>
                                    <label class="file-checkbox-label" for="category-all">todos</label>
                                </span>
                                <c:forEach var="category" items="${categories}">
                                    <span>
                                        <input class="file-checkbox" type="checkbox" id="category-${category.categoryId}" name="category-type"
                                               value="${category.categoryId}" onclick="unToggle('category-all')"
                                               <c:if test="${categoryType.contains(category.categoryId)}">checked</c:if>>
                                        <label class="file-checkbox-label" for="category-${category.categoryId}"><c:out value="${category.categoryName}"/></label>
                                    </span>
                                </c:forEach>
                            </div>
                        </div>
                    </form>
                    <div class="file-grid">
                        <c:forEach var="file" items="${files}">
                            <div class="file-unit" id="file-${file.fileId}">
                                <a href="<c:url value="/download/${file.fileId}"/>" class="styleless-anchor"
                                   style="display: flex;margin-left: 10px; align-items: center">
                                    <img src="<c:url value="${page.Context.request.contextPath}/resources/images/extensions/${file.extension.fileExtension}.png"/>"
                                         class="file-img" alt="${file.name}"/>
                                <p class="file-name"><c:out value=" ${file.name}"/></p>
                                </a>
                                <img src="${page.Context.request.contextPath}/resources/images/trash.png"
                                     alt="delete" class="medium-icon" onclick="deleteById(${file.fileId})">
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <jsp:include page="../components/footer.jsp"/>
</div>
</body>
</html>
