/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.domdocument.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.arjuna.dbplugins.interconnect.domdocument.dataflownodes.CommonDefs;
import com.arjuna.dbplugins.interconnect.domdocument.dataflownodes.DOMDocumentProviderJunction;

@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@WebServiceProvider(targetNamespace = CommonDefs.INTERCONNECT_NAMESPACE, serviceName = CommonDefs.INTERCONNECT_SERVICENAME_PROVIDER, portName = CommonDefs.INTERCONNECT_PORTNAME_PROVIDER)
public class DOMDocumentProviderProvider implements Provider<SOAPMessage>
{
    private static final Logger logger = Logger.getLogger(DOMDocumentProviderProvider.class.getName());

    public DOMDocumentProviderProvider()
    {
        logger.log(Level.FINE, "DOMDocumentProviderProvider");
    }

    @SuppressWarnings("unchecked")
    @WebMethod
    public SOAPMessage invoke(SOAPMessage request)
    {
        logger.log(Level.FINE, "DOMDocumentProviderProvider.invoke");

        try
        {
            if (logger.isLoggable(Level.FINER))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINER, "DOMDocumentProviderProvider.invoke: request: " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_domDocumentProviderJunction != null)
            {
                String id = null;

                SOAPPart              requestPart     = request.getSOAPPart();
                SOAPEnvelope          requestEnvelope = requestPart.getEnvelope();
                SOAPBody              requestBody     = requestEnvelope.getBody();

                Iterator<SOAPElement> requestElements = (Iterator<SOAPElement>) requestBody.getChildElements();

                while (requestElements.hasNext())
                {
                    SOAPElement requestElement = requestElements.next();

                    if ((requestElement.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_OPERATIONNAME_PROVIDER_OBTAINDATA.equals(requestElement.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestElement.getNamespaceURI()))
                    {
                        Iterator<SOAPElement> requestParameters = (Iterator<SOAPElement>) requestElement.getChildElements();

                        while ((id == null) && requestParameters.hasNext())
                        {
                            SOAPElement requestParameter = requestParameters.next();

                            if ((requestParameter.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_PARAMETERNAME_ID.equals(requestParameter.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestParameter.getNamespaceURI()))
                                id = requestParameter.getTextContent();
                        }
                    }
                }

                MessageFactory responceFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

                SOAPMessage  responce         = responceFactory.createMessage();
                SOAPPart     responcePart     = responce.getSOAPPart();
                SOAPEnvelope responceEnvelope = responcePart.getEnvelope();
                SOAPBody     responceBody     = responceEnvelope.getBody();

                logger.log(Level.FINE, "DOMDocumentProviderProvider.invoke: id = " + id);
                if (id != null)
                {
                    Document document = _domDocumentProviderJunction.withdraw(id);
                    if (document != null)
                        responceBody.addDocument(document);
                }

                if (logger.isLoggable(Level.FINER))
                {
                    ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
                    request.writeTo(responceOutputStream);
                    logger.log(Level.FINER, "DOMDocumentProviderProvider.invoke: responce = " + responceOutputStream.toString());
                    responceOutputStream.close();
                }

                return responce;
            }
        }
        catch (SOAPException soapException)
        {
            logger.log(Level.WARNING, "DOMDocumentProviderProvider ", soapException);
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "DOMDocumentProviderProvider ", ioException);
        }

        return null;
    }

    @EJB
    private DOMDocumentProviderJunction _domDocumentProviderJunction;
}
