package axon.services.item;

import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemQueryService {
    @Autowired
    private QueryGateway queryGateway;
}
