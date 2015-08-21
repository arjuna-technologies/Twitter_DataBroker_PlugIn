/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.twitter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.arjuna.databroker.data.DataFlowNode;
import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.InvalidClassException;
import com.arjuna.databroker.data.InvalidMetaPropertyException;
import com.arjuna.databroker.data.InvalidNameException;
import com.arjuna.databroker.data.InvalidPropertyException;
import com.arjuna.databroker.data.MissingMetaPropertyException;
import com.arjuna.databroker.data.MissingPropertyException;

public class TwitterDataSourceFactory implements DataFlowNodeFactory
{
    public TwitterDataSourceFactory(String name, Map<String, String> properties)
    {
        _name       = name;
        _properties = properties;
    }

    @Override
    public String getName()
    {
        System.err.println("getName");
        return _name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        System.err.println("getProperties");
        return _properties;
    }

    @Override
    public List<Class<? extends DataFlowNode>> getClasses()
    {
        System.err.println("getClasses");
        List<Class<? extends DataFlowNode>> classes = new LinkedList<Class<? extends DataFlowNode>>();

        classes.add(DataSource.class);

        return classes;
    }

    @Override
    public <T extends DataFlowNode> List<String> getMetaPropertyNames(Class<T> dataFlowNodeClass)
    {
        System.err.println("getMetaPropertyNames");
        return Collections.emptyList();
    }

    @Override
    public <T extends DataFlowNode> List<String> getPropertyNames(Class<T> dataFlowNodeClass, Map<String, String> metaProperties)
        throws InvalidClassException, InvalidMetaPropertyException, MissingMetaPropertyException
    {
        System.err.println("getPropertyNames");
        List<String> propertyNames = new LinkedList<String>();

        propertyNames.add(TwitterDataSource.TWITTER_CONSUMERKEY_PROPERTYNAME);
        propertyNames.add(TwitterDataSource.TWITTER_CONSUMERSECRET_PROPERTYNAME);
        propertyNames.add(TwitterDataSource.TWITTER_TOKEN_PROPERTYNAME);
        propertyNames.add(TwitterDataSource.TWITTER_TOKENSECRET_PROPERTYNAME);
        propertyNames.add(TwitterDataSource.TWITTER_TRACKTERM_PROPERTYNAME);
        propertyNames.add(TwitterDataSource.POLLINTERVAL_PROPERTYNAME);

        return propertyNames;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataFlowNode> T createDataFlowNode(String name, Class<T> dataFlowNodeClass, Map<String, String> metaProperties, Map<String, String> properties)
        throws InvalidNameException, InvalidPropertyException, MissingPropertyException
    {
        System.err.println("createDataFlowNode");
        if (dataFlowNodeClass.isAssignableFrom(TwitterDataSource.class))
            return (T) new TwitterDataSource(name, properties);
        else
            return null;
    }

    private String              _name;
    private Map<String, String> _properties;
}
