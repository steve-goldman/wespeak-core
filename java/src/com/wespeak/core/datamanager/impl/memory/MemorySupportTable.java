package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.SupportTable;

import java.util.Iterator;

public class MemorySupportTable implements SupportTable
{
    @Override
    public boolean supports(String userId, String statementId)
    {
        return false;
    }

    @Override
    public Iterator<String> getSupported(String userId)
    {
        return null;
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
