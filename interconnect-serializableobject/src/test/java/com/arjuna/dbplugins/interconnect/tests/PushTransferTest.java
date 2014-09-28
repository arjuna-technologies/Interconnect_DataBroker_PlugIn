/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.tests;

import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import com.arjuna.databroker.data.jee.DataFlowNodeLifeCycleControl;
// import static org.junit.Assert.*;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.SerializableObjectPushDataSink;

public class PushTransferTest
{
    @Test
    public void simplestPushTransfer()
    {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(SerializableObjectPushDataSink.SERVICEROOTURL_PROPERTYNAME, "http://ouseburn:8080");
        properties.put(SerializableObjectPushDataSink.ENDPOINTPATH_PROPERTYNAME, "foo");

        SerializableObjectPushDataSink serializableObjectPushDataSink = new SerializableObjectPushDataSink("Serializable Object Provider Data Sink", properties);

        DataFlowNodeLifeCycleControl.processCreatedDataFlowNode(serializableObjectPushDataSink, null);

        serializableObjectPushDataSink.consume("Data String");

        DataFlowNodeLifeCycleControl.removeDataFlowNode(serializableObjectPushDataSink);
    }
}