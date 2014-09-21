/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect;

import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataFlowNodeFactoryInventory;
import com.arjuna.dbplugins.interconnect.domdocument.DOMDocumentInterconnectDataFlowNodeFactory;
import com.arjuna.dbplugins.interconnect.serializableobject.SerializableObjectInterconnectDataFlowNodeFactory;

@Startup
@Singleton
public class InterconnectDataFlowNodeFactoriesSetup
{
    @PostConstruct
    public void setup()
    {
        DataFlowNodeFactory domDocumentInterconnectDataFlowNodeFactory        = new DOMDocumentInterconnectDataFlowNodeFactory("DOM Document Interconnect Data Flow Node Factories", Collections.<String, String>emptyMap());
        DataFlowNodeFactory serializableObjectInterconnectDataFlowNodeFactory = new SerializableObjectInterconnectDataFlowNodeFactory("Serializable Object Interconnect Data Flow Node Factories", Collections.<String, String>emptyMap());

        _dataFlowNodeFactoryInventory.addDataFlowNodeFactory(domDocumentInterconnectDataFlowNodeFactory);
        _dataFlowNodeFactoryInventory.addDataFlowNodeFactory(serializableObjectInterconnectDataFlowNodeFactory);
    }

    @PreDestroy
    public void cleanup()
    {
        _dataFlowNodeFactoryInventory.removeDataFlowNodeFactory("DOM Document Interconnect Data Flow Node Factories");
        _dataFlowNodeFactoryInventory.removeDataFlowNodeFactory("Serializable Object Interconnect Data Flow Node Factories");
    }

    @EJB(lookup="java:global/databroker/control-core/DataFlowNodeFactoryInventory")
    private DataFlowNodeFactoryInventory _dataFlowNodeFactoryInventory;
}
