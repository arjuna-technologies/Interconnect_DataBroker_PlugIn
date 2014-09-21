/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.arjuna.databroker.data.DataFlowNode;
import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataSink;
import com.arjuna.databroker.data.DataSource;
import com.arjuna.databroker.data.InvalidClassException;
import com.arjuna.databroker.data.InvalidMetaPropertyException;
import com.arjuna.databroker.data.InvalidNameException;
import com.arjuna.databroker.data.InvalidPropertyException;
import com.arjuna.databroker.data.MissingMetaPropertyException;
import com.arjuna.databroker.data.MissingPropertyException;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.AcceptorWebServiceDataSource;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.ProviderWebServiceDataSink;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.PullWebServiceDataSource;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.PushWebServiceDataSink;

public class SerializableObjectInterconnectDataFlowNodeFactory implements DataFlowNodeFactory
{
    public SerializableObjectInterconnectDataFlowNodeFactory(String name, Map<String, String> properties)
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
        classes.add(DataSink.class);

        return classes;
    }

    @Override
    public <T extends DataFlowNode> List<String> getMetaPropertyNames(Class<T> dataFlowNodeClass)
        throws InvalidClassException
    {
        if (dataFlowNodeClass.equals(DataSource.class) || dataFlowNodeClass.equals(DataSink.class))
        {
            List<String> metaPropertyNames = new LinkedList<String>();

            metaPropertyNames.add("Type");

            return metaPropertyNames;
        }
        else
            throw new InvalidClassException("Unsupported class", dataFlowNodeClass.getName());
    }

    @Override
    public <T extends DataFlowNode> List<String> getPropertyNames(Class<T> dataFlowNodeClass, Map<String, String> metaProperties)
        throws InvalidClassException, InvalidMetaPropertyException, MissingMetaPropertyException
    {
        if (dataFlowNodeClass.equals(DataSource.class))
        {
            if ((metaProperties.size() == 1) && metaProperties.containsKey("Type"))
            {
                List<String> propertyNames = new LinkedList<String>();

                String type = metaProperties.get("Type");
                if (type.equals("Pull"))
                {
                    propertyNames.add(PullWebServiceDataSource.SERVICEURL_PROPERTYNAME);
                    propertyNames.add(PullWebServiceDataSource.ENDPOINTPATH_PROPERTYNAME);
                    propertyNames.add(PullWebServiceDataSource.SCHEDULEDELAY_PROPERTYNAME);
                    propertyNames.add(PullWebServiceDataSource.SCHEDULEPERIOD_PROPERTYNAME);
                }
                else if (type.equals("Acceptor"))
                {
                    propertyNames.add(AcceptorWebServiceDataSource.ENDPOINTPATH_PROPERTYNAME);
                }
                else
                    throw new InvalidMetaPropertyException("Expecting value of 'Type' meta property 'Pull' or 'Acceptor'", "Type", type);

                return propertyNames;
            }
            else
                throw new MissingMetaPropertyException("No metaproperties expected", "Type");
        }
        else if ((metaProperties.size() == 1) && metaProperties.containsKey("Type"))
        {
            if (metaProperties.isEmpty())
            {
                List<String> propertyNames = new LinkedList<String>();

                String type = metaProperties.get("Type");
                if (type.equals("Push"))
                {
                    propertyNames.add(PushWebServiceDataSink.SERVICEROOTURL_PROPERTYNAME);
                    propertyNames.add(PushWebServiceDataSink.ENDPOINTPATH_PROPERTYNAME);
                }
                else if (type.equals("Provider"))
                    propertyNames.add(ProviderWebServiceDataSink.ENDPOINTPATH_PROPERTYNAME);
                else
                    throw new InvalidMetaPropertyException("Expecting value of 'Type' meta property 'Push' or 'Provider'", "Type", type);

                return propertyNames;
            }
            else
                throw new MissingMetaPropertyException("No metaproperties expected", "Type");
        }
        else
            throw new InvalidClassException("Unsupported class", dataFlowNodeClass.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataFlowNode> T createDataFlowNode(String name, Class<T> dataFlowNodeClass, Map<String, String> metaProperties, Map<String, String> properties)
        throws InvalidClassException, InvalidNameException, InvalidMetaPropertyException, MissingMetaPropertyException, InvalidPropertyException, MissingPropertyException
    {
        if (dataFlowNodeClass.equals(DataSource.class))
        {
            if ((metaProperties.size() == 1) && metaProperties.containsKey("Type"))
            {
                String type = metaProperties.get("Type");
                if (type.equals("Pull"))
                    return (T) new PullWebServiceDataSource(name, properties);
                else if (type.equals("Acceptor"))
                    return (T) new AcceptorWebServiceDataSource(name, properties);
                else
                    throw new InvalidMetaPropertyException("Expecting value of 'Type' meta property 'Pull' or 'Acceptor'", "Type", type);
            }
            else
                throw new InvalidMetaPropertyException("No metaproperties expected", null, null);
        }
        else if (dataFlowNodeClass.equals(DataSink.class))
        {
            if ((metaProperties.size() == 1) && metaProperties.containsKey("Type"))
            {
                String type = metaProperties.get("Type");
                if (type.equals("Push"))
                    return (T) new PushWebServiceDataSink(name, properties);
                else if (type.equals("Provider"))
                    return (T) new ProviderWebServiceDataSink(name, properties);
                else
                    throw new InvalidMetaPropertyException("Expecting value of 'Type' meta property 'Push' or 'Provider'", "Type", type);
            }
            else
                throw new InvalidMetaPropertyException("No metaproperties expected", null, null);
        }
        else
            throw new InvalidClassException("Unsupported class", dataFlowNodeClass.getName());
    }

    private String              _name;
    private Map<String, String> _properties;
}