package com.prodyna.pac.eternity.common.model.authentication;

import com.prodyna.pac.eternity.common.model.AbstractNode;

import java.util.Calendar;

/**
 * Alternative login token which can be stored for some time.
 */
public class RememberMe extends AbstractNode {

    /**
     * The token to authenticate against the hashedToken.
     */
    private String token;

    /**
     * the stored database hash.
     */
    private String hashedToken;

    /**
     * The initial creation time.
     */
    private Calendar createdTime;

    /**
     * Basic Getter
     *
     * @return the token
     */
    public String getToken() {

        return this.token;
    }

    /**
     * Basic Setter
     *
     * @param token to be set
     */
    public void setToken(final String token) {

        this.token = token;
    }

    /**
     * Basic Getter
     *
     * @return the hashedToken
     */
    public String getHashedToken() {

        return hashedToken;
    }

    /**
     * Basic Setter
     *
     * @param hashedToken to be set
     */
    public void setHashedToken(final String hashedToken) {

        this.hashedToken = hashedToken;
    }

    /**
     * Basic Getter
     *
     * @return the createdTime
     */

    public Calendar getCreatedTime() {

        return this.createdTime;
    }

    /**
     * Basic Setter
     *
     * @param createdTime to be set
     */
    public void setCreatedTime(final Calendar createdTime) {

        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RememberMe that = (RememberMe) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (hashedToken != null ? !hashedToken.equals(that.hashedToken) : that.hashedToken != null) return false;
        return !(createdTime != null ? !createdTime.equals(that.createdTime) : that.createdTime != null);

    }

    @Override
    public int hashCode() {

        return super.hashCode();
    }

    @Override
    public String toString() {

        return "RememberMe{" +
                "token='" + token + '\'' +
                ", hashedToken='" + hashedToken + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }

}
