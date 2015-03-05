/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.binary.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.xml.namespace.QName;
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
import org.w3c.dom.Node;
import com.arjuna.dbplugins.interconnect.binary.dataflownodes.CommonDefs;
import com.arjuna.dbplugins.interconnect.binary.dataflownodes.BinaryAcceptorDispatcher;

@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@WebServiceProvider(targetNamespace = CommonDefs.INTERCONNECT_NAMESPACE, serviceName = CommonDefs.INTERCONNECT_SERVICENAME_ACCEPTOR, portName = CommonDefs.INTERCONNECT_PORTNAME_ACCEPTOR)
public class BinaryAcceptorProvider implements Provider<SOAPMessage>
{
    private static final Logger logger = Logger.getLogger(BinaryAcceptorProvider.class.getName());

    public BinaryAcceptorProvider()
    {
        logger.log(Level.FINE, "BinaryAcceptorProvider");
    }

    public SOAPMessage invoke(SOAPMessage request)
    {
        logger.log(Level.FINE, "BinaryAcceptorProvider.invoke");

        try
        {
            if (logger.isLoggable(Level.FINE))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINE, "BinaryAcceptorProvider.invoke: request = " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_binaryAcceptorDispatcher != null)
            {
                SOAPPart     requestPart     = request.getSOAPPart();
                SOAPEnvelope requestEnvelope = requestPart.getEnvelope();
                SOAPBody     requestBody     = requestEnvelope.getBody();

                Iterator<SOAPElement> requestBodyElements = (Iterator<SOAPElement>) requestBody.getChildElements();

                String id     = null;
                String binary = null;
                while (requestBodyElements.hasNext())
                {
                    SOAPElement requestBodyElement = requestBodyElements.next();

                    if ((requestBodyElement.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_ACCEPTOR_ACCEPTDATA_REQUEST.equals(requestBodyElement.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestBodyElement.getNamespaceURI()))
                    {
                        Iterator<SOAPElement> requestElements = (Iterator<SOAPElement>) requestBodyElement.getChildElements();

                        while (requestElements.hasNext())
                        {
                            SOAPElement requestElement = requestElements.next();

                            if ((requestElement.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_ID.equals(requestElement.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestElement.getNamespaceURI()))
                            {
                                if (id == null)
                                    id = requestElement.getTextContent();
                                else
                                    logger.log(Level.WARNING, "invoke: 'id' has already defined" + id);
                            }
                            else if ((requestElement.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_BINARY.equals(requestElement.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestElement.getNamespaceURI()))
                            {
                                if (binary == null)
                                    binary = requestElement.getTextContent();
                                else
                                    logger.log(Level.WARNING, "invoke: 'binary' has already defined");
                            }
                            else if (requestElement.getNodeType() == Node.TEXT_NODE)
                            {
                                if (! "".equals(requestElement.getNodeValue().trim()))
                                    logger.log(Level.WARNING, "invoke: unexperted text [" + requestElement.getNodeValue() + "]");
                            }
                            else if (requestElement.getNodeType() != Node.COMMENT_NODE)
                                logger.log(Level.WARNING, "invoke: unexperted parameter " + requestElement.getNodeName());
                        }
                    }
                    else
                        logger.log(Level.WARNING, "invoke: unexperted request " + requestBodyElement.getNodeName());
                }

                if ((id != null) && (binary != null))
                    _binaryAcceptorDispatcher.dispatch(id, binary);

                MessageFactory responceFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
                SOAPMessage    responce        = responceFactory.createMessage();
                SOAPBody       responceBody    = responce.getSOAPBody();
                responceBody.addNamespaceDeclaration("ic", CommonDefs.INTERCONNECT_NAMESPACE);

                QName           responceBodyQName   = responceBody.createQName(CommonDefs.INTERCONNECT_ACCEPTOR_ACCEPTDATA_RESPONCE, "ic");
                responceBody.addBodyElement(responceBodyQName);

                if (logger.isLoggable(Level.FINE))
                {
                    ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
                    responce.writeTo(responceOutputStream);
                    logger.log(Level.FINE, "BinaryAcceptorProvider.invoke: responce = " + responceOutputStream.toString());
                    responceOutputStream.close();
                }

                return responce;
            }
        }
        catch (SOAPException soapException)
        {
            logger.log(Level.WARNING, "BinaryAcceptorProvider ", soapException);
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "BinaryAcceptorProvider ", ioException);
        }

        return null;
    }

    @EJB
    private BinaryAcceptorDispatcher _binaryAcceptorDispatcher;
}
