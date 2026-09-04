package org.boosty.entity;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Subscriber {

    private long id;
    private String name;
    private String email;
    private String avatarUrl;
    private long onTime;
    private String status;
    private int payments;
    private int price;
    private boolean subscribed;

    @SerializedName("isBlackListed")
    private boolean isBlackListed;

    @SerializedName("isFeePaid")
    private boolean isFeePaid;

    private boolean canWrite;

    @SerializedName("isOfficial")
    private boolean isOfficial;

    private boolean hasAvatar;

    private Level level;

    public long getLevelId() {
        return level != null ? level.getId() : 0L;
    }

    public String getLevelName() {
        return level != null ? level.getName() : null;
    }

    public int getLevelPrice() {
        return level != null ? level.getPrice() : 0;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Level {
        private long id;
        private String name;
        private int price;
        private long ownerId;

        @SerializedName("isHidden")
        private boolean isHidden;

        @SerializedName("isArchived")
        private boolean isArchived;

        @SerializedName("isLimited")
        private boolean isLimited;

        private boolean deleted;
        private long createdAt;
    }
}
