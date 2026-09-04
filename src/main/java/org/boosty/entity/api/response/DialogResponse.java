package org.boosty.entity.api.response;

import lombok.Data;
import org.boosty.entity.Dialog;

import java.util.List;

@Data
public class DialogResponse implements ApiResponse {

    private List<Dialog> data;
    private Dialog.Extra extra;
}
