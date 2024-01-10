package org.foi.nwtis.bgolubic.zadaca_2.rest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("letovi")
@RequestScoped
public class RestLetovi {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @Inject
  private ServletContext context;

  @Path("{icao}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajLetoveZaDan(@PathParam("icao") String icao, @QueryParam("dan") String dan,
      @QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {

    if (dan == null) {
      return Response.status(404).build();
    }

    if (odBroja <= 0 || broj <= 0) {
      odBroja = 1;
      broj = 20;
    }

    Konfiguracija konf = (Konfiguracija) context.getAttribute("konfig");
    String korisnik = konf.dajPostavku("OpenSkyNetwork.korisnik");
    String lozinka = konf.dajPostavku("OpenSkyNetwork.lozinka");

    Long odVremena = dajEpochPocetni(dan);
    Long doVremena = dajEpochZavrsni(dan);

    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);

    List<LetAviona> avioniPolasci = null;
    try {
      avioniPolasci = oSKlijent.getDepartures(icao, odVremena, doVremena);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }

    if (avioniPolasci.size() < broj)
      broj = avioniPolasci.size() - 1;

    var gson = new Gson();
    var jsonAerodromi = gson.toJson(avioniPolasci.subList(odBroja - 1, broj));

    avioniPolasci.removeIf(l -> (l.getEstArrivalAirport() == null));

    var odgovor = Response.ok().entity(jsonAerodromi).build();

    return odgovor;
  }

  @Path("{icaoOd}/{icaoDo}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajLetoveIzmeduAerodromaZaDan(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo, @QueryParam("dan") String dan) {

    if (dan == null) {
      return Response.status(404).build();
    }

    Konfiguracija konf = (Konfiguracija) context.getAttribute("konfig");
    String korisnik = konf.dajPostavku("OpenSkyNetwork.korisnik");
    String lozinka = konf.dajPostavku("OpenSkyNetwork.lozinka");

    Long odVremena = dajEpochPocetni(dan);
    Long doVremena = dajEpochZavrsni(dan);

    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);

    List<LetAviona> avioniPolasci = null;
    try {
      avioniPolasci = oSKlijent.getDepartures(icaoOd, odVremena, doVremena);
      avioniPolasci.removeIf(
          l -> (l.getEstArrivalAirport() == null || !l.getEstArrivalAirport().matches(icaoDo)));
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }

    var gson = new Gson();
    var jsonAerodromi = gson.toJson(avioniPolasci);

    var odgovor = Response.ok().entity(jsonAerodromi).build();

    return odgovor;
  }

  @Path("spremljeni")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSpremljeneLetove() {
    var letovi = new ArrayList<LetAviona>();

    String query = "SELECT * FROM LETOVI_POLASCI";

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);

      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        letovi.add(dajPodatkeZaLet(rs));
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
    var jsonLetovi = gson.toJson(letovi);

    var odgovor = Response.ok().entity(jsonLetovi).build();

    return odgovor;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void spremiLet(LetAviona let) {

    String query = String.format(
        "INSERT INTO PUBLIC.PUBLIC.LETOVI_POLASCI (ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, "
            + "ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, ESTDEPARTUREAIRPORTVERTDISTANCE,"
            + " ESTARRIVALAIRPORTHORIZDISTANCE, ESTARRIVALAIRPORTVERTDISTANCE, DEPARTUREAIRPORTCANDIDATESCOUNT,"
            + " ARRIVALAIRPORTCANDIDATESCOUNT, STORED) VALUES('%s', %d, '%s', %d, '%s', '%s', %d, %d, %d, %d, %d, %d, '"
            + Timestamp.from(Instant.now()) + "')",
        let.getIcao24(), let.getFirstSeen(), let.getEstDepartureAirport(), let.getLastSeen(),
        let.getEstArrivalAirport(), let.getCallsign(), let.getEstDepartureAirportHorizDistance(),
        let.getEstDepartureAirportVertDistance(), let.getEstArrivalAirportHorizDistance(),
        let.getEstArrivalAirportVertDistance(), let.getDepartureAirportCandidatesCount(),
        let.getArrivalAirportCandidatesCount());

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);

      pstmt.execute();

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
  }

  @DELETE
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void spremiLet(@PathParam("id") int id) {

    String query = "DELETE FROM LETOVI_POLASCI WHERE ID = ?";

    PreparedStatement pstmt = null;
    try (var con = ds.getConnection()) {
      pstmt = con.prepareStatement(query);
      pstmt.setInt(1, id);

      pstmt.execute();

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
  }

  private long dajEpochPocetni(String dan) {
    long epoch = 0;

    try {
      SimpleDateFormat formaterVremena = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
      formaterVremena.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date datum = formaterVremena.parse(dan + " 00:00:00");
      epoch = datum.toInstant().getEpochSecond();
    } catch (ParseException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }

    return epoch;
  }

  private long dajEpochZavrsni(String dan) {
    long epoch = 0;

    try {
      SimpleDateFormat formaterVremena = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
      formaterVremena.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date datum = formaterVremena.parse(dan + " 23:59:59");
      epoch = datum.toInstant().getEpochSecond();
    } catch (ParseException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }

    return epoch;
  }

  private LetAviona dajPodatkeZaLet(ResultSet rs) throws SQLException {
    var let = new LetAviona();

    let.setArrivalAirportCandidatesCount(rs.getInt("ARRIVALAIRPORTCANDIDATESCOUNT"));
    let.setCallsign(rs.getString("CALLSIGN"));
    let.setDepartureAirportCandidatesCount(rs.getInt("DEPARTUREAIRPORTCANDIDATESCOUNT"));
    let.setEstArrivalAirport(rs.getString("ESTARRIVALAIRPORT"));
    let.setEstArrivalAirportHorizDistance(rs.getInt("ESTARRIVALAIRPORTHORIZDISTANCE"));
    let.setEstArrivalAirportVertDistance(rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE"));
    let.setEstDepartureAirport(rs.getString("ESTDEPARTUREAIRPORT"));
    let.setEstDepartureAirportHorizDistance(rs.getInt("ESTDEPARTUREAIRPORTHORIZDISTANCE"));
    let.setEstDepartureAirportVertDistance(rs.getInt("ESTDEPARTUREAIRPORTVERTDISTANCE"));
    let.setFirstSeen(rs.getInt("FIRSTSEEN"));
    let.setIcao24(rs.getString("ICAO24"));
    let.setLastSeen(rs.getInt("LASTSEEN"));

    return let;
  }
}
