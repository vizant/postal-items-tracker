package khozhaev.postalitemstracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import khozhaev.postalitemstracker.exception.EntityNotFoundException;
import khozhaev.postalitemstracker.model.PostOffice;
import khozhaev.postalitemstracker.repository.PostOfficeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostOfficeService {

    private final PostOfficeRepository postOfficeRepository;

    public PostOffice getPostOffice(Long id) {
        return postOfficeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find post office with id" + id)
        );
    }

    public List<PostOffice> getAllPostOffice() {
        return postOfficeRepository.findAll();
    }
}
