package org.boosty.entity.api.response;

import lombok.Data;
import org.boosty.entity.dialog.Dialogs;

import java.util.List;

@Data
public class DialogsResponse implements ApiResponse {

    private List<Dialogs> data;
    private Dialogs.Extra extra;
}
