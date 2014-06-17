/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.webservice.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Document;

import com.arjuna.dbplugins.interconnect.webservice.dataflownodes.ProviderWebServiceJunction;

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider
public class ProviderWebServiceProvider
{
    private static final Logger logger = Logger.getLogger(ProviderWebServiceProvider.class.getName());

    public ProviderWebServiceProvider()
    {
        logger.log(Level.INFO, "WebServiceEndpointProvider");
    }

    @WebMethod
    public SOAPMessage onMessage(SOAPMessage message)
    {
        logger.log(Level.INFO, "WebServiceEndpointProvider.onMessage");

        try
        {
            if (logger.isLoggable(Level.INFO))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                message.writeTo(requestOutputStream);
                logger.log(Level.FINE, "Message: " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_providerWebServiceJunction != null)
            {
                if (logger.isLoggable(Level.FINE))
                {
                    ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                    message.writeTo(requestOutputStream);
                    logger.log(Level.FINE, "Request: " + requestOutputStream.toString());
                    requestOutputStream.close();
                }

                String id = (String) _webServiceContext.getMessageContext().get(MessageContext.PATH_INFO);
                logger.log(Level.INFO, "WebServiceEndpointProvider.onMessage: id = " + id);

                if (id != null)
                {
                    Document document = _providerWebServiceJunction.withdraw(id);

                    MessageFactory messageFactory  = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

                    SOAPMessage  responce        = messageFactory.createMessage();
                    SOAPPart     messagePart     = message.getSOAPPart();
                    SOAPEnvelope messageEnvelope = messagePart.getEnvelope();
                    SOAPBody     messageBody     = messageEnvelope.getBody();
                    messageBody.addDocument(document);

                    return responce;
                }
                else
                    return null;
            }
        }
        catch (SOAPException soapException)
        {
            logger.log(Level.WARNING, "WebServiceEndpointProvider ", soapException);
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "WebServiceEndpointProvider ", ioException);
        }
        
        return null;
    }

    @Resource
    private WebServiceContext _webServiceContext;

    @EJB
    private ProviderWebServiceJunction _providerWebServiceJunction;
}
