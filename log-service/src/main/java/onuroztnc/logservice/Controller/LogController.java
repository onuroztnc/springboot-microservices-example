package onuroztnc.logservice.Controller;

import lombok.RequiredArgsConstructor;
import onuroztnc.logservice.Payload.LogRequest;
import onuroztnc.logservice.Payload.LogResponse;
import onuroztnc.logservice.Service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping("/createLog")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLog(@RequestBody LogRequest logRequest) {
        logService.createLog(logRequest);
    }

    @GetMapping("/getAllLogs")
    @ResponseStatus(HttpStatus.OK)
    public List<LogResponse> getAllLogs() {
        return logService.getAllLogs();
    }

}