package org.boosty.entity.dialog;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Dialogs {

    private Long id;
    private Long createdAt;
    private Integer unreadMsgCount;
    private LastMessage lastMessage;
    private Chatmate chatmate;

    @Data
    public static class Chatmate {

        private Long id;
        private String name;
        private String url;
        private String avatarUrl;
        private String currency;
        private String blogCurrency;
        private boolean isBlogger;
        private boolean isOfficial;
        private boolean hasAvatar;
        private boolean hasAdultContent;
        private List<String> acceptedCurrencies;
    }

    @Data
    public static class LastMessage {

        private Long id;
        private Long dialogId;
        private Long authorId;
        private Long createdAt;
        private Integer price;
        private boolean isDeleted;
        private boolean isRead;
        private boolean isPaid;
        private boolean isFeePaid;
        private String previewType;
        private List<DataItem> data;
        private Attachments attachments;
        private Map<String, Double> currencyPrices;
        private Donation donation;
        private List<Object> teaser;
    }

    @Data
    public static class DataItem {

        private String type;
        private String content;
        private String modificator;
        private String name;
        private String largeUrl;
        private String mediumUrl;
        private String smallUrl;
        private String id;
        private boolean isAnimated;
    }

    @Data
    public static class Attachments {

        private AttachmentInfo images;
        private AttachmentInfo videos;
        private AttachmentInfo text;
        private AttachmentInfo audios;
        private AttachmentInfo files;
    }

    @Data
    public static class AttachmentInfo {

        private Integer count;
        private String previewUrl;
    }

    @Data
    public static class Donation {

        private Long id;
        private Long bloggerId;
        private Long targetId;
        private Long createdAt;
        private Integer amount;
        private String type;
        private boolean isFeePaid;
        private User user;
        private Map<String, Double> currencyAmounts;
    }

    @Data
    public static class User {

        private Long id;
        private String name;
        private String email;
        private String avatarUrl;
        private boolean hasAvatar;
        private boolean isOfficial;
    }

    @Data
    public static class Extra {

        private Integer total;
        private Integer offset;
    }
}