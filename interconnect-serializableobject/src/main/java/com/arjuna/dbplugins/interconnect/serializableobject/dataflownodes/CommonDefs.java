/*
 * Copyright (c) 2014-2015, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.interconnect.serializableobject.dataflownodes;

public class CommonDefs
{
    public static final String INTERCONNECT_NAMESPACE                                    = "http://databroker.arjuna.com/interconnect/";

    public static final String INTERCONNECT_SERVICE_PATH                                 = "serializableobject_interconnect";

    public static final String INTERCONNECT_SERVICENAME_ACCEPTOR                         = "AcceptorService";
    public static final String INTERCONNECT_PORTNAME_ACCEPTOR                            = "AcceptorPort";
    public static final String INTERCONNECT_OPERATIONNAME_ACCEPTOR_ACCEPTDATA            = "AcceptData";
    public static final String INTERCONNECT_ACCEPTOR_ACCEPTDATA_REQUEST                  = "AcceptDataRequest";
    public static final String INTERCONNECT_ACCEPTDATA_PARAMETERNAME_ID                  = "id";
    public static final String INTERCONNECT_ACCEPTDATA_PARAMETERNAME_SERIALIALIZEDOBJECT = "serialializedobject";
    public static final String INTERCONNECT_ACCEPTOR_ACCEPTDATA_RESPONCE                 = "AcceptDataResponce";

    public static final String INTERCONNECT_SERVICENAME_PROVIDER                         = "ProviderService";
    public static final String INTERCONNECT_PORTNAME_PROVIDER                            = "ProviderPort";
    public static final String INTERCONNECT_OPERATIONNAME_PROVIDER_PROVIDEDATA           = "ProvideData";
    public static final String INTERCONNECT_PROVIDER_PROVIDEDATA_REQUEST                 = "ProvideDataRequest";
    public static final String INTERCONNECT_PROVIDEDATA_PARAMETERNAME_ID                 = "id";
    public static final String INTERCONNECT_PROVIDER_PROVIDEDATA_RESPONCE                = "ProvideDataResponce";
    public static final String INTERCONNECT_PROVIDEDATA_RESULTNAME_SERIALIALIZEDOBJECT   = "serialializedobject";
}