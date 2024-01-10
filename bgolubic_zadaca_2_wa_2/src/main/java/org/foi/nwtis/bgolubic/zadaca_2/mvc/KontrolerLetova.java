package org.foi.nwtis.bgolubic.zadaca_2.mvc;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.bgolubic.zadaca_2.rest.RestKlijentLetova;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

/****
 * 
 * @author NWTiS
 */
@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetova {

  @Inject
  ServletContext context;

  @Inject
  private Models model;

  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}


  @GET
  @Path("odabirPolazakaOdAerodroma")
  @View("odabirPolazakaOdAerodroma.jsp")
  public void odabirPolasciOdAerodroma() {}

  @GET
  @Path("polasciOdAerodroma")
  @View("polasciOdAerodroma.jsp")
  public void getPolasciOdAerodroma(@QueryParam("icao") String icao,
      @QueryParam("dan") String dan) {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentLetova rcl = new RestKlijentLetova(konfig);
      var polasci = rcl.getPolasciOdAerodroma(icao, dan);
      model.put("polasci", polasci);
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
  }

  @GET
  @Path("odabirPolazakaOdDo")
  @View("odabirPolazakaOdDo.jsp")
  public void odabirPolazakaOdDo() {}

  @GET
  @Path("polasciOdDo")
  @View("polasciOdDo.jsp")
  public void getPolasciOdDo(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo, @QueryParam("dan") String dan) {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentLetova rcl = new RestKlijentLetova(konfig);
      var polasci = rcl.getPolasciOdDo(icaoOd, icaoDo, dan);
      model.put("polasci", polasci);
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
  }

  @GET
  @Path("pregledSpremljenih")
  @View("pregledSpremljenih.jsp")
  public void getSpremljeni() {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentLetova rcl = new RestKlijentLetova(konfig);
      var polasci = rcl.getSpremljeni();
      model.put("polasci", polasci);
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
  }

}
