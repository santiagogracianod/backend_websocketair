package com.udea.websocketair;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AircraftInfo {
  private String flightNumber;
  private String location; // Coordenadas (lat, lon)
  private double altitude; // Altitud en pies
  private double speed;    // Velocidad en km/h
  private String status;   // Estado (en vuelo, aterrizando, etc.)
}
