package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.SupportTable;

import java.util.*;

public class MemorySupportTable implements SupportTable
{
    private final Map<String, Set<String>> supportedStatementIdsByUserId = new HashMap<String, Set<String>>();
    private final Map<String, Integer>     supportCounts                 = new HashMap<String, Integer>();

    @Override
    public boolean supports(final String userId, final String statementId)
    {
        final Set<String> statementIds = supportedStatementIdsByUserId.get(userId);

        return statementIds != null && statementIds.contains(statementId);
    }

    @Override
    public Iterator<String> getSupported(final String userId)
    {
        if (!supportedStatementIdsByUserId.containsKey(userId))
        {
            supportedStatementIdsByUserId.put(userId, new LinkedHashSet<String>());
        }

        return supportedStatementIdsByUserId.get(userId).iterator();
    }

    @Override
    public int getSupportCount(final String statementId)
    {
        final Integer count = supportCounts.get(statementId);

        return count != null ? count : 0;
    }

    @Override
    public void support(final String userId, final String statementId)
    {
        if (!supportedStatementIdsByUserId.containsKey(userId))
        {
            supportedStatementIdsByUserId.put(userId, new LinkedHashSet<String>());
        }

        supportedStatementIdsByUserId.get(userId).add(statementId);

        if (!supportCounts.containsKey(statementId))
        {
            supportCounts.put(statementId, 0);
        }

        supportCounts.put(statementId, supportCounts.get(statementId) + 1);
    }
}
