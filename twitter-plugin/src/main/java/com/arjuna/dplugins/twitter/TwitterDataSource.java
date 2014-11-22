/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dplugins.twitter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arjuna.databroker.data.DataFlow;
import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.jee.annotation.DataProviderInjection;
import com.arjuna.databroker.data.jee.annotation.PostActivated;
import com.arjuna.databroker.data.jee.annotation.PostConfig;
import com.arjuna.databroker.data.jee.annotation.PostCreated;
import com.arjuna.databroker.data.jee.annotation.PreDeactivated;
import com.arjuna.databroker.data.jee.annotation.PreDelete;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterDataSource implements DataSource
{
    private static final Logger logger = Logger.getLogger(TwitterDataSource.class.getName());

    public static final String TWITTER_CONSUMERKEY_PROPERTYNAME    = "Twitter ConsumerKey";
    public static final String TWITTER_CONSUMERSECRET_PROPERTYNAME = "Twitter Consumer Secret";
    public static final String TWITTER_TOKEN_PROPERTYNAME          = "Twitter Token";
    public static final String TWITTER_SECRET_PROPERTYNAME         = "Twitter Secret";
    public static final String POLLINTERVAL_PROPERTYNAME           = "Poll Interval";

    public TwitterDataSource(String name, Map<String, String> properties)
    {
        logger.log(Level.FINE, "TwitterDataSource: " + name + ", " + properties);

        _name       = name;
        _properties = properties;
        _dataFlow   = null;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public void setName(String name)
    {
        _name = name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.unmodifiableMap(_properties);
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        _properties = properties;
    }

    @Override
    public DataFlow getDataFlow()
    {
        return _dataFlow;
    }

    @Override
    public void setDataFlow(DataFlow dataFlow)
    {
        _dataFlow = dataFlow;
    }

    @PostCreated
    public void startup()
    {
        _fetcher = new Fetcher();
        _fetcher.config();
        _fetcher.start();
    }

    @PostActivated
    public void activate()
    {
        _fetcher.activate();
    }

    @PostConfig
    public void config()
    {
        _fetcher.config();
    }

    @PreDeactivated
    public void deactivate()
    {
        _fetcher.deactivate();
    }

    @PreDelete
    public void shutdown()
    {
        try
        {
            _fetcher.finish();
            _fetcher.join();
        }
        catch (Throwable throwable)
        {
            logger.log(Level.WARNING, "Fetcher shutdown failed", throwable);
        }
        _fetcher = null;
    }
 
    private class Fetcher extends Thread
    {
    	private static final int TWEETQUEUESIZE = 1000;
    	
        public Fetcher()
        {
            _twitterClient = null;
            _tweetQueue    = null;
            _pollInterval  = Long.MAX_VALUE;

            _pauseSyncObject = new Object();
            _fetch           = false;
            _finish          = false;
        }

        public void config()
        {
            try
            {
                _tweetQueue = new LinkedBlockingQueue<String>(TWEETQUEUESIZE);

                StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
                endpoint.stallWarnings(false);

                String         consumerKey    = _properties.get(TWITTER_CONSUMERKEY_PROPERTYNAME);
                String         consumerSecret = _properties.get(TWITTER_CONSUMERSECRET_PROPERTYNAME);
                String         token          = _properties.get(TWITTER_TOKEN_PROPERTYNAME);
                String         secret         = _properties.get(TWITTER_SECRET_PROPERTYNAME);
                Authentication authentication = new OAuth1(consumerKey, consumerSecret, token, secret);

                ClientBuilder twitterClientBuilder = new ClientBuilder();
                twitterClientBuilder.name("DataBrokerClient");
                twitterClientBuilder.hosts(Constants.STREAM_HOST);
                twitterClientBuilder.endpoint(endpoint);
                twitterClientBuilder.authentication(authentication);
                twitterClientBuilder.processor(new StringDelimitedProcessor(_tweetQueue));

                _twitterClient = twitterClientBuilder.build();

                _pollInterval = Long.parseLong(_properties.get(POLLINTERVAL_PROPERTYNAME));
            }
            catch (Throwable throwable)
            {
                logger.log(Level.FINE, "TwitterDataSource: Configuring problem \"" + _name + "\"", throwable);
                _twitterClient = null;
                _tweetQueue    = null;
                _pollInterval  = Long.MAX_VALUE;
            }
        }

        public void run()
        {
            while (! _finish)
            {
                try
                {
                    synchronized (_pauseSyncObject)
                    {
                        if ((_twitterClient == null) || _twitterClient.isDone() || (! _fetch))
                            _pauseSyncObject.wait();
                    }

                    if ((_twitterClient != null) && (! _twitterClient.isDone()) && _fetch)
                    {
                        _twitterClient.connect();

                        while ((! _twitterClient.isDone()) && _fetch)
                        {
                            String tweet = _tweetQueue.poll(_pollInterval, TimeUnit.SECONDS);
                            if (tweet != null)
                                _dataProvider.produce(tweet);
                        }

                        _twitterClient.stop();
                    }
                }
                catch (InterruptedException interruptedException)
                {
                }
                catch (Throwable throwable)
                {
                    logger.log(Level.FINE, "TwitterDataSource: Fetched problem \"" + _name + "\"", throwable);
                }
            }
        }

        public void activate()
        {
            synchronized (_pauseSyncObject)
            {
                _fetch = true;
                this.interrupt();
            }
        }

        public void deactivate()
        {
            synchronized (_pauseSyncObject)
            {
                _fetch = false;
                this.interrupt();
            }
        }

        public void finish()
        {
            synchronized (_pauseSyncObject)
            {
                _fetch  = false;
                _finish = true;
                this.interrupt();
            }
        }

        private BasicClient           _twitterClient;
        private BlockingQueue<String> _tweetQueue;
        private long                  _pollInterval;

        private Object           _pauseSyncObject;
        private volatile boolean _fetch;
        private volatile boolean _finish;
    }

    @Override
    public Collection<Class<?>> getDataProviderDataClasses()
    {
        Set<Class<?>> dataProviderDataClasses = new HashSet<Class<?>>();

        dataProviderDataClasses.add(String.class);

        return dataProviderDataClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataProvider<T> getDataProvider(Class<T> dataClass)
    {
        if (dataClass == String.class)
            return (DataProvider<T>) _dataProvider;
        else
            return null;
    }

    public Fetcher _fetcher;

    private String               _name;
    private Map<String, String>  _properties;
    private DataFlow             _dataFlow;
    @DataProviderInjection
    private DataProvider<String> _dataProvider;
}
