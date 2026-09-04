package org.boosty.entity.api.response;

import lombok.*;
import org.boosty.entity.Subscriber;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberResponse implements ApiResponse {

    private List<Subscriber> data;
    private int limit;
    private int total;
    private int offset;
}
