/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.impl.webservice;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.flowable.bpmn.model.Import;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.util.ReflectUtil;
import org.flowable.engine.impl.bpmn.data.SimpleStructureDefinition;
import org.flowable.engine.impl.bpmn.data.StructureDefinition;
import org.flowable.engine.impl.bpmn.parser.XMLImporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.ConsoleErrorReporter;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

/**
 * A WSDL importer
 * 
 * @author Esteban Robles Luna
 */
public class WSDLImporter implements XMLImporter {

    protected Map<String, WSService> wsServices = new HashMap<>();

    protected Map<String, WSOperation> wsOperations = new HashMap<>();

    protected Map<String, StructureDefinition> structures = new HashMap<>();

    protected String wsdlLocation;

    protected String namespace;

    public WSDLImporter() {
        this.namespace = "";
    }

    @Override
    public void importFrom(Import theImport, String sourceSystemId) {
        this.namespace = theImport.getNamespace() == null ? "" : theImport.getNamespace() + ":";
        this.importFrom(theImport.getLocation());
    }

    public void importFrom(String url) {
        this.wsServices.clear();
        this.wsOperations.clear();
        this.structures.clear();

        this.wsdlLocation = url;

        try {
            Definition definition = this.parseWSDLDefinition();
            this.importServicesAndOperations(definition);
            this.importTypes(definition.getTypes());
        } catch (WSDLException e) {
            throw new FlowableException(e.getMessage(), e);
        }
    }

    /**
     * Parse the WSDL definition using WSDL4J.
     */
    protected Definition parseWSDLDefinition() throws WSDLException {
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader reader = wsdlFactory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition definition = reader.readWSDL(this.wsdlLocation);
        return definition;
    }

    /**
     * Imports services and operations from the WSDL definition
     */
    protected void importServicesAndOperations(Definition definition) {
        for (Object serviceObject : definition.getServices().values()) {
            Service service = (Service) serviceObject;
            WSService wsService = this.importService(service);
            this.wsServices.put(this.namespace + wsService.getName(), wsService);

            Port port = (Port) service.getPorts().values().iterator().next();
            for (Object bindOperationObject : port.getBinding().getBindingOperations()) {
                BindingOperation bindOperation = (BindingOperation) bindOperationObject;
                WSOperation operation = this.processOperation(bindOperation.getOperation(), wsService);
                wsService.addOperation(operation);

                this.wsOperations.put(this.namespace + operation.getName(), operation);
            }
        }
    }

    /**
     * Imports the service from the WSDL service definition
     */
    protected WSService importService(Service service) {
        String name = service.getQName().getLocalPart();
        Port port = (Port) service.getPorts().values().iterator().next();
        String location = "";

        List extensionElements = port.getExtensibilityElements();
        for (Object extension : extensionElements) {
            if (extension instanceof SOAPAddress) {
                SOAPAddress address = (SOAPAddress) extension;
                location = address.getLocationURI();
            }
        }

        WSService wsService = new WSService(this.namespace + name, location, this.wsdlLocation);
        return wsService;
    }

    protected WSOperation processOperation(Operation wsOperation, WSService service) {
        WSOperation operation = new WSOperation(this.namespace + wsOperation.getName(), wsOperation.getName(), service);
        return operation;
    }

    /**
     * Import the Types from the WSDL definition using the same strategy that Cxf uses taking advantage of JAXB
     */
    protected void importTypes(Types types) {
        SchemaCompiler compiler = XJC.createSchemaCompiler();
        ErrorListener elForRun = new ConsoleErrorReporter();
        compiler.setErrorListener(elForRun);

        Element rootTypes = this.getRootTypes();
        this.createDefaultStructures(rootTypes);

        S2JJAXBModel intermediateModel = this.compileModel(types, compiler, rootTypes);
        Collection<? extends Mapping> mappings = intermediateModel.getMappings();

        for (Mapping mapping : mappings) {
            this.importStructure(mapping);
        }
    }

    protected void importStructure(Mapping mapping) {
        QName qname = mapping.getElement();
        JDefinedClass theClass = (JDefinedClass) mapping.getType().getTypeClass();
        SimpleStructureDefinition structure = (SimpleStructureDefinition) this.structures.get(this.namespace + qname.getLocalPart());

        Map<String, JFieldVar> fields = theClass.fields();
        int index = 0;
        for (Entry<String, JFieldVar> entry : fields.entrySet()) {

            final JType fieldType = entry.getValue().type();
            final Class<?> fieldClass = ReflectUtil.loadClass(fieldType.boxify().fullName());
            final Class<?> fieldParameterClass;
            if (fieldType instanceof JClass) {
                final JClass fieldClassType = (JClass) fieldType;
                final List<JClass> fieldTypeParameters = fieldClassType.getTypeParameters();
                if (fieldTypeParameters.size() > 1) {
                    throw new FlowableException(
                            String.format("Field type '%s' with more than one parameter is not supported: %S",
                                    fieldClassType, fieldTypeParameters));
                } else if (fieldTypeParameters.isEmpty()) {
                    fieldParameterClass = null;
                } else {
                    final JClass fieldParameterType = fieldTypeParameters.get(0);

                    // Hack because JClass.fullname() doesn't return the right class fullname for a nested class to be
                    // loaded from classloader. It should be contain "$" instead of "." as separator
                    boolean isFieldParameterTypeNeestedClass = false;
                    final Iterator<JDefinedClass> theClassNeestedClassIt = theClass.classes();
                    while (theClassNeestedClassIt.hasNext() && !isFieldParameterTypeNeestedClass) {
                        final JDefinedClass neestedType = theClassNeestedClassIt.next();
                        if (neestedType.name().equals(fieldParameterType.name())) {
                            isFieldParameterTypeNeestedClass = true;
                        }
                    }
                    if (isFieldParameterTypeNeestedClass) {
                        // The parameter type is a nested class
                        fieldParameterClass = ReflectUtil
                                .loadClass(theClass.erasure().fullName() + "$" + fieldParameterType.name());
                    } else {
                        // The parameter type is not a nested class
                        fieldParameterClass = ReflectUtil.loadClass(fieldParameterType.erasure().fullName());
                    }
                }
            } else {
                fieldParameterClass = null;
            }

            structure.setFieldName(index, entry.getKey(), fieldClass, fieldParameterClass);
            index++;
        }
    }

    protected S2JJAXBModel compileModel(Types types, SchemaCompiler compiler, Element rootTypes) {
        Schema schema = (Schema) types.getExtensibilityElements().get(0);
        compiler.parseSchema(schema.getDocumentBaseURI() + "#types1", rootTypes);
        S2JJAXBModel intermediateModel = compiler.bind();
        return intermediateModel;
    }

    protected void createDefaultStructures(Element rootTypes) {
        NodeList complexTypes = rootTypes.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "complexType");
        for (int i = 0; i < complexTypes.getLength(); i++) {
            Element element = (Element) complexTypes.item(i);
            String structureName = this.namespace + element.getAttribute("name");
            SimpleStructureDefinition structure = new SimpleStructureDefinition(structureName);
            this.structures.put(structure.getId(), structure);
        }
    }

    protected Element getRootTypes() {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.parse(this.wsdlLocation);
            Element root = (Element) doc.getFirstChild();
            Element typesElement = (Element) root.getElementsByTagName("wsdl:types").item(0);
            return (Element) typesElement.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema").item(0);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new FlowableException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, StructureDefinition> getStructures() {
        return this.structures;
    }

    @Override
    public Map<String, WSService> getServices() {
        return this.wsServices;
    }

    @Override
    public Map<String, WSOperation> getOperations() {
        return this.wsOperations;
    }
}
