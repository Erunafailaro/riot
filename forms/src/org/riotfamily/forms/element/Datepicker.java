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
package org.riotfamily.forms.element;

import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.client.Html;
import org.springframework.context.i18n.LocaleContextHolder;

public class Datepicker extends TextField {
	
	private static Map<String, Object> defaults = Generics.newHashMap();
	static {
		defaults.put("showAnim", "");
	}
	
	public class State extends TextField.State {

		@Override
		protected void renderElement(Html html) {
			super.renderElement(html);
			Locale locale = LocaleContextHolder.getLocale();
			html.invoke(id(), ":text", "datepicker", getMergedConfig(locale));
		}
		
		protected Map<String, Object> getMergedConfig(Locale locale) {
			Map<String, Object> merged = Generics.newHashMap();
			merged.putAll(defaults);
			/*
			if (config != null) {
				merged.putAll(config);
			}
			*/
			return merged;
		}
	} 
}
