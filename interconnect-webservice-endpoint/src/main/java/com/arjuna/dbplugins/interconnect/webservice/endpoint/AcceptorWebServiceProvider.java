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

import com.arjuna.dbplugins.interconnect.webservice.dataflownodes.AcceptorWebServiceDispatcher;

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider
public class AcceptorWebServiceProvider
{
    private static final Logger logger = Logger.getLogger(AcceptorWebServiceProvider.class.getName());

    public AcceptorWebServiceProvider()
    {
        logger.log(Level.INFO, "AcceptorWebServiceProvider");
    }

    @WebMethod
    public SOAPMessage onMessage(SOAPMessage message)
    {
        logger.log(Level.INFO, "AcceptorWebServiceProvider.onMessage");

        try
        {
            if (logger.isLoggable(Level.INFO))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                message.writeTo(requestOutputStream);
                logger.log(Level.FINE, "Message: " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_acceptorWebServiceDispatcher != null)
            {
                if (logger.isLoggable(Level.FINE))
                {
                    ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                    message.writeTo(requestOutputStream);
                    logger.log(Level.FINE, "Request: " + requestOutputStream.toString());
                    requestOutputStream.close();
                }

                String id = (String) _webServiceContext.getMessageContext().get(MessageContext.PATH_INFO);
                logger.log(Level.INFO, "AcceptorWebServiceProvider.onMessage: id = " + id);

                SOAPPart     messagePart     = message.getSOAPPart();
                SOAPEnvelope messageEnvelope = messagePart.getEnvelope();
                SOAPBody     messageBody     = messageEnvelope.getBody();

                Document document = messageBody.extractContentAsDocument();

                if ((id != null) && (document != null))
                    _acceptorWebServiceDispatcher.dispatch(id, document);

                MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

                return messageFactory.createMessage();
            }
        }
        catch (SOAPException soapException)
        {
            logger.log(Level.WARNING, "AcceptorWebServiceProvider ", soapException);
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "AcceptorWebServiceProvider ", ioException);
        }
        
        return null;
    }

    @Resource
    private WebServiceContext _webServiceContext;

    @EJB
    private AcceptorWebServiceDispatcher _acceptorWebServiceDispatcher;
}
