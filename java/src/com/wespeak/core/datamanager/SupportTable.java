package com.wespeak.core.datamanager;

import java.util.Iterator;

public interface SupportTable
{
    //
    // getters
    //
    boolean  supports            (String userId, String statementId);
    int      getSupportCount     (String statementId);
    Iterator<String> getSupported(String userId);

    //
    // setters
    //
    void     support             (String userId, String statementId);
}
