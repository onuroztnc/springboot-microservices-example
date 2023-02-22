package onuroztnc.logservice.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onuroztnc.logservice.Model.Log;
import onuroztnc.logservice.Payload.LogRequest;
import onuroztnc.logservice.Payload.LogResponse;
import onuroztnc.logservice.Repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public void createLog(LogRequest logRequest) {
        Log log = Log.builder()
                .username(logRequest.getUsername())
                .userId(logRequest.getUserId())
                .productId(logRequest.getProductId())
                .productName(logRequest.getProductName())
                .createdAt(logRequest.getCreatedAt())
                .build();

        logRepository.save(log);
    }

    public List<LogResponse> getAllLogs() {
        List<Log> logs = logRepository.findAll();

        return logs.stream().map(this::mapToLogResponse).toList();
    }

    private LogResponse mapToLogResponse(Log log) {
        return LogResponse.builder()
                .username(log.getUsername())
                .userId(log.getUserId())
                .productId(log.getProductId())
                .productName(log.getProductName())
                .createdAt(log.getCreatedAt())
                .build();
    }
}