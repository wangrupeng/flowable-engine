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
package org.flowable.engine.test.mock;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.scripting.Resolver;
import org.flowable.common.engine.impl.scripting.ResolverFactory;

/**
 * This is a bridge resolver, making available any objects registered through {@link org.flowable.engine.test.mock.Mocks#register} inside scripts supported by process execution. <br>
 * <br>
 * In order to use it, you need to declare it as ResolverFactory, for example by using flowable.cfg.xml like this: <br>
 * <br>
 * 
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;<br>
 * &lt;beans xmlns=&quot;http://www.springframework.org/schema/beans&quot;<br>
 * xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;<br>
 * xsi:schemaLocation=&quot;http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd&quot;&gt;<br>
 * <br>
 * &lt;bean id=&quot;processEngineConfiguration&quot;<br>
 * class=&quot;org.flowable.engine.impl.cfg. StandaloneInMemProcessEngineConfiguration&quot;&gt;<br>
 * &lt;property name=&quot;expressionManager&quot;&gt;<br>
 * &lt;bean class=&quot;org.flowable.engine.test.mock.MockExpressionManager&quot; /&gt;<br>
 * &lt;/property&gt;<br>
 * &lt;property name=&quot;resolverFactories&quot;&gt;<br>
 * &lt;list&gt;<br>
 * &lt;bean class=&quot;org.flowable.common.engine.impl.scripting.VariableScopeResolverFactory &quot; /&gt;<br>
 * &lt;bean class=&quot;org.flowable.common.engine.impl.scripting.BeansResolverFactory&quot; /&gt;<br>
 * &lt;bean class=&quot;com.deenero.activiti.MockResolverFactory&quot; /&gt;<br>
 * &lt;/list&gt;<br>
 * &lt;/property&gt;<br>
 * &lt;/bean&gt;<br>
 * <br>
 * &lt;/beans&gt; <br>
 * <br>
 * or by any other means of creating configuration.
 * 
 * @author Emil Genov (http://www.emil-genov.info/)
 * 
 */
public class MockResolverFactory implements ResolverFactory {
    @Override
    public Resolver createResolver(AbstractEngineConfiguration engineConfiguration, VariableContainer scopeContainer,
            VariableContainer inputVariableContainer) {
        return new Resolver() {

            @Override
            public Object get(Object key) {
                return Mocks.get(key);
            }

            @Override
            public boolean containsKey(Object key) {
                return Mocks.get(key) != null;
            }
        };
    }
}
