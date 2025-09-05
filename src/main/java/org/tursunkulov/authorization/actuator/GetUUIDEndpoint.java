package org.tursunkulov.authorization.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Endpoint(id = "uuid")
public class GetUUIDEndpoint {
    @ReadOperation
    public UUID getUUID() {
        return UUID.randomUUID();
    }
}
