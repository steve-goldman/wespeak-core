package com.wespeak.core.datamanager;

import java.util.Iterator;

public interface UsersTable
{
    //
    // getters
    //
    boolean exists               (String userId);
    boolean isActive             (String userId);
    long    getActiveTime        (String userId);
    long    getExpirationTime    (String userId);
    Iterator<String> getActiveUsers();
    boolean hasActiveUsers       ();
    String  getOldestActiveUserId();

    //
    // setters
    //
    void    extendActive         (String userId, long until);
    void    setActive            (String userId, long from, long until);
    void    setInactive          (String userId);
}
