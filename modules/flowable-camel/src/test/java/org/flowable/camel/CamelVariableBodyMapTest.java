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
package org.flowable.camel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.SpringFlowableTestCase;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@Tag("camel")
@ContextConfiguration("classpath:generic-camel-flowable-context.xml")
public class CamelVariableBodyMapTest extends SpringFlowableTestCase {

    protected MockEndpoint service1;

    @Autowired
    protected CamelContext camelContext;

    @BeforeEach
    public void setUp() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("flowable:HelloCamel:serviceTask1").log(LoggingLevel.INFO, "Received message on service task").to("mock:serviceBehavior");
            }
        });

        service1 = (MockEndpoint) camelContext.getEndpoint("mock:serviceBehavior");
        service1.reset();
    }

    @AfterEach
    public void tearDown() throws Exception {
        List<Route> routes = camelContext.getRoutes();
        for (Route r : routes) {
            camelContext.getRouteController().stopRoute(r.getId());
            camelContext.removeRoute(r.getId());
        }
    }

    @Test
    @Deployment(resources = {"process/HelloCamelBodyMap.bpmn20.xml"})
    public void testCamelBody() throws Exception {
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("camelBody", "hello world");
        service1.expectedBodiesReceived(varMap);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HelloCamel", varMap);
        // Ensure that the variable is equal to the expected value.
        assertThat(runtimeService.getVariable(processInstance.getId(), "camelBody")).isEqualTo("hello world");
        service1.assertIsSatisfied();

        Task task = taskService.createTaskQuery().singleResult();

        // Ensure that the name of the task is correct.
        assertThat(task.getName()).isEqualTo("Hello Task");

        // Complete the task.
        taskService.complete(task.getId());
    }

}
