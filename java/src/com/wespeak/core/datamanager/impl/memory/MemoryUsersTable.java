package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.UsersTable;

import java.util.*;

public class MemoryUsersTable implements UsersTable
{
    private class UserData
    {
        final String userId;

        boolean isActive;
        long    activeFrom;
        long    activeUntil;

        UserData(final String userId)
        {
            this.userId = userId;
        }
    }

    private final Map<String, UserData> usersById   = new HashMap<String, UserData>();
    private final Queue<UserData>       activeUsers = new LinkedList<UserData>();

    @Override
    public boolean exists(final String userId)
    {
        return usersById.containsKey(userId);
    }

    @Override
    public boolean isActive(final String userId)
    {
        return usersById.get(userId).isActive;
    }

    @Override
    public long getActiveTime(final String userId)
    {
        return usersById.get(userId).activeFrom;
    }

    @Override
    public long getExpirationTime(final String userId)
    {
        return usersById.get(userId).activeUntil;
    }

    @Override
    public Iterator<String> getActiveUsers()
    {
        final Iterator<UserData> iter = activeUsers.iterator();

        return new Iterator<String>()
        {
            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public String next()
            {
                return iter.next().userId;
            }
        };
    }

    @Override
    public boolean hasActiveUsers()
    {
        return !activeUsers.isEmpty();
    }

    @Override
    public String getOldestActiveUserId()
    {
        return activeUsers.peek().userId;
    }

    @Override
    public void extendActive(final String userId, final long until)
    {
        usersById.get(userId).activeUntil = until;
    }

    @Override
    public void setActive(final String userId, final long from, final long until)
    {
        if (!exists(userId))
        {
            usersById.put(userId, new UserData(userId));
        }

        final UserData userData = usersById.get(userId);
        userData.isActive = true;
        userData.activeFrom = from;
        userData.activeUntil = until;

        activeUsers.add(userData);
    }

    @Override
    public void setInactive(final String userId)
    {
        final UserData userData = usersById.get(userId);
        userData.isActive = false;

        // TODO: make this sub-linear
        activeUsers.remove(userData);
    }
}
