package org.foi.nwtis.bgolubic.zadaca_2.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import com.google.gson.Gson;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentAerodroma {

  private static Konfiguracija konfig;

  public RestKlijentAerodroma(Konfiguracija konf) {
    konfig = konf;
  }

  public List<Aerodrom> getAerodromi(int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Aerodrom[] json_Aerodromi = rc.getAerodromi(odBroja, broj);
    List<Aerodrom> aerodromi;
    if (json_Aerodromi == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(json_Aerodromi);
    }
    rc.close();
    return aerodromi;
  }

  public List<Aerodrom> getAerodromi() {
    return this.getAerodromi(1, Integer.parseInt(konfig.dajPostavku("stranica.brojRedova")));
  }

  public Aerodrom getAerodrom(String icao) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Aerodrom a = rc.getAerodrom(icao);
    rc.close();
    return a;
  }

  public List<Udaljenost> getAerodromiUdaljenost(String icaoFrom, String icaoTo) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Udaljenost[] json_AerodromiUdaljenosti = rc.getAerodromiUdaljenost(icaoFrom, icaoTo);
    List<Udaljenost> aerodromiUdaljenosti;
    if (json_AerodromiUdaljenosti == null) {
      aerodromiUdaljenosti = new ArrayList<>();
    } else {
      aerodromiUdaljenosti = Arrays.asList(json_AerodromiUdaljenosti);
    }
    rc.close();
    return aerodromiUdaljenosti;
  }

  public List<UdaljenostAerodrom> getUdaljenosti(String icao) {
    return this.getUdaljenosti(icao, 1,
        Integer.parseInt(konfig.dajPostavku("stranica.brojRedova")));
  }

  public List<UdaljenostAerodrom> getUdaljenosti(String icao, int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(konfig);
    UdaljenostAerodrom[] json_Udaljenosti = rc.getUdaljenosti(icao, odBroja, broj);
    List<UdaljenostAerodrom> udaljenosti;
    if (json_Udaljenosti == null) {
      udaljenosti = new ArrayList<>();
    } else {
      udaljenosti = Arrays.asList(json_Udaljenosti);
    }
    rc.close();
    return udaljenosti;
  }

  public UdaljenostAerodromDrzava getNajduljiPut(String icao) {
    RestKKlijent rc = new RestKKlijent(konfig);
    UdaljenostAerodromDrzava najduljiPut = rc.getNajduljiPut(icao);
    rc.close();
    return najduljiPut;
  }

  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;
    private static volatile String BASE_URI;

    public RestKKlijent(Konfiguracija konf) {
      client = ClientBuilder.newClient();
      BASE_URI = konf.dajPostavku("adresa.wa_1");
      webTarget = client.target(BASE_URI).path("aerodromi");
    }

    public Aerodrom[] getAerodromi(int odBroja, int broj) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource = resource.queryParam("odBroja", odBroja);
      resource = resource.queryParam("broj", broj);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Aerodrom[] aerodromi = gson.fromJson(request.get(String.class), Aerodrom[].class);

      return aerodromi;
    }

    public Aerodrom getAerodrom(String icao) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Aerodrom aerodrom = gson.fromJson(request.get(String.class), Aerodrom.class);
      return aerodrom;
    }

    public Udaljenost[] getAerodromiUdaljenost(String icaoFrom, String icaoTo)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}/{1}", icaoFrom, icaoTo));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Udaljenost[] udaljenost = gson.fromJson(request.get(String.class), Udaljenost[].class);
      return udaljenost;
    }

    public UdaljenostAerodrom[] getUdaljenosti(String icao, int odBroja, int broj)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}/udaljenosti", new Object[] {icao}));
      resource = resource.queryParam("odBroja", odBroja);
      resource = resource.queryParam("broj", broj);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      UdaljenostAerodrom[] udaljenosti =
          gson.fromJson(request.get(String.class), UdaljenostAerodrom[].class);
      return udaljenosti;
    }

    public UdaljenostAerodromDrzava getNajduljiPut(String icao) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource
          .path(java.text.MessageFormat.format("{0}/najduljiPutDrzave", new Object[] {icao}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      UdaljenostAerodromDrzava najduljiPut =
          gson.fromJson(request.get(String.class), UdaljenostAerodromDrzava.class);
      return najduljiPut;
    }

    public void close() {
      client.close();
    }
  }

}
