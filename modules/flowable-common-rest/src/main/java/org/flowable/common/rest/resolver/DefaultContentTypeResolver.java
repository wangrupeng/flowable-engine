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

package org.flowable.common.rest.resolver;

/**
 * Default implementation of a {@link ContentTypeResolver}, resolving a limited set of well-known content types used in the engine.
 * 
 * @author Tijs Rademakers
 * @author Yvo Swillens
 * @author Tim Stephenson
 */
public class DefaultContentTypeResolver implements ContentTypeResolver {

    @Override
    public String resolveContentType(String resourceName) {
        String contentType = null;
        if (resourceName != null && !resourceName.isEmpty()) {
            String lowerResourceName = resourceName.toLowerCase();

            if (lowerResourceName.endsWith("png")) {
                contentType = "image/png";
            } else if (lowerResourceName.endsWith("xml")
                    || lowerResourceName.endsWith("bpmn")
                    || lowerResourceName.endsWith("dmn")) {
                contentType = "text/xml";
            } else if (lowerResourceName.endsWith(".app")
                    || lowerResourceName.endsWith(".event")
                    || lowerResourceName.endsWith(".form")
                    || lowerResourceName.endsWith(".channel")) {
                contentType = "application/json";
            }
        }
        return contentType;
    }
}
