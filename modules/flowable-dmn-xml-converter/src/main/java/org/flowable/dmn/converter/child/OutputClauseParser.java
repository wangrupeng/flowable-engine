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
package org.flowable.dmn.converter.child;

import javax.xml.stream.XMLStreamReader;

import org.flowable.dmn.model.Decision;
import org.flowable.dmn.model.DecisionTable;
import org.flowable.dmn.model.DmnElement;
import org.flowable.dmn.model.OutputClause;

/**
 * @author Tijs Rademakers
 * @author Yvo Swillens
 */
public class OutputClauseParser extends BaseChildElementParser {

    @Override
    public String getElementName() {
        return ELEMENT_OUTPUT_CLAUSE;
    }

    @Override
    public void parseChildElement(XMLStreamReader xtr, DmnElement parentElement, Decision decision) throws Exception {
        if (!(parentElement instanceof DecisionTable)) {
            return;
        }

        OutputClause output = new OutputClause();
        output.setId(xtr.getAttributeValue(null, ATTRIBUTE_ID));
        output.setLabel(xtr.getAttributeValue(null, ATTRIBUTE_LABEL));
        output.setName(xtr.getAttributeValue(null, ATTRIBUTE_NAME));
        output.setTypeRef(xtr.getAttributeValue(null, ATTRIBUTE_TYPE_REF));

        ((DecisionTable) parentElement).addOutput(output);
    }
}
