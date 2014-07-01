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
//import javax.ejb.Startup;
import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataFlowNodeFactoryInventory;
import com.arjuna.dbplugins.interconnect.webservice.WebServiceDataFlowNodeFactory;

@Startup
@Singleton
public class InterconnectDataFlowNodeFactoriesSetup
{
    @PostConstruct
    public void setup()
    {
        DataFlowNodeFactory webserviceDataFlowNodeFactory = new WebServiceDataFlowNodeFactory("WebService Interconnect Data Flow Node Factories", Collections.<String, String>emptyMap());

        _dataFlowNodeFactoryInventory.addDataFlowNodeFactory(webserviceDataFlowNodeFactory);
    }

    @PreDestroy
    public void cleanup()
    {
        _dataFlowNodeFactoryInventory.removeDataFlowNodeFactory("WebService Interconnect Data Flow Node Factories");
    }

    @EJB(lookup="java:global/databroker/control-core/DataFlowNodeFactoryInventory")
    private DataFlowNodeFactoryInventory _dataFlowNodeFactoryInventory;
}
