package com.bantanger.qqcommon;

import java.io.Serializable;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String useId; // 用户名
    private String passwd;

    public User() {
    }

    public String getUseId() {
        return useId;
    }

    public void setUseId(String useId) {
        this.useId = useId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public User(String useId, String passwd) {
        this.useId = useId;
        this.passwd = passwd;
    }
}
