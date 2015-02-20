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
    private final LinkedList<UserData>  activeUsers = new LinkedList<UserData>();

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
    public int getActiveUserCount()
    {
        return activeUsers.size();
    }

    @Override
    public String getNextActiveUserIdToTimeout()
    {
        return activeUsers.getFirst().userId;
    }

    @Override
    public void extendActive(final String userId, final long until)
    {
        final UserData userData = usersById.get(userId);
        userData.activeUntil = until;

        // give it a new spot in the sorted active users list
        activeUsers.remove(userData);
        addActiveUserSorted(userData);
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

        addActiveUserSorted(userData);
    }

    private void addActiveUserSorted(final UserData userData)
    {
        if (activeUsers.isEmpty() || userData.activeUntil < activeUsers.getFirst().activeUntil)
        {
            activeUsers.addFirst(userData);
            return;
        }

        final ListIterator<UserData> iter = activeUsers.listIterator(1);
        boolean added = false;
        while (iter.hasNext())
        {
            if (userData.activeUntil < iter.next().activeUntil)
            {
                iter.previous();
                iter.add(userData);
                added = true;
                break;
            }
        }

        if (!added)
        {
            iter.add(userData);
        }
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
