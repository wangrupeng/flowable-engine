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
package org.flowable.bpmn.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.child.BaseChildElementParser;
import org.flowable.bpmn.converter.child.InParameterParser;
import org.flowable.bpmn.converter.child.ScriptTextParser;
import org.flowable.bpmn.converter.util.BpmnXMLUtil;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.ScriptTask;

/**
 * @author Tijs Rademakers
 */
public class ScriptTaskXMLConverter extends BaseBpmnXMLConverter {

    protected static final List<ExtensionAttribute> defaultScriptTaskAttributes = Arrays.asList(
            new ExtensionAttribute(ATTRIBUTE_TASK_SCRIPT_FORMAT),
            new ExtensionAttribute(ATTRIBUTE_TASK_SCRIPT_RESULTVARIABLE),
            new ExtensionAttribute(ATTRIBUTE_TASK_SERVICE_RESULT_VARIABLE_NAME),
            new ExtensionAttribute(ATTRIBUTE_TASK_SCRIPT_SKIP_EXPRESSION),
            new ExtensionAttribute(ATTRIBUTE_TASK_SCRIPT_AUTO_STORE_VARIABLE),
            new ExtensionAttribute(ATTRIBUTE_TASK_SCRIPT_DO_NOT_INCLUDE_VARIABLES)
    );

    protected Map<String, BaseChildElementParser> childParserMap = new HashMap<>();

    public ScriptTaskXMLConverter() {
        ScriptTextParser scriptTextParser = new ScriptTextParser();
        childParserMap.put(scriptTextParser.getElementName(), scriptTextParser);
        InParameterParser inParameterParser = new InParameterParser();
        childParserMap.put(inParameterParser.getElementName(), inParameterParser);
    }

    @Override
    public Class<? extends BaseElement> getBpmnElementType() {
        return ScriptTask.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_TASK_SCRIPT;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
        ScriptTask scriptTask = new ScriptTask();
        BpmnXMLUtil.addXMLLocation(scriptTask, xtr);
        scriptTask.setScriptFormat(xtr.getAttributeValue(null, ATTRIBUTE_TASK_SCRIPT_FORMAT));
        scriptTask.setResultVariable(BpmnXMLUtil.getAttributeValue(ATTRIBUTE_TASK_SCRIPT_RESULTVARIABLE, xtr));
        if (StringUtils.isEmpty(scriptTask.getResultVariable())) {
            scriptTask.setResultVariable(BpmnXMLUtil.getAttributeValue(ATTRIBUTE_TASK_SERVICE_RESULT_VARIABLE_NAME, xtr));
        }
        String skipExpression = BpmnXMLUtil.getAttributeValue(ATTRIBUTE_TASK_SCRIPT_SKIP_EXPRESSION, xtr);
        if (StringUtils.isNotEmpty(skipExpression)) {
            scriptTask.setSkipExpression(skipExpression);
        }
        String autoStoreVariables = BpmnXMLUtil.getAttributeValue(ATTRIBUTE_TASK_SCRIPT_AUTO_STORE_VARIABLE, xtr);
        if (StringUtils.isNotEmpty(autoStoreVariables)) {
            scriptTask.setAutoStoreVariables(Boolean.valueOf(autoStoreVariables));
        }

        scriptTask.setDoNotIncludeVariables(Boolean.parseBoolean(BpmnXMLUtil.getAttributeValue(ATTRIBUTE_TASK_SCRIPT_DO_NOT_INCLUDE_VARIABLES, xtr)));

        BpmnXMLUtil.addCustomAttributes(xtr, scriptTask, defaultElementAttributes, defaultActivityAttributes, defaultScriptTaskAttributes);

        parseChildElements(getXMLElementName(), scriptTask, childParserMap, model, xtr);

        return scriptTask;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        ScriptTask scriptTask = (ScriptTask) element;
        writeDefaultAttribute(ATTRIBUTE_TASK_SCRIPT_FORMAT, scriptTask.getScriptFormat(), xtw);
        writeQualifiedAttribute(ATTRIBUTE_TASK_SCRIPT_RESULTVARIABLE, scriptTask.getResultVariable(), xtw);
        writeQualifiedAttribute(ATTRIBUTE_TASK_SCRIPT_SKIP_EXPRESSION, scriptTask.getSkipExpression(), xtw);
        writeQualifiedAttribute(ATTRIBUTE_TASK_SCRIPT_AUTO_STORE_VARIABLE, String.valueOf(scriptTask.isAutoStoreVariables()), xtw);
        if (scriptTask.isDoNotIncludeVariables()) {
            writeQualifiedAttribute(ATTRIBUTE_TASK_SCRIPT_DO_NOT_INCLUDE_VARIABLES, "true", xtw);
        }

        BpmnXMLUtil.writeCustomAttributes(scriptTask.getAttributes().values(), xtw, defaultElementAttributes,
                defaultActivityAttributes, defaultScriptTaskAttributes);
    }

    @Override
    protected boolean writeExtensionChildElements(BaseElement element, boolean didWriteExtensionStartElement, XMLStreamWriter xtw) throws Exception {
        ScriptTask scriptTask = (ScriptTask) element;
        didWriteExtensionStartElement = super.writeExtensionChildElements(element, didWriteExtensionStartElement, xtw);
        didWriteExtensionStartElement = BpmnXMLUtil.writeIOParameters(ELEMENT_IN_PARAMETERS,
                scriptTask.getInParameters(), didWriteExtensionStartElement, xtw);
        return didWriteExtensionStartElement;
    }

    @Override
    protected void writeAdditionalChildElements(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        ScriptTask scriptTask = (ScriptTask) element;
        if (StringUtils.isNotEmpty(scriptTask.getScript())) {
            xtw.writeStartElement(ATTRIBUTE_TASK_SCRIPT_TEXT);
            xtw.writeCData(scriptTask.getScript());
            xtw.writeEndElement();
        }
    }
}
