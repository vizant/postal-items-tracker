package khozhaev.postalitemstracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import khozhaev.postalitemstracker.dto.MailItemDto;
import khozhaev.postalitemstracker.model.DeliveryStatus;
import khozhaev.postalitemstracker.model.MailItem;
import khozhaev.postalitemstracker.model.MailMovement;
import khozhaev.postalitemstracker.service.MailItemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailItemService mailItemService;

    @PostMapping("/register")
    public ResponseEntity<MailItem> registerMailItem(@RequestBody MailItemDto mailItemDto) {
        MailItem mailItem = mailItemService.registerMailItem(mailItemDto);
        return new ResponseEntity<>(mailItem, HttpStatus.CREATED);
    }

    @PostMapping("/{trackingNumber}/arrive/{postOfficeId}")
    public ResponseEntity<MailItem> arriveToPostOffice(@PathVariable String trackingNumber, @PathVariable Long postOfficeId) {
        MailItem mailItem = mailItemService.arriveToPostOffice(trackingNumber, postOfficeId);
        return new ResponseEntity<>(mailItem, HttpStatus.OK);
    }

    @PostMapping("/{trackingNumber}/depart")
    public ResponseEntity<MailItem> departFromPostOffice(@PathVariable String trackingNumber) {
        MailItem mailItem = mailItemService.departFromPostOffice(trackingNumber);
        return new ResponseEntity<>(mailItem, HttpStatus.OK);
    }

    @PostMapping("/{trackingNumber}/receive")
    public ResponseEntity<MailItem> receiveByRecipient(@PathVariable String trackingNumber) {
        MailItem mailItem = mailItemService.receiveByRecipient(trackingNumber);
        return new ResponseEntity<>(mailItem, HttpStatus.OK);
    }

    @GetMapping("/{trackingNumber}/status")
    public ResponseEntity<DeliveryStatus> getStatus(@PathVariable String trackingNumber) {
        DeliveryStatus status = mailItemService.getStatus(trackingNumber);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/{trackingNumber}/history")
    public ResponseEntity<List<MailMovement>> getMovementHistory(@PathVariable String trackingNumber) {
        List<MailMovement> shipmentHistory = mailItemService.getMovementHistory(trackingNumber);
        return new ResponseEntity<>(shipmentHistory, HttpStatus.OK);
    }
}
