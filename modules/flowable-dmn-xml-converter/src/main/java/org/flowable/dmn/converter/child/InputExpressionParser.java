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
import org.flowable.dmn.model.DmnElement;
import org.flowable.dmn.model.InputClause;
import org.flowable.dmn.model.LiteralExpression;

/**
 * @author Yvo Swillens
 */
public class InputExpressionParser extends BaseChildElementParser {

    @Override
    public String getElementName() {
        return ELEMENT_INPUT_EXPRESSION;
    }

    @Override
    public void parseChildElement(XMLStreamReader xtr, DmnElement parentElement, Decision decision) throws Exception {
        if (!(parentElement instanceof InputClause)) {
            return;
        }

        InputClause clause = (InputClause) parentElement;
        LiteralExpression inputExpression = new LiteralExpression();
        inputExpression.setId(xtr.getAttributeValue(null, ATTRIBUTE_ID));
        inputExpression.setTypeRef(xtr.getAttributeValue(null, ATTRIBUTE_TYPE_REF));

        boolean readyWithInputExpression = false;
        try {
            while (!readyWithInputExpression && xtr.hasNext()) {
                xtr.next();
                if (xtr.isStartElement() && ELEMENT_TEXT.equalsIgnoreCase(xtr.getLocalName())) {
                    inputExpression.setText(xtr.getElementText());

                } else if (xtr.isEndElement() && getElementName().equalsIgnoreCase(xtr.getLocalName())) {
                    readyWithInputExpression = true;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error parsing input expression", e);
        }

        clause.setInputExpression(inputExpression);
    }
}
