package com.udea.websocketair;

import lombok.*;

@Data
@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor


public class GateInfo {

  private String gate;
  private String flightNumber;
  private String destination;
  private String departureTime;
  private String status;

}
