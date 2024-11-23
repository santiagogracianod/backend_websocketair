package com.udea.websocketair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/gates")
@CrossOrigin(origins = "http://localhost:3000")
public class GateController {
  @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public GateController(SimpMessagingTemplate messagingTemplate){
      this.messagingTemplate = messagingTemplate;
    }

    //recibir la informacion desde el cliente (react) y enviar  a todos los subscriptores
    @MessageMapping("/updateGate")
    @SendTo("/topic/gates")
    public GateInfo updateGateInfo(GateInfo gateInfo) throws Exception {
      System.out.println("Actualizando información de la puerta: " + gateInfo.getGate());
      messagingTemplate.convertAndSend("/topic/gates", gateInfo);

      //Devolver la información de la puerta a todos los subcriptores
      return new GateInfo(
        HtmlUtils.htmlEscape(gateInfo.getGate()),
        HtmlUtils.htmlEscape(gateInfo.getFlightNumber()),
        HtmlUtils.htmlEscape(gateInfo.getDestination()),
        HtmlUtils.htmlEscape(gateInfo.getDepartureTime()),
        HtmlUtils.htmlEscape(gateInfo.getStatus())

      );

    }



    //Metodo para enviar actualizaciones programaticas o desde un servicio externo
    public void sendUpdate(GateInfo gateInfo){
      //enviar los datos actualizados de una puerte de embarque
      //a todos los subscriptores en /topic/gates
      messagingTemplate.convertAndSend("/topic/gates", gateInfo);
    }

    private Map<String, GateInfo> gateData = new ConcurrentHashMap<>(); 

    //metodos para actualizar la informacion de la puerta de embarque
    @PostMapping("/update")
    public ResponseEntity<String> updateGate(@RequestBody GateInfo gate){
      //actualizar los datos de la puerta
      gateData.put(gate.getGate(), gate);
      
      //enviar la actualizacion a todos los subscriptores del websocket
      messagingTemplate.convertAndSend("/topic/gates", gate);

      return ResponseEntity.ok("Puerta de embarque ha sido actualizada con exito");

    }

    @GetMapping("/{gateNumber}")
    public ResponseEntity<GateInfo> getGateInfo(@PathVariable String gateNumber) {
      GateInfo gate = gateData.get(gateNumber);
      return ResponseEntity.ok(gate);
    }

}
