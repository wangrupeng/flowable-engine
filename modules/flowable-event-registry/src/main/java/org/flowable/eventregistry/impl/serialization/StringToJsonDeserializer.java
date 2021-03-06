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
package org.flowable.eventregistry.impl.serialization;

import java.io.IOException;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.eventregistry.api.FlowableEventInfo;
import org.flowable.eventregistry.api.InboundEventDeserializer;
import org.flowable.eventregistry.impl.FlowableEventInfoImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Joram Barrez
 * @author Filip Hrisafov
 */
public class StringToJsonDeserializer implements InboundEventDeserializer<JsonNode> {

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public FlowableEventInfo<JsonNode> deserialize(Object rawEvent) {
        try {
            JsonNode eventNode = objectMapper.readTree(convertEventToString(rawEvent));
            return new FlowableEventInfoImpl<>(null, eventNode);
        } catch (IOException e) {
            throw new FlowableException("Could not deserialize event to json", e);
        }
    }
    
    public String convertEventToString(Object rawEvent) {
        return rawEvent.toString();
    }

}
