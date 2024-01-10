package org.foi.nwtis.bgolubic.zadaca_2.rest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSveAerodrome(@QueryParam("odBroja") int odBroja,
      @QueryParam("broj") int broj) {

    if (odBroja <= 0 || broj <= 0) {
      odBroja = 1;
      broj = 20;
    }


    List<Aerodrom> aerodromi = new ArrayList<>();
    String query = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS LIMIT " + broj
        + " OFFSET " + (odBroja - 1);

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String icao = rs.getString("ICAO");
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String[] koordinate = (rs.getString("COORDINATES").split(","));
        Lokacija lokacija = new Lokacija(koordinate[0].trim(), koordinate[1].trim());
        Aerodrom a = new Aerodrom(icao, naziv, drzava, lokacija);
        aerodromi.add(a);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed())
          pstmt.close();
      } catch (SQLException e) {
        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      }
    }
    var gson = new Gson();
    var jsonAerodromi = gson.toJson(aerodromi);

    var odgovor = Response.ok().entity(jsonAerodromi).build();

    return odgovor;
  }

  @Path("{icao}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajAerodrom(@PathParam("icao") String icao) {
    Aerodrom aerodrom = null;

    String query = "SELECT NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE ICAO = ?";

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String[] koordinate = (rs.getString("COORDINATES").split(","));
        Lokacija lokacija = new Lokacija(koordinate[0].trim(), koordinate[1].trim());
        aerodrom = new Aerodrom(icao, naziv, drzava, lokacija);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed())
          pstmt.close();
      } catch (SQLException e) {
        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      }
    }

    if (aerodrom == null) {
      return Response.status(404).build();
    }

    var gson = new Gson();
    var jsonAerodrom = gson.toJson(aerodrom);

    var odgovor = Response.ok().entity(jsonAerodrom).build();

    return odgovor;
  }

  @Path("{icaoOd}/{icaoDo}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiAerodroma(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {
    var udaljenosti = new ArrayList<Udaljenost>();

    String query =
        "SELECT COUNTRY, DIST_CTRY FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? AND ICAO_TO = ?";

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icaoOd);
      pstmt.setString(2, icaoDo);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("DIST_CTRY");
        var u = new Udaljenost(drzava, udaljenost);
        udaljenosti.add(u);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed())
          pstmt.close();
      } catch (SQLException e) {
        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      }
    }

    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(udaljenosti);

    var odgovor = Response.ok().entity(jsonUdaljenosti).build();

    return odgovor;
  }

  @Path("{icao}/udaljenosti")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiAerodromaPremaOstalima(@PathParam("icao") String icao,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {
    var udaljenosti = new ArrayList<UdaljenostAerodrom>();

    if (odBroja <= 0 || broj <= 0) {
      odBroja = 1;
      broj = 20;
    }

    String query =
        "SELECT ICAO_TO, DIST_CTRY FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? LIMIT " + broj
            + " OFFSET " + (odBroja - 1);

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String icaoTo = rs.getString("ICAO_TO");
        float udaljenost = rs.getFloat("DIST_CTRY");
        var u = new UdaljenostAerodrom(icaoTo, udaljenost);
        udaljenosti.add(u);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed())
          pstmt.close();
      } catch (SQLException e) {
        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      }
    }

    var gson = new Gson();
    var jsonUdaljenosti = gson.toJson(udaljenosti);

    var odgovor = Response.ok().entity(jsonUdaljenosti).build();

    return odgovor;
  }

  @Path("{icao}/najduljiPutDrzave")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajNajduljiPutDrzave(@PathParam("icao") String icao) {

    UdaljenostAerodromDrzava maxUdaljenost = null;

    String query = "SELECT ICAO_TO, COUNTRY, MAX(DIST_CTRY) AS "
        + "MAX_UDALJENOST FROM AIRPORTS_DISTANCE_MATRIX "
        + "WHERE ICAO_FROM = ? GROUP BY ICAO_TO, COUNTRY ORDER BY 3 DESC LIMIT 1";

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, icao);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        String icaoTo = rs.getString("ICAO_TO");
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("MAX_UDALJENOST");
        maxUdaljenost = new UdaljenostAerodromDrzava(icaoTo, drzava, udaljenost);
      }
      rs.close();
      pstmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    } finally {
      try {
        if (pstmt != null && !pstmt.isClosed())
          pstmt.close();
      } catch (SQLException e) {
        Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      }
    }

    if (maxUdaljenost == null) {
      return Response.status(404).build();
    }

    var gson = new Gson();
    var jsonUdaljenost = gson.toJson(maxUdaljenost);

    var odgovor = Response.ok().entity(jsonUdaljenost).build();

    return odgovor;
  }
}
