package khozhaev.postalitemstracker.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import khozhaev.postalitemstracker.exception.EntityNotFoundException;
import khozhaev.postalitemstracker.model.PostOffice;
import khozhaev.postalitemstracker.repository.PostOfficeRepository;

@ExtendWith(MockitoExtension.class)
class PostOfficeServiceTest {

    @InjectMocks
    private PostOfficeService postOfficeService;

    @Mock
    private PostOfficeRepository postOfficeRepository;

    @Test
    public void getPostOffice_shouldThrowException_whenPostOfficeByIdNotFound() {
        Long postOfficeId = RandomUtils.nextLong();

        Mockito.when(postOfficeRepository.findById(postOfficeId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postOfficeService.getPostOffice(postOfficeId),
                "Can't find post office with id" + postOfficeId
        );
    }

    @Test
    public void getPostOffice_shouldGetPostOffice() {
        Long postOfficeId = RandomUtils.nextLong();
        PostOffice expectedPostOffice = new PostOffice();
        expectedPostOffice.setId(postOfficeId);
        expectedPostOffice.setName("name");
        expectedPostOffice.setIndex("123456");
        expectedPostOffice.setAddress("address");

        Mockito.when(postOfficeRepository.findById(postOfficeId))
                .thenReturn(Optional.of(expectedPostOffice));

        PostOffice actualPostOffice = postOfficeService.getPostOffice(postOfficeId);
        assertEquals(expectedPostOffice, actualPostOffice);
    }
}