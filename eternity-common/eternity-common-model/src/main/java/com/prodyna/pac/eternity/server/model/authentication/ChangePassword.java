package com.prodyna.pac.eternity.server.model.authentication;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePassword {

    @NotNull
    @Size(min = 1, max = 30)
    private String oldPassword;

    @NotNull
    @Size(min = 1, max = 30)
    private String newPassword;

    public ChangePassword() {

    }

    public String getOldPassword() {

        return oldPassword;

    }

    public void setOldPassword(final String oldPassword) {

        this.oldPassword = oldPassword;

    }

    public String getNewPassword() {

        return newPassword;

    }

    public void setNewPassword(final String newPassword) {

        this.newPassword = newPassword;

    }

    @Override
    public String toString() {

        return "ChangePassword{" +
                ", oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }

}