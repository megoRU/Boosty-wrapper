package org.boosty.entity.messages;

import lombok.Data;
import org.boosty.entity.dialog.Dialogs;

import java.util.List;

@Data
public class Messages {

    private List<Message> data;
    private Dialogs.Extra extra;
}