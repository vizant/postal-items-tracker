package khozhaev.postalitemstracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import khozhaev.postalitemstracker.model.PostOffice;
import khozhaev.postalitemstracker.service.PostOfficeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/post-office")
public class PostOfficeController {

    private final PostOfficeService postOfficeService;

    @GetMapping("{postOfficeId}")
    public ResponseEntity<PostOffice> getPostOffice(@PathVariable Long postOfficeId) {
        PostOffice postOffice = postOfficeService.getPostOffice(postOfficeId);
        return new ResponseEntity<>(postOffice, HttpStatus.OK);
    }

    @GetMapping("post-offices")
    public ResponseEntity<List<PostOffice>> getAllPostOffice() {
        List<PostOffice> postOffices = postOfficeService.getAllPostOffice();
        return new ResponseEntity<>(postOffices, HttpStatus.OK);
    }
}
