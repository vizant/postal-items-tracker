package khozhaev.postalitemstracker.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import khozhaev.postalitemstracker.dto.MailItemDto;
import khozhaev.postalitemstracker.exception.EntityNotFoundException;
import khozhaev.postalitemstracker.model.*;
import khozhaev.postalitemstracker.repository.MailItemRepository;
import khozhaev.postalitemstracker.repository.PostOfficeRepository;
import khozhaev.postalitemstracker.repository.MailMovementRepository;

@ExtendWith(MockitoExtension.class)
class MailItemServiceTest {

    @InjectMocks
    private MailItemService mailItemService;

    @Mock
    private MailItemRepository mailItemRepository;

    @Mock
    private PostOfficeRepository postOfficeRepository;

    @Mock
    private MailMovementRepository mailMovementRepository;

    @Spy
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<MailMovement> mailMovementArgumentCaptor;

    @Test
    public void registerMailItem_shouldMapDtoObjectAndSetCreatedStatus() {
        MailItemDto mailItemDto = new MailItemDto();
        mailItemDto.setTrackingNumber("123456789");
        mailItemDto.setSender("sender");
        mailItemDto.setRecipient("recipient");
        mailItemDto.setMailType(MailType.LETTER);

        MailItem mailItem = mailItemService.registerMailItem(mailItemDto);

        assertEquals(mailItem.getTrackingNumber(), mailItemDto.getTrackingNumber());
        assertEquals(mailItem.getRecipient(), mailItemDto.getRecipient());
        assertEquals(mailItem.getSender(), mailItemDto.getSender());
        assertEquals(mailItem.getMailType(), mailItemDto.getMailType());
        assertEquals(mailItem.getStatus(), DeliveryStatus.CREATED);
    }

    @Test
    public void arriveToPostOffice_shouldThrowException_whenMailItemWithTrackingNumberNotFound() {
        String trackingNumber = generateString();
        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber)).thenReturn(Optional.empty());
        Long postOfficeId = generateId();

        assertThrows(
                EntityNotFoundException.class,
                () -> mailItemService.arriveToPostOffice(trackingNumber, postOfficeId),
                "Can't find mail item with id: " + trackingNumber
        );
    }

    @Test
    public void arriveToPostOffice_shouldThrowException_whenPostOfficeWithIdNotFound() {
        String trackingNumber = generateString();
        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber))
                .thenReturn(Optional.of(Mockito.mock(MailItem.class)));
        Long postOfficeId = generateId();
        Mockito.when(postOfficeRepository.findById(postOfficeId))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> mailItemService.arriveToPostOffice(trackingNumber, postOfficeId),
                "Can't find office with id: " + postOfficeId
        );
    }

    @Test
    public void arriveToPostOffice_shouldCreateShipmentEventAndSetInDeliveryStatusToMailItem() {
        String trackingNumber = generateString();
        MailItem mailItem = generateMailItem(
                trackingNumber, DeliveryStatus.CREATED, "sender", "recipient"
        );
        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber))
                .thenReturn(Optional.of(mailItem));

        Long postOfficeId = generateId();
        PostOffice postOffice = generatePostOffice(postOfficeId,"name", "address", "123456");
        Mockito.when(postOfficeRepository.findById(postOfficeId))
                .thenReturn(Optional.of(postOffice));

        mailItemService.arriveToPostOffice(trackingNumber, postOfficeId);

        assertEquals(mailItem.getStatus(), DeliveryStatus.IN_DELIVERY);

        Mockito.verify(mailMovementRepository, Mockito.times(1))
                .save(mailMovementArgumentCaptor.capture());
        MailMovement mailMovement = mailMovementArgumentCaptor.getValue();
        assertEquals(mailMovement.getMailItem(), mailItem);
        assertEquals(mailMovement.getPostOffice(), postOffice);
        assertEquals(mailMovement.getMovementType(), MovementType.ARRIVAL);
    }

    @Test
    public void departFromPostOffice_shouldThrowException_whenMailMovementNotFound() {
        String trackingNumber = generateString();

        Mockito.when(mailMovementRepository.findLastMailMovementByTrackingNumber(trackingNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> mailItemService.departFromPostOffice(trackingNumber),
                "There are no any movement events related to mail item with id: " + trackingNumber
        );
    }

    @Test
    public void departFromPostOffice_shouldCreateMovementEventAndSetInDeliveryStatusToMailItem() {
        String trackingNumber = generateString();
        MailItem mailItem = generateMailItem(
                trackingNumber,
                DeliveryStatus.CREATED,
                "sender",
                "recipient"
        );

        Long postOfficeId = generateId();
        PostOffice postOffice = generatePostOffice(
                postOfficeId,
                "Post office №1",
                "address",
                "index"
        );

        MailMovement mailMovement = generateMailMovement(
                MovementType.ARRIVAL,
                postOffice,
                mailItem
        );
        Mockito.when(mailMovementRepository.findLastMailMovementByTrackingNumber(trackingNumber))
                .thenReturn(Optional.of(mailMovement));

        mailItemService.departFromPostOffice(trackingNumber);

        Mockito.verify(mailMovementRepository, Mockito.times(1))
                .save(mailMovementArgumentCaptor.capture());
        MailMovement capturedEvent = mailMovementArgumentCaptor.getValue();
        assertEquals(capturedEvent.getMailItem(), mailItem);
        assertEquals(capturedEvent.getPostOffice(), postOffice);
        assertEquals(capturedEvent.getMovementType(), MovementType.DEPARTURE);

        assertEquals(mailItem.getStatus(), DeliveryStatus.IN_DELIVERY);
    }

    @Test
    public void receiveByRecipient_shouldThrowException_whenMailItemNotFound() {
        String trackingNumber = generateString();

        Mockito.when(mailMovementRepository.findLastMailMovementByTrackingNumber(trackingNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> mailItemService.receiveByRecipient(trackingNumber),
                "There are no any movement events related to mail item with id: " + trackingNumber
        );
    }

    @Test
    public void receiveByRecipient_shouldCreateMovementEventAndSetDeliveredStatusToMailItem() {
        String trackingNumber = generateString();
        MailItem mailItem = generateMailItem(
                trackingNumber,
                DeliveryStatus.IN_DELIVERY,
                "sender",
                "recipient"
        );

        Long postOfficeId = generateId();
        PostOffice postOffice = generatePostOffice(
                postOfficeId,
                "Post office №1",
                "address",
                "index"
        );

        MailMovement mailMovement = generateMailMovement(
                MovementType.ARRIVAL,
                postOffice,
                mailItem
        );
        Mockito.when(mailMovementRepository.findLastMailMovementByTrackingNumber(trackingNumber))
                .thenReturn(Optional.of(mailMovement));

        mailItemService.receiveByRecipient(trackingNumber);

        Mockito.verify(mailMovementRepository, Mockito.times(1))
                .save(mailMovementArgumentCaptor.capture());
        MailMovement capturedEvent = mailMovementArgumentCaptor.getValue();
        assertEquals(capturedEvent.getMailItem(), mailItem);
        assertEquals(capturedEvent.getPostOffice(), postOffice);
        assertEquals(capturedEvent.getMovementType(), MovementType.RECEIPT);

        assertEquals(mailItem.getStatus(), DeliveryStatus.DELIVERED);
    }

    @Test
    public void getStatus_shouldGetMailItemStatus() {
        String trackingNumber = generateString();
        MailItem mailItem = generateMailItem(
                trackingNumber,
                DeliveryStatus.IN_DELIVERY,
                "sender",
                "recipient"
        );
        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber))
                .thenReturn(Optional.of(mailItem));

        DeliveryStatus status = mailItemService.getStatus(trackingNumber);

        assertEquals(mailItem.getStatus().name(), status.name());
    }

    @Test
    public void getStatus_shouldThrowException_whenMailItemNotFound() {
        String trackingNumber = generateString();

        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> mailItemService.getStatus(trackingNumber),
                "Can't find mail item with id: " + trackingNumber
        );
    }

    @Test
    public void getMovementHistory_shouldGetMovementsHistory_whenMailItemFound() {
        String trackingNumber = generateString();
        MailItem mailItem = generateMailItem(
                trackingNumber,
                DeliveryStatus.IN_DELIVERY,
                "sender",
                "recipient"
        );
        PostOffice postOffice1 = generatePostOffice(generateId(), "name1", "address1", "123456");
        PostOffice postOffice2 = generatePostOffice(generateId(), "name2", "address2", "654321");
        mailItem.setMailMovements(
                Arrays.asList(
                        generateMailMovement(MovementType.ARRIVAL, postOffice1, mailItem),
                        generateMailMovement(MovementType.DEPARTURE, postOffice1, mailItem),
                        generateMailMovement(MovementType.ARRIVAL, postOffice2, mailItem)
                )
        );

        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber)).thenReturn(Optional.of(mailItem));

        List<MailMovement> movementHistory = mailItemService.getMovementHistory(trackingNumber);

        assertIterableEquals(mailItem.getMailMovements(), movementHistory);
    }

    @Test
    public void getMovementHistory_shouldThrowException_whenMailItemNotFound() {
        String trackingNumber = generateString();

        Mockito.when(mailItemRepository.findByTrackingNumber(trackingNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> mailItemService.getStatus(trackingNumber),
                "Can't find mail item with id: " + trackingNumber
        );
    }

    private MailItem generateMailItem(String trackingNumber,
                                      DeliveryStatus status,
                                      String sender,
                                      String recipient) {
        MailItem mailItem = new MailItem();
        mailItem.setId(generateId());
        mailItem.setTrackingNumber(trackingNumber);
        mailItem.setStatus(status);
        mailItem.setSender(sender);
        mailItem.setRecipient(recipient);

        return mailItem;
    }

    private PostOffice generatePostOffice(Long id, String name, String address, String index) {
        PostOffice postOffice = new PostOffice();
        postOffice.setId(id);
        postOffice.setName(name);
        postOffice.setAddress(address);
        postOffice.setIndex(index);

        return postOffice;
    }

    private MailMovement generateMailMovement(MovementType movementType,
                                              PostOffice postOffice,
                                              MailItem mailItem) {
        MailMovement mailMovement = new MailMovement();
        mailMovement.setId(generateId());
        mailMovement.setMovementType(movementType);
        mailMovement.setPostOffice(postOffice);
        mailMovement.setMailItem(mailItem);

        return mailMovement;
    }

    private Long generateId() {
        return  RandomUtils.nextLong();
    }

    private String generateString() {
        return RandomStringUtils.randomNumeric(8);
    }
}