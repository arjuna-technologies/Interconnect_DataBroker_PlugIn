/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.endpoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
            if (logger.isLoggable(Level.FINER))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINER, "SerializableObjectAcceptorProvider.invoke: request = " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_serializableObjectAcceptorDispatcher != null)
            {
                SOAPPart     requestPart     = request.getSOAPPart();
                SOAPEnvelope requestEnvelope = requestPart.getEnvelope();
                SOAPBody     requestBody     = requestEnvelope.getBody();
                Document     requestDocument = requestBody.extractContentAsDocument();
                Element      requestElement  = requestDocument.getDocumentElement();
                NodeList     requestNodeList = requestElement.getChildNodes();

                String       id                 = null;
                Serializable serializableObject = null;
                for (int requestNodeIndex = 0; requestNodeIndex < requestNodeList.getLength(); requestNodeIndex++)
                {
                    Node requestNode = requestNodeList.item(requestNodeIndex);

                    if ((requestNode.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_ID.equals(requestNode.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestNode.getNamespaceURI()))
                    {
                        if (id == null)
                            id = requestNode.getTextContent();
                        else
                            logger.log(Level.WARNING, "invoke: 'id' has already defined" + id);
                    }
                    else if ((requestNode.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_ACCEPTDATA_PARAMETERNAME_SERIALIALIZEDOBJECT.equals(requestNode.getLocalName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestNode.getNamespaceURI()))
                    {
                        if (serializableObject == null)
                        {
                            try
                            {
                                byte[]               objectBytes                = Hex.decodeHex(requestNode.getTextContent().toCharArray());
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
                    else if (requestNode.getNodeType() == Node.TEXT_NODE)
                    {
                        if (! "".equals(requestNode.getNodeValue().trim()))
                            logger.log(Level.WARNING, "invoke: unexperted text [" + requestNode.getNodeValue() + "]");
                    }
                    else if (requestNode.getNodeType() != Node.COMMENT_NODE)
                        logger.log(Level.WARNING, "invoke: unexperted parameter " + requestNode.getNodeName());
                }

                logger.log(Level.FINE, "SerializableObjectAcceptorProvider.invoke: id = " + id + ", serializableObject = " + serializableObject);
                if ((id != null) && (serializableObject != null))
                    _serializableObjectAcceptorDispatcher.dispatch(id, serializableObject);

                MessageFactory responceFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
                SOAPMessage    responce        = responceFactory.createMessage();

                if (logger.isLoggable(Level.FINER))
                {
                    ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
                    responce.writeTo(responceOutputStream);
                    logger.log(Level.FINER, "SerializableObjectAcceptorProvider.invoke: responce = " + responceOutputStream.toString());
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
