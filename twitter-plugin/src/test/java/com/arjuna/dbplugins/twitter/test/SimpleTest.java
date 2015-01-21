/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.twitter.test;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import com.arjuna.databroker.data.connector.ObservableDataProvider;
import com.arjuna.databroker.data.connector.ObserverDataConsumer;
import com.arjuna.databroker.data.jee.DataFlowNodeLifeCycleControl;
import com.arjuna.dbutilities.testsupport.dataflownodes.dummy.DummyDataProcessor;
import com.arjuna.dplugins.twitter.TwitterDataSource;

public class SimpleTest
{
    @Test
    public void simpleInvocation()
    {
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

        DataFlowNodeLifeCycleControl.processCreatedDataFlowNode(twitterDataSource, null);
        DataFlowNodeLifeCycleControl.processCreatedDataFlowNode(dummyDataProcessor, null);

        ((ObservableDataProvider<String>) twitterDataSource.getDataProvider(String.class)).addDataConsumer((ObserverDataConsumer<String>) dummyDataProcessor.getDataConsumer(String.class));

        try
        {
            Thread.sleep(60000);
        }
        catch (InterruptedException interruptedException)
        {
            fail("Interrupted during sleep");
        }

        DataFlowNodeLifeCycleControl.removeDataFlowNode(dummyDataProcessor);

        assertTrue("Didn't receive any tweets", dummyDataProcessor.receivedData().size() > 0);
    }
}
