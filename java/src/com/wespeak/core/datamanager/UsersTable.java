package com.wespeak.core.datamanager;

public interface UsersTable
{
    //
    // getters
    //
    boolean exists               (String userId);
    boolean isActive             (String userId);
    long    getActiveTime        (String userId);
    long    getExpirationTime    (String userId);
    String  getOldestActiveUserId();

    //
    // setters
    //
    void    setActive            (String userId, long from, long until);
    void    setInactive          (String userId);
}
