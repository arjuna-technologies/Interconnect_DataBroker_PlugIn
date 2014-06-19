/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.arjuna.dbplugins.interconnect.webservice.dataflownodes.AcceptorWebServiceDataSource;
import com.arjuna.dbplugins.interconnect.webservice.dataflownodes.ProviderWebServiceDataSink;
import com.arjuna.dbplugins.interconnect.webservice.dataflownodes.PullWebServiceDataSource;
import com.arjuna.dbplugins.interconnect.webservice.dataflownodes.PushWebServiceDataSink;

@Startup
@Singleton
public class StandaloneInterconnectSetup
{
    private static final Logger logger = Logger.getLogger(StandaloneInterconnectSetup.class.getName());

    @PostConstruct
    public void setup()
    {
        try
        {
            String              acceptorName       = "Acceptor Name";
            Map<String, String> acceptorProperties = new HashMap<String, String>();
            acceptorProperties.put(AcceptorWebServiceDataSource.ENDPOINTPATH_PROPERTYNAME, "TestPath");
            AcceptorWebServiceDataSource acceptorWebServiceDataSource = new AcceptorWebServiceDataSource(acceptorName, acceptorProperties);

            String              pushName       = "Push Name";
            Map<String, String> pushProperties = new HashMap<String, String>();
            pushProperties.put(PushWebServiceDataSink.SERVICEROOTURL_PROPERTYNAME, "http://localhost:80");
            pushProperties.put(PushWebServiceDataSink.ENDPOINTPATH_PROPERTYNAME, "TestPath");
            PushWebServiceDataSink pushWebServiceDataSink = new PushWebServiceDataSink(pushName, pushProperties);

            DocumentBuilderFactory document1BuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        document1Builder        = document1BuilderFactory.newDocumentBuilder();
            Document               document1               = document1Builder.newDocument();

            Element element1 = document1.createElement("test1");
            element1.appendChild(document1.createTextNode("value1"));
            document1.appendChild(element1);

            logger.fine("****************");
            pushWebServiceDataSink.consume(document1);
            logger.fine("****************");

            Thread.sleep(10000);

            String              pullName       = "Pull Name";
            Map<String, String> pullProperties = new HashMap<String, String>();
            pullProperties.put(PullWebServiceDataSource.SERVICEURL_PROPERTYNAME, "http://localhost:80");
            PullWebServiceDataSource pullWebServiceDataSource = new PullWebServiceDataSource(pullName, pullProperties);

            String              providerName       = "Provider Name";
            Map<String, String> providerProperties = new HashMap<String, String>();
            providerProperties.put(ProviderWebServiceDataSink.SERVICEURL_PROPERTYNAME, "http://localhost:80");
            providerProperties.put(ProviderWebServiceDataSink.SERVICEURL_PROPERTYNAME, "http://localhost:80");
            ProviderWebServiceDataSink providerWebServiceDataSink = new ProviderWebServiceDataSink(providerName, providerProperties);

            DocumentBuilderFactory document2BuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        document2Builder        = document2BuilderFactory.newDocumentBuilder();
            Document               document2               = document2Builder.newDocument();

            Element element2 = document2.createElement("test2");
            element2.appendChild(document2.createTextNode("value2"));
            document2.appendChild(element2);

            logger.fine("****************");
            providerWebServiceDataSink.consume(document2);
            logger.fine("****************");

            Thread.sleep(10000);
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
}
