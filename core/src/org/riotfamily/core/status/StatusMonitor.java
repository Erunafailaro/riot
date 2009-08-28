/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.status;

import java.util.Map;

import org.riotfamily.core.screen.ModelAndViewScreenlet;
import org.riotfamily.core.screen.ScreenContext;

public abstract class StatusMonitor extends ModelAndViewScreenlet {

	@Override
	protected Class<?> getTemplateClass() {
		return StatusMonitor.class;
	}
	
	@Override
	protected final void populateModel(Map<String, Object> model,
			ScreenContext context) {
		
		model.put("status", getStatus(context));
	}
	
	protected abstract Status getStatus(ScreenContext context);

}