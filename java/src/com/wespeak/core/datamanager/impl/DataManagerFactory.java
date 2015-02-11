package com.wespeak.core.datamanager.impl;

import com.wespeak.core.datamanager.DataManager;
import com.wespeak.core.datamanager.DataManagerImpl;
import com.wespeak.core.datamanager.DataManagerType;
import com.wespeak.core.datamanager.impl.memory.MemoryStatementsTable;
import com.wespeak.core.datamanager.impl.memory.MemorySupportTable;
import com.wespeak.core.datamanager.impl.memory.MemoryUsersTable;
import com.wespeak.core.datamanager.impl.memory.MemoryVotesTable;

public class DataManagerFactory
{
    private static final DataManagerType DefaultType = DataManagerType.MEMORY;

    public static DataManager getInstance()
    {
        return getInstance(DefaultType);
    }

    public static DataManager getInstance(DataManagerType type)
    {
        switch (type)
        {
            case MEMORY:
            {
                return new DataManagerImpl(
                        new MemoryUsersTable(),
                        new MemoryStatementsTable(),
                        new MemorySupportTable(),
                        new MemoryVotesTable());
            }
        }

        throw new IllegalArgumentException("Unexpected type=" + type.name());
    }
}
