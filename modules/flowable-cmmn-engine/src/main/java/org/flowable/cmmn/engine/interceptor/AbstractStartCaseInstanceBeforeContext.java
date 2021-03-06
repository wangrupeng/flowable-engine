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
package org.flowable.cmmn.engine.interceptor;

import java.util.Map;

import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.model.Case;
import org.flowable.cmmn.model.CmmnModel;

public class AbstractStartCaseInstanceBeforeContext {

    protected String businessKey;
    protected String businessStatus;
    protected String caseInstanceName;
    protected Map<String, Object> variables;
    protected Case caseModel;
    protected CaseDefinition caseDefinition;
    protected CmmnModel cmmnModel;

    public AbstractStartCaseInstanceBeforeContext() {

    }

    public AbstractStartCaseInstanceBeforeContext(String businessKey, String businessStatus, String caseInstanceName, Map<String, Object> variables,
            Case caseModel, CaseDefinition caseDefinition, CmmnModel cmmnModel) {

        this.businessKey = businessKey;
        this.businessStatus = businessStatus;
        this.caseInstanceName = caseInstanceName;
        this.variables = variables;
        this.caseModel = caseModel;
        this.caseDefinition = caseDefinition;
        this.cmmnModel = cmmnModel;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public String getCaseInstanceName() {
        return caseInstanceName;
    }

    public void setCaseInstanceName(String caseInstanceName) {
        this.caseInstanceName = caseInstanceName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Case getCaseModel() {
        return caseModel;
    }

    public void setCaseModel(Case caseModel) {
        this.caseModel = caseModel;
    }

    public CaseDefinition getCaseDefinition() {
        return caseDefinition;
    }

    public void setCaseDefinition(CaseDefinition caseDefinition) {
        this.caseDefinition = caseDefinition;
    }

    public CmmnModel getCmmnModel() {
        return cmmnModel;
    }

    public void setCmmnModel(CmmnModel cmmnModel) {
        this.cmmnModel = cmmnModel;
    }
}
