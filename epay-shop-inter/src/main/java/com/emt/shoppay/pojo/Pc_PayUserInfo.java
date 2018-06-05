/**
 *
 */
package com.emt.shoppay.pojo;

import java.io.Serializable;

/**
 * @author Eddy
 *
 */
public class Pc_PayUserInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2885920928393183847L;
    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
