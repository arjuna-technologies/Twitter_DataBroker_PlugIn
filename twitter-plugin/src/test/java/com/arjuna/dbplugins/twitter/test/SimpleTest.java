/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.twitter.test;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import com.arjuna.databroker.data.connector.ObservableDataProvider;
import com.arjuna.databroker.data.connector.ObserverDataConsumer;
import com.arjuna.databroker.data.core.DataFlowNodeLifeCycleControl;
import com.arjuna.dbplugins.twitter.TwitterDataSource;
import com.arjuna.dbutils.testsupport.dataflownodes.dummy.DummyDataProcessor;
import com.arjuna.dbutils.testsupport.dataflownodes.lifecycle.TestJEEDataFlowNodeLifeCycleControl;

public class SimpleTest
{
    @Test
    public void simpleInvocation()
    {
        DataFlowNodeLifeCycleControl dataFlowNodeLifeCycleControl = new TestJEEDataFlowNodeLifeCycleControl();

        AuthenticationProperties authenticationProperties = new AuthenticationProperties("authentication.properties");

        String              name       = "Twitter Data Source";
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(TwitterDataSource.TWITTER_CONSUMERKEY_PROPERTYNAME,    authenticationProperties.getConsumerKey());
        properties.put(TwitterDataSource.TWITTER_CONSUMERSECRET_PROPERTYNAME, authenticationProperties.getConsumerSecret());
        properties.put(TwitterDataSource.TWITTER_TOKEN_PROPERTYNAME,          authenticationProperties.getToken());
        properties.put(TwitterDataSource.TWITTER_TOKENSECRET_PROPERTYNAME,    authenticationProperties.getTokenSecret());
        properties.put(TwitterDataSource.TWITTER_TRACKTERM_PROPERTYNAME,      "newcastle");
        properties.put(TwitterDataSource.POLLINTERVAL_PROPERTYNAME,           "5");

        TwitterDataSource  twitterDataSource  = new TwitterDataSource(name, properties);
        DummyDataProcessor dummyDataProcessor = new DummyDataProcessor("Dummy Data Processor", Collections.<String, String>emptyMap());

        dataFlowNodeLifeCycleControl.completeCreationAndActivateDataFlowNode(UUID.randomUUID().toString(), twitterDataSource, null);
        dataFlowNodeLifeCycleControl.completeCreationAndActivateDataFlowNode(UUID.randomUUID().toString(), dummyDataProcessor, null);

        ((ObservableDataProvider<String>) twitterDataSource.getDataProvider(String.class)).addDataConsumer((ObserverDataConsumer<String>) dummyDataProcessor.getDataConsumer(String.class));

        try
        {
            Thread.sleep(60000);
        }
        catch (InterruptedException interruptedException)
        {
            fail("Interrupted during sleep");
        }

        dataFlowNodeLifeCycleControl.removeDataFlowNode(dummyDataProcessor);

        assertTrue("Didn't receive any tweets", dummyDataProcessor.receivedData().size() > 0);
    }
}
