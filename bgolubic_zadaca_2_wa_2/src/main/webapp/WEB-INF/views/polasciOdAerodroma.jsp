<%@page import="org.foi.nwtis.Konfiguracija"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Polasci od aerodroma</title>
</head>
<body>
<% ServletContext sc = request.getServletContext();
    Konfiguracija konf = (Konfiguracija) sc.getAttribute("konfig"); %>
	<h1>Zaglavlje</h1>
	Autor: <%= konf.dajPostavku("autor.ime") %> <%= konf.dajPostavku("autor.prezime") %><br/>
	Predmet: <%= konf.dajPostavku("autor.predmet") %><br/>
	Godina: <%= konf.dajPostavku("aplikacija.godina") %><br/>
	Verzija aplikacije: <%= konf.dajPostavku("aplikacija.verzija") %><br/>
	<button onclick="location.href='${pageContext.servletContext.contextPath}'" type="button">Početna stranica</button><br/><br/>
	<h1>Svi polasci</h1>
	<table border=1>
	<tr><th>ICAO24</th><th>FIRSTSEEN</th><th>ESTDEPARTUREAIRPORT</th><th>LASTSEEN</th><th>ESTARRIVALAIRPORT</th><th>CALLSIGN</th><th>ESTDEPARTUREAIRPORTHORIZDISTANCE</th><th>ESTDEPARTUREAIRPORTVERTDISTANCE</th><th>ESTARRIVALAIRPORTHORIZDISTANCE</th><th>ESTARRIVALAIRPORTVERTDISTANCE</th><th>DEPARTUREAIRPORTCANDIDATESCOUNT</th><th>ARRIVALAIRPORTCANDIDATESCOUNT</th></tr>
	<c:forEach var="polazak" items="${polasci}">
   		<tr><td><c:out value="${polazak.getIcao24()}"/></td>
   		<td><c:out value="${polazak.getFirstSeen()}"/></td>
   		<td><c:out value="${polazak.getEstDepartureAirport()}"/></td>
   		<td><c:out value="${polazak.getLastSeen()}"/></td>
   		<td><c:out value="${polazak.getEstArrivalAirport()}"/></td>
   		<td><c:out value="${polazak.getCallsign()}"/></td>
   		<td><c:out value="${polazak.getEstDepartureAirportHorizDistance()}"/></td>
   		<td><c:out value="${polazak.getEstDepartureAirportVertDistance()}"/></td>
   		<td><c:out value="${polazak.getEstArrivalAirportHorizDistance()}"/></td>
   		<td><c:out value="${polazak.getEstArrivalAirportVertDistance()}"/></td>
   		<td><c:out value="${polazak.getDepartureAirportCandidatesCount()}"/></td>
   		<td><c:out value="${polazak.getArrivalAirportCandidatesCount()}"/></td></tr>
 	</c:forEach>
</table>
</body>
</html>