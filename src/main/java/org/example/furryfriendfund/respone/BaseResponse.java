package org.example.furryfriendfund.respone;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BaseResponse {
    private Object data;
    private String message;
    private int status;
}
