package onuroztnc.logservice.Repository;

import onuroztnc.logservice.Model.Log;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<Log, String> {
}
