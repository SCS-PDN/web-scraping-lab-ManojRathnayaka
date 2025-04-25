<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Scraped Results</title>
</head>
<body>
    <h1>Scraped Data Results</h1>
    
    <table border="1">
        <thead>
            <tr>
                <th>Title</th>
                <th>Link</th>
                <th>Image</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="data" items="${scrapedDataList}">
                <tr>
                    <td>${data.Title}</td>
                    <td>${data.Link}</td>
                    <td>${data.Image}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <a href="ScrapeServlet?action=download">Download Results as CSV</a>
</body>
</html>
