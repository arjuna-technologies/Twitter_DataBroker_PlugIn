/*
 * Copyright (c) 2013-2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.databroker.plugins.twitter;

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
        return _name;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return _properties;
    }

    @Override
    public List<Class<? extends DataFlowNode>> getClasses()
    {
        List<Class<? extends DataFlowNode>> classes = new LinkedList<Class<? extends DataFlowNode>>();

        classes.add(DataSource.class);

        return classes;
    }

    @Override
    public <T extends DataFlowNode> List<String> getMetaPropertyNames(Class<T> dataFlowNodeClass)
    {
        return Collections.emptyList();
    }

    @Override
    public <T extends DataFlowNode> List<String> getPropertyNames(Class<T> dataFlowNodeClass, Map<String, String> metaProperties)
        throws InvalidClassException, InvalidMetaPropertyException, MissingMetaPropertyException
    {
        List<String> propertyNames = new LinkedList<String>();

        propertyNames.add(TwitterDataSource.TIMEINTERVAL_PROPERTYNAME);

        return propertyNames;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataFlowNode> T createDataFlowNode(String name, Class<T> dataFlowNodeClass, Map<String, String> metaProperties, Map<String, String> properties)
        throws InvalidNameException, InvalidPropertyException, MissingPropertyException
    {
        if (dataFlowNodeClass.isAssignableFrom(TwitterDataSource.class))
            return (T) new TwitterDataSource(name, properties);
        else
            return null;
    }

    private String              _name;
    private Map<String, String> _properties;
}
