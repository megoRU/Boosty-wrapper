package org.boosty.entity.dialog;

import lombok.Data;

@Data
public class DialogRelation {

    private Long startAt;
    private boolean canWrite;
    private boolean canWriteMe;
    private boolean isChatmateBlackListed;
    private String type;
    private boolean needDonation;
    private boolean isBlackListed;
    private boolean chatmateRequiresVerificationForPayments;
}