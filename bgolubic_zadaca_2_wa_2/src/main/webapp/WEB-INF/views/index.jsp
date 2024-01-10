<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Početna</title>
</head>
<body>
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/svi">Pregled aerodroma</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/odabirAerodroma">Pregled jednog aerodroma</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/odabirUdaljenostiOdDo">Pregled udaljenosti 2 aerodroma</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/odabirSvihUdaljenosti">Pregled svih udaljenosti</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/odabirNajduljiPut">Pregled najduljeg puta države</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/letovi/odabirPolazakaOdAerodroma">Pregled polazaka s aerodroma</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/letovi/odabirPolazakaOdDo">Pregled polazaka s aerodroma do aerodroma</a><br/>
	<a href="${pageContext.servletContext.contextPath}/mvc/letovi/pregledSpremljenih">Pregled spremljenih</a><br/>
</body>
</html>