package khozhaev.postalitemstracker.service;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import khozhaev.postalitemstracker.dto.MailItemDto;
import khozhaev.postalitemstracker.exception.EntityNotFoundException;
import khozhaev.postalitemstracker.model.*;
import khozhaev.postalitemstracker.repository.MailItemRepository;
import khozhaev.postalitemstracker.repository.PostOfficeRepository;
import khozhaev.postalitemstracker.repository.MailMovementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailItemService {

    private final MailItemRepository mailItemRepository;
    private final PostOfficeRepository postOfficeRepository;
    private final MailMovementRepository mailMovementRepository;
    private final ModelMapper modelMapper;

    public MailItem registerMailItem(MailItemDto mailItemDto) {
        MailItem mailItem = modelMapper.map(mailItemDto, MailItem.class);
        mailItem.setStatus(DeliveryStatus.CREATED);
        mailItemRepository.save(mailItem);
        return mailItem;
    }

    public MailItem arriveToPostOffice(String trackingNumber, Long postOfficeId) {
        MailItem mailItem = getMailItemByTrackingNumber(trackingNumber);
        PostOffice postOffice = postOfficeRepository.findById(postOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find office with id: " + postOfficeId));

        MailMovement mailMovement = new MailMovement(mailItem, postOffice, MovementType.ARRIVAL, LocalDate.now());
        mailMovementRepository.save(mailMovement);

        mailItem.setStatus(DeliveryStatus.IN_DELIVERY);
        mailItem.addMailMovement(mailMovement);
        mailItemRepository.save(mailItem);

        return mailItem;
    }

    public MailItem departFromPostOffice(String trackingNumber) {
        MailMovement lastEvent = mailMovementRepository.findLastMailMovementByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There are no any movement events related to mail item with id: " + trackingNumber));

        MailMovement mailMovement = new MailMovement(
                lastEvent.getMailItem(),
                lastEvent.getPostOffice(),
                MovementType.DEPARTURE,
                LocalDate.now()
        );
        mailMovementRepository.save(mailMovement);

        MailItem mailItem = lastEvent.getMailItem();
        mailItem.setStatus(DeliveryStatus.IN_DELIVERY);
        mailItem.addMailMovement(mailMovement);
        mailItemRepository.save(mailItem);

        return mailItem;
    }

    public MailItem receiveByRecipient(String trackingNumber) {
        MailMovement lastEvent = mailMovementRepository.findLastMailMovementByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There are no any movement events related to mail item with id: " + trackingNumber));

        MailMovement mailMovement = new MailMovement(
                lastEvent.getMailItem(),
                lastEvent.getPostOffice(),
                MovementType.RECEIPT,
                LocalDate.now()
        );
        mailMovementRepository.save(mailMovement);

        MailItem mailItem = lastEvent.getMailItem();
        mailItem.setStatus(DeliveryStatus.DELIVERED);
        mailItem.addMailMovement(mailMovement);
        mailItemRepository.save(mailItem);

        return mailItem;
    }

    public DeliveryStatus getStatus(String trackingNumber) {
        MailItem mailItem = getMailItemByTrackingNumber(trackingNumber);
        return mailItem.getStatus();
    }

    public List<MailMovement> getMovementHistory(String trackingNumber) {
        MailItem mailItem = getMailItemByTrackingNumber(trackingNumber);
        return mailItem.getMailMovements();
    }

    private MailItem getMailItemByTrackingNumber(String trackingNumber) {
        return mailItemRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find mail item with id: " + trackingNumber));
    }
}
