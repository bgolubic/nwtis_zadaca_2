package org.foi.nwtis.bgolubic.zadaca_2.mvc;


import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.bgolubic.zadaca_2.rest.RestKlijentAerodroma;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

/**
 *
 * @author NWTiS
 */
@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {

  @Inject
  ServletContext context;

  @Inject
  private Models model;

  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}


  @GET
  @Path("svi")
  @View("aerodromi.jsp")
  public void getAerodromi() {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
      var aerodromi = rca.getAerodromi();
      model.put("aerodromi", aerodromi);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("odabirAerodroma")
  @View("odabirAerodroma.jsp")
  public void odabirAerodroma() {}

  @GET
  @Path("icao")
  @View("aerodrom.jsp")
  public void getAerodrom(@QueryParam("icao") String icao) {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
      var aerodrom = rca.getAerodrom(icao);
      model.put("aerodrom", aerodrom);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("odabirUdaljenostiOdDo")
  @View("odabirUdaljenostiOdDo.jsp")
  public void odabirUdaljenostiOdDo() {}

  @GET
  @Path("udaljenosti2aerodroma")
  @View("aerodromiUdaljenosti.jsp")
  public void getAerodromiUdaljenost(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
      var aerodromiUdaljenost = rca.getAerodromiUdaljenost(icaoOd, icaoDo);
      model.put("aerodromiUdaljenost", aerodromiUdaljenost);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("odabirSvihUdaljenosti")
  @View("odabirSvihUdaljenosti.jsp")
  public void odabirSvihUdaljenosti() {}

  @GET
  @Path("udaljenosti")
  @View("udaljenosti.jsp")
  public void getUdaljenosti(@QueryParam("icao") String icao) {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
      var udaljenosti = rca.getUdaljenosti(icao);
      model.put("udaljenosti", udaljenosti);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("odabirNajduljiPut")
  @View("odabirNajduljiPut.jsp")
  public void odabirNajduljiPut() {}

  @GET
  @Path("najduljiPut")
  @View("najduljiPut.jsp")
  public void getNajduljiPut(@QueryParam("icao") String icao) {
    try {
      Konfiguracija konfig = (Konfiguracija) context.getAttribute("konfig");
      RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
      var najduljiPut = rca.getNajduljiPut(icao);
      model.put("najduljiPut", najduljiPut);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
