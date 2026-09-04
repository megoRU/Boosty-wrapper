package org.boosty.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Subscriber {

    private long id;
    private String name;
    private String avatarUrl;
    private long onTime;
    private String status;
    private int payments;
    private int price;
    private boolean subscribed;
    private long levelId;
    private String levelName;
    private int levelPrice;
}
