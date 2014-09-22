/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes;

public class CommonDefs
{
    public static final String INTERCONNECT_NAMESPACE                                     = "http://databroker.arjuna.com/interconnect/";

    public static final String INTERCONNECT_SERVICE_PATH                                  = "serializableobject_interconnect";

    public static final String INTERCONNECT_SERVICENAME_ACCEPTOR                          = "AcceptorService";
    public static final String INTERCONNECT_PORTNAME_ACCEPTOR                             = "AcceptorPort";
    public static final String INTERCONNECT_OPERATIONNAME_ACCEPTOR_RECEIVEDATA            = "ReceiveData";

    public static final String INTERCONNECT_RECEIVEDATA_PARAMETERNAME_ID                  = "id";
    public static final String INTERCONNECT_RECEIVEDATA_PARAMETERNAME_SERIALIALIZEDOBJECT = "serialializedobject";

    public static final String INTERCONNECT_SERVICENAME_PROVIDER                          = "ProviderService";
    public static final String INTERCONNECT_PORTNAME_PROVIDER                             = "ProviderPort";
    public static final String INTERCONNECT_OPERATIONNAME_PROVIDER_OBTAINDATA             = "ObtainData";

    public static final String INTERCONNECT_OBTAINDATA_PARAMETERNAME_ID                   = "id";
    public static final String INTERCONNECT_OBTAINDATA_PARAMETERNAME_SERIALIALIZEDOBJECT  = "serialializedobject";
}