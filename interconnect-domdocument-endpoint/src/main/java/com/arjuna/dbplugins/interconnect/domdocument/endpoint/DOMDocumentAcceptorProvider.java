/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.domdocument.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.arjuna.dbplugins.interconnect.domdocument.dataflownodes.DOMDocumentAcceptorDispatcher;
import com.arjuna.dbplugins.interconnect.domdocument.dataflownodes.CommonDefs;

@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@WebServiceProvider(targetNamespace = CommonDefs.INTERCONNECT_NAMESPACE, serviceName = CommonDefs.INTERCONNECT_SERVICENAME_ACCEPTOR, portName = CommonDefs.INTERCONNECT_PORTNAME_ACCEPTOR)
public class DOMDocumentAcceptorProvider implements Provider<SOAPMessage>
{
    private static final Logger logger = Logger.getLogger(DOMDocumentAcceptorProvider.class.getName());

    public DOMDocumentAcceptorProvider()
    {
        logger.log(Level.FINE, "DOMDocumentAcceptorProvider");
    }

    public SOAPMessage invoke(SOAPMessage request)
    {
        logger.log(Level.FINE, "DOMDocumentAcceptorProvider.invoke");

        try
        {
            if (logger.isLoggable(Level.FINER))
            {
                ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
                request.writeTo(requestOutputStream);
                logger.log(Level.FINER, "DOMDocumentAcceptorProvider.invoke: request = " + requestOutputStream.toString());
                requestOutputStream.close();
            }

            if (_domDocumentAcceptorDispatcher != null)
            {
                SOAPPart     requestPart     = request.getSOAPPart();
                SOAPEnvelope requestEnvelope = requestPart.getEnvelope();
                SOAPBody     requestBody     = requestEnvelope.getBody();
                Document     requestDocument = requestBody.extractContentAsDocument();
                Element      requestElement  = requestDocument.getDocumentElement();
                NodeList     requestNodeList = requestElement.getChildNodes();

                String id = null;
                for (int requestNodeIndex = 0; requestNodeIndex < requestNodeList.getLength(); requestNodeIndex++)
                {
                    Node requestNode = requestNodeList.item(requestNodeIndex);
                    if ((requestNode.getNodeType() == Node.ELEMENT_NODE) && CommonDefs.INTERCONNECT_PARAMETERNAME_ID.equals(requestNode.getNodeName()) && CommonDefs.INTERCONNECT_NAMESPACE.equals(requestNode.getNamespaceURI()))
                    {
                        if (id == null)
                            id = requestNode.getTextContent();
                        requestElement.removeChild(requestNode);
                    }
                }

                logger.log(Level.FINE, "DOMDocumentAcceptorProvider.invoke: id = " + id);
                if ((id != null) && (requestDocument != null))
                    _domDocumentAcceptorDispatcher.dispatch(id, requestDocument);

                MessageFactory responceFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
                SOAPMessage    responce        = responceFactory.createMessage();

                if (logger.isLoggable(Level.FINER))
                {
                    ByteArrayOutputStream responceOutputStream = new ByteArrayOutputStream();
                    responce.writeTo(responceOutputStream);
                    logger.log(Level.FINER, "DOMDocumentAcceptorProvider.invoke: responce = " + responceOutputStream.toString());
                    responceOutputStream.close();
                }

                return responce;
            }
        }
        catch (SOAPException soapException)
        {
            logger.log(Level.WARNING, "DOMDocumentAcceptorProvider ", soapException);
        }
        catch (IOException ioException)
        {
            logger.log(Level.WARNING, "DOMDocumentAcceptorProvider ", ioException);
        }

        return null;
    }

    @EJB
    private DOMDocumentAcceptorDispatcher _domDocumentAcceptorDispatcher;
}
