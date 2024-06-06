/*
 *  Copyright (c) Telicent Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.telicent.jena.abac.evalserver;

import io.telicent.jena.abac.core.Attributes;
import io.telicent.jena.abac.core.AttributesStore;
import io.telicent.jena.abac.core.AttributesStoreRemote;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.logging.FmtLog;
import org.apache.jena.cmd.ArgDecl;
import org.apache.jena.cmd.CmdException;
import org.apache.jena.cmd.CmdGeneral;
import org.apache.jena.fuseki.system.FusekiLogging;
import org.apache.jena.graph.Graph;
import org.apache.jena.irix.IRIException;
import org.apache.jena.irix.IRIs;
import org.apache.jena.irix.IRIx;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.sys.JenaSystem;
import org.slf4j.Logger;

public class MainAttrEvalServer extends CmdGeneral {

    private static final Logger LOG = AttributeEvalServer.LOG;

    static { JenaSystem.init(); }

    public static void main(String...args) {
        new MainAttrEvalServer(args).mainRun() ;
    }

    private static AttributesStore createRemoteAttributeStore(String lookupUserEndpoint, String lookupHierarchyEndpoint) {
        AttributesStore attrStoreRemote = new AttributesStoreRemote(lookupUserEndpoint, lookupHierarchyEndpoint);
        return attrStoreRemote;
    }

    private static AttributesStore createLocalAttributeStore(String localAttributeStoreFile) {
        Graph g = RDFParser.source(localAttributeStoreFile).toGraph();
        AttributesStore attrStoreLocal = Attributes.buildStore(g);
        return attrStoreLocal;
    }

    private static final ArgDecl argPort      = new ArgDecl(ArgDecl.HasValue, "port");
    private static final ArgDecl argConfig    = new ArgDecl(ArgDecl.HasValue, "config", "conf");
    private static final ArgDecl argStoreURL  = new ArgDecl(ArgDecl.HasValue, "attrStore", "attrstore", "store");

    private String configFile       = null;
    private String storeURL         = null;
    private int port                = 0;
    private static final int defaultPort  = 4045;

    protected MainAttrEvalServer(String[] argv) {
        super(argv);
        add(argConfig,   "--config",      "Configuration file");
        add(argStoreURL, "--attrstore",   "File name for local attribute store or URL for remote store");
        add(argPort,     "--port",        "Listen on this port number");

        // --config conf.json.
        // OR
        // --attrstore [file:]|https://
    }

    @Override
    protected String getSummary() {
        return null;
    }

    @Override
    protected String getCommandName() {
        return null;
    }

    private String localAttributeStore = null;
    private String lookupUserEndpoint = "";
    private String lookupHierarchyEndpoint = "";

    @Override
    protected void processModulesAndArgs() {
        super.processModulesAndArgs();
        configFile = super.getValue(argConfig);
        storeURL = super.getValue(argStoreURL);

        port = portNumber(argPort);
        if ( port <=0 )
            port = defaultPort;

        if ( configFile == null && storeURL == null)
            throw new CmdException("Required: one of --attrstore and --config");
        if ( configFile != null && storeURL != null)
            throw new CmdException("Required: one of --attrstore and --config");

        if ( storeURL != null ) {
            try {
                IRIx iri = IRIx.create(storeURL);
                if ( ! iri.isAbsolute() )
                    throw new CmdException("Bad URI for attribute store: "+storeURL);
                String scheme = IRIs.scheme(storeURL);
                if ( scheme == null || "file".equalsIgnoreCase(scheme) ) {
                    localAttributeStore = storeURL;
                    lookupUserEndpoint = null;
                    lookupHierarchyEndpoint = null;
                } else {
                    localAttributeStore = null;
                    lookupUserEndpoint = storeURL;
                    lookupHierarchyEndpoint = storeURL;
                }
            } catch (IRIException ex) {
                throw new CmdException("Bad syntax in URI for attribute store: "+storeURL);
            }
        }
        if ( configFile != null  ) {
            JsonObject jObject = JSON.read(configFile);
            String storeURI = jObject.get("userAttrStore").getAsString().value();
            String hierURI = jObject.get("hierarchyService").getAsString().value();
            if ( storeURI == null ) {}
            if ( hierURI == null ) {}
        }
    }

    @Override
    protected void exec() {
        AttributesStore attrStore =
                (localAttributeStore != null)
                ? createLocalAttributeStore(localAttributeStore)
                : createRemoteAttributeStore(lookupUserEndpoint, lookupHierarchyEndpoint) ;
        FusekiLogging.setLogging();
        FmtLog.info(LOG, "Attribute evaluation server : port = %s", port);
        String URL = AttributeEvalServer.run(port, "/eval", attrStore);
    }

    private int portNumber(ArgDecl arg) {
        String portStr = getValue(arg);
        if ( null == portStr || portStr.isEmpty() )
            return -1;
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException ex) {
            throw new CmdException(argPort.getKeyName() + " : bad port number: '" + portStr+"'");
        }
    }
}
