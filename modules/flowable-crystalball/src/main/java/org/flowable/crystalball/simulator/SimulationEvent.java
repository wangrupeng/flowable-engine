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
package org.flowable.crystalball.simulator;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;

/**
 * Simulation event representation
 *
 * @author martin.grofcik
 */
public class SimulationEvent {

    private final long simulationTime;
    private final String type;
    private final Map<String, Object> properties;
    private final int priority;

    protected SimulationEvent(Builder builder) {
        this.simulationTime = builder.simulationTime;
        this.type = builder.type;
        this.properties = builder.properties;
        this.priority = builder.priority;

    }

    public Object getProperty() {
        return properties.entrySet().iterator().next().getValue();
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        String date = hasSimulationTime() ? (new Date(simulationTime)) + ", " : "now ";
        return date + type + ", " + priority + ", " + properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public boolean hasSimulationTime() {
        return this.simulationTime != Long.MIN_VALUE;
    }

    public static class Builder {
        // required
        private long simulationTime;
        private String type;

        // optional
        private Map<String, Object> properties;
        private Object property;
        private int priority;

        public Builder(String type) {
            this.type = type;
            this.simulationTime = Long.MIN_VALUE;
        }

        public Builder simulationTime(long simulationTime) {
            this.simulationTime = simulationTime;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public Builder property(Object property) {
            this.property = property;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public SimulationEvent build() {
            return new SimulationEvent(this);
        }
    }

    public static class Factory implements FactoryBean<SimulationEvent> {
        private long simulationTime;
        private String type;
        private Map<String, Object> properties;
        private int priority;

        public Factory(String type, long simulationTime, Map<String, Object> properties) {
            this.type = type;
            this.simulationTime = simulationTime;
            this.properties = properties;
        }

        @Override
        public SimulationEvent getObject() throws Exception {
            return new Builder(this.type).simulationTime(this.simulationTime).properties(this.properties).priority(this.priority).build();
        }

        @Override
        public Class<?> getObjectType() {
            return SimulationEvent.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

}
