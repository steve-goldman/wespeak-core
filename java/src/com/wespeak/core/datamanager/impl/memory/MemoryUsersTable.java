package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.UsersTable;

public class MemoryUsersTable implements UsersTable
{
    @Override
    public boolean exists(String userId)
    {
        return false;
    }

    @Override
    public boolean isActive(String userId)
    {
        return false;
    }

    @Override
    public long getActiveTime(String userId)
    {
        return 0;
    }

    @Override
    public long getExpirationTime(String userId)
    {
        return 0;
    }

    @Override
    public String getOldestActiveUserId()
    {
        return null;
    }

    @Override
    public void setActive(String userId, long from, long until)
    {

    }

    @Override
    public void setInactive(String userId)
    {

    }
}
