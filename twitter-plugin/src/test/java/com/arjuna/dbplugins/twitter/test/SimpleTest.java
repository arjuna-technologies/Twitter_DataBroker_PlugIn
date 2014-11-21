/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.twitter.test;

import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import com.arjuna.databroker.data.jee.DataFlowNodeLifeCycleControl;
import com.arjuna.dplugins.twitter.TwitterDataSource;

public class SimpleTest
{
    @Test
    public void simpleInvocation()
    {
    	String              name              = "Twitter Data Source";
    	Map<String, String> properties        = Collections.emptyMap();
        TwitterDataSource   twitterDataSource = new TwitterDataSource(name, properties);

        DataFlowNodeLifeCycleControl.processCreatedDataFlowNode(twitterDataSource, null);
    }
}
