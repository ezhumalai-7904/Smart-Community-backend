package com.example.demo.controller;

import com.example.demo.entity.Request;
import com.example.demo.repository.RequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestRepository requestRepository;

    public RequestController(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String type,
            @RequestParam String priority,
            @RequestParam(required = false) MultipartFile image
    ) {

        Request request = new Request();
        request.setTitle(title);
        request.setDescription(description);
        request.setLocation(location);
        request.setType(type);
        request.setPriority(priority);
        request.setStatus("PENDING");

        try {

            if (image != null && !image.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");

                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }

                java.nio.file.Path filePath = uploadPath.resolve(fileName);

                java.nio.file.Files.copy(
                        image.getInputStream(),
                        filePath,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                request.setImageUrl("/uploads/" + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        requestRepository.save(request);

        return ResponseEntity.ok(request);
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @PutMapping("/{id}/accept")
    public Request acceptRequest(@PathVariable Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("ACCEPTED");
        return requestRepository.save(request);
    }

    @PutMapping("/{id}/complete")
    public Request completeRequest(@PathVariable Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("COMPLETED");
        return requestRepository.save(request);
    }
    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }
}