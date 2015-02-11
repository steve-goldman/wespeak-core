package com.wespeak.core.datamanager;

public interface SupportTable
{
    //
    // getters
    //
    boolean  supports       (String userId, String statementId);
    String[] getSupported   (String userId);
    int      getSupportCount(String statementId);

    //
    // setters
    //
    void     support        (String userId, String statementId);
}
