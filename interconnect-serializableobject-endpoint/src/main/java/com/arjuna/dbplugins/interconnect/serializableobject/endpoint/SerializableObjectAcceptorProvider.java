/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.endpoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
import org.apache.commons.codec.binary.Hex;
import org.w3c.dom.Node;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.CommonDefs;
import com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes.SerializableObjectAcceptorDispatcher;

@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@WebServiceProvider(targetNamespace = CommonDefs.INTERCONNECT_NAMESPACE, serviceName = CommonDefs.INTERCONNECT_SERVICENAME_ACCEPTOR, portName = CommonDefs.INTERCONNECT_PORTNAME_ACCEPTOR)
public class SerializableObjectAcceptorProvider implements Provider<SOAPMessage>
{
    private static final Logger logger = Logger.getLogger(SerializableObjectAcceptorProvider.class.getName());

    public SerializableObjectAcceptorProvider()
    {
        logger.log(Level.FINE, "SerializableObjectAcceptorProvider");
    }

    public SOAPMessage invoke(SOAPMessage request)
    {
        logger.log(Level.FINE, "SerializableObjectAcceptorProvider.invoke");

        try
        {
            if (logger.isLoggable(Level.FINE))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINE, "SerializableObjectAcceptorProvider.invoke: request = " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_serializableObjectAcceptorDispatcher != null)
            {
                SOAPPart     requestPart     = request.getSOAPPart();
                SOAPEnvelope requestEnvelope = requestPart.getEnvelope();
                SOAPBody     requestBody     = requestEnvelope.getBody();

                Iterator<SOAPElement> requestBodyElements = (Iterator<SOAPElement>) requestBody.getChildElements();

                String       id                 = null;
                Serializable serializableObject = null;
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
                            else if ((requestElement.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_SERIALIALIZEDOBJECT.equals(requestElement.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestElement.getNamespaceURI()))
                            {
                                if (serializableObject == null)
                                {
                                    try
                                    {
                                        byte[]               objectBytes                = Hex.decodeHex(requestElement.getTextContent().toCharArray());
                                        ByteArrayInputStream objectByteArrayInputStream = new ByteArrayInputStream(objectBytes);
                                        ObjectInputStream    objectObjectInputStream    = new ObjectInputStream(objectByteArrayInputStream);
                                        serializableObject = (Serializable) objectObjectInputStream.readObject();
                                        objectByteArrayInputStream.close();
                                    }
                                    catch (Throwable throwable)
                                    {
                                        logger.log(Level.WARNING, "invoke: unable to deserialize 'serializableObject'", throwable);
                                    }
                                }
                                else
                                    logger.log(Level.WARNING, "invoke: 'serializableObject' has already defined");
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

                if ((id != null) && (serializableObject != null))
                    _serializableObjectAcceptorDispatcher.dispatch(id, serializableObject);

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
                    logger.log(Level.FINE, "SerializableObjectAcceptorProvider.invoke: responce = " + responceOutputStream.toString());
                    responceOutputStream.close();
                }

                return responce;
            }
        }
        catch (SOAPException soapException)
        {
            logger.log(Level.WARNING, "SerializableObjectAcceptorProvider ", soapException);
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "SerializableObjectAcceptorProvider ", ioException);
        }

        return null;
    }

    @EJB
    private SerializableObjectAcceptorDispatcher _serializableObjectAcceptorDispatcher;
}
