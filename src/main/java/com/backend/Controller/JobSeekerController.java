package com.backend.Controller;

import com.backend.Dto.Request.JobSeekerRequest;
import com.backend.Dto.Response.ApiResponse;
import com.backend.Dto.Response.JobSeekerResponse;
import com.backend.Services.JobSeekerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job-seekers")
@Slf4j
@RequiredArgsConstructor
public class JobSeekerController {
    private final JobSeekerService jobSeekerService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobSeekerResponse>> createJobSeeker(@Valid @RequestBody JobSeekerRequest request) {
        JobSeekerResponse jobSeeker = jobSeekerService.createJobSeeker(request);
        return ResponseEntity.ok(ApiResponse.success(jobSeeker));
    }

}
