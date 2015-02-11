package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.SupportTable;

public class MemorySupportTable implements SupportTable
{
    @Override
    public boolean supports(String userId, String statementId)
    {
        return false;
    }

    @Override
    public String[] getSupported(String userId)
    {
        return new String[0];
    }

    @Override
    public int getSupportCount(String statementId)
    {
        return 0;
    }

    @Override
    public void support(String userId, String statementId)
    {

    }
}
