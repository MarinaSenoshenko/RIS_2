package manager.api.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manager.api.dto.CrackHashService;
import manager.api.dto.requests.*;
import manager.urls.ManagerUrls;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(ManagerUrls.API_HASH_URL)
public class CrackHashController {
    private CrackHashService crackHashService;

    @PostMapping(ManagerUrls.CRACK_URL)
    public ResponseEntity<RequestIdDto> crackHash(@RequestBody RequestCrackDto request) {
        log.info("Received request to crack hash: {}", request);
        return new ResponseEntity<>(
                new RequestIdDto(crackHashService.crackHash(request.getHash(), request.getMaxLength())), HttpStatus.OK);
    }

    @GetMapping(ManagerUrls.STATUS_URL)
    public ResponseEntity<RequestStatusDto> getStatus(@PathVariable String requestId) {
        log.info("Received request to get status of request: {}", requestId);
        return new ResponseEntity<>(crackHashService.getStatus(requestId), HttpStatus.OK);
    }
}
