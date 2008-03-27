/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.model.wrapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.riotfamily.media.model.RiotFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class BuiltInWrapperFactory implements ValueWrapperFactory {

	public ValueWrapper createWapper(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof ValueWrapper) {
			return (ValueWrapper) value;
		}
		ValueWrapper wrapper = null;
		if (value instanceof String) {
			wrapper = new StringWrapper();
		}
		if (value instanceof Number) {
			wrapper = new NumberWrapper();
		}
		if (value instanceof Boolean) {
			wrapper = new BooleanWrapper();
		}
		if (value instanceof Date) {
			wrapper = new DateWrapper();
		}
		if (value instanceof RiotFile) {
			wrapper = new RiotFileWrapper();
		}
		if (value instanceof List) {
			wrapper = new ListWrapper();
		}
		if (value instanceof Map) {
			wrapper = new MapWrapper();
		}
		if (wrapper != null) {
			wrapper.wrap(value);
		}
		return wrapper;
	}
	
}