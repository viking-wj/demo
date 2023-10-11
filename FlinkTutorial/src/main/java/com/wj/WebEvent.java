package com.wj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/11 14:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebEvent {
    public String user;

    public String url;

    public Long timestamp;

}
