package com.udea.websocketair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/aircrafts")
@CrossOrigin(origins = "http://localhost:3000")
public class AircraftController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Almacenar datos en un mapa para simulación (puede reemplazarse por una BD)
    private final Map<String, AircraftInfo> aircraftData = new ConcurrentHashMap<>();

    // Endpoint para manejar mensajes desde el frontend (WebSocket)
    @MessageMapping("/updateAircraft")
    @SendTo("/topic/aircrafts")
    public AircraftInfo updateAircraftInfo(AircraftInfo aircraftInfo) {
        System.out.println("Actualizando información del avión: " + aircraftInfo.getFlightNumber());
        
        // Guardar o actualizar datos en el mapa
        aircraftData.put(aircraftInfo.getFlightNumber(), aircraftInfo);

        // Enviar datos actualizados a todos los suscriptores
        messagingTemplate.convertAndSend("/topic/aircrafts", aircraftInfo);
        return aircraftInfo;
    }

    // Endpoint HTTP para agregar o actualizar un avión (llamado desde el formulario)
    @PostMapping("/update")
    public ResponseEntity<String> updateAircraft(@RequestBody AircraftInfo aircraftInfo) {
        aircraftData.put(aircraftInfo.getFlightNumber(), aircraftInfo);

        // Notificar a través del canal WebSocket
        messagingTemplate.convertAndSend("/topic/aircrafts", aircraftInfo);
        return ResponseEntity.ok("Información del avión actualizada exitosamente");
    }

    // Obtener todos los aviones (opcional, para pruebas)
    @GetMapping
    public ResponseEntity<Map<String, AircraftInfo>> getAllAircrafts() {
        return ResponseEntity.ok(aircraftData);
    }

    // Obtener datos de un avión específico
    @GetMapping("/{flightNumber}")
    public ResponseEntity<AircraftInfo> getAircraft(@PathVariable String flightNumber) {
        AircraftInfo aircraft = aircraftData.get(flightNumber);
        if (aircraft != null) {
            return ResponseEntity.ok(aircraft);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
