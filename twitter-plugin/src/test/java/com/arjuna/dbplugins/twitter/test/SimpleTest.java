/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.twitter.test;

import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;
import com.arjuna.databroker.data.jee.DataFlowNodeLifeCycleControl;
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
        properties.put(TwitterDataSource.TWITTER_TOKENSECRET_PROPERTYNAME,    authenticationProperties.getSecret());
        properties.put(TwitterDataSource.TWITTER_TRACKTERM_PROPERTYNAME,      "newcastle");
        properties.put(TwitterDataSource.POLLINTERVAL_PROPERTYNAME,           "4");

        TwitterDataSource twitterDataSource = new TwitterDataSource(name, properties);

//        DataFlowNodeLifeCycleControl.processCreatedDataFlowNode(twitterDataSource, null);

        twitterDataSource.startup();
        twitterDataSource.activate();
        try
        {
            Thread.sleep(12000);
        }
        catch (Throwable throwable)
        {
        }
        twitterDataSource.deactivate();
        twitterDataSource.shutdown();
    }
}
