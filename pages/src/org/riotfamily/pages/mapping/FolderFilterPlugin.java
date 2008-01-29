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
package org.riotfamily.pages.mapping;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.common.web.filter.PluginChain;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.security.AccessController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * FilterPlugin that provides folder support. Normally the website-servlet is
 * mapped using a suffix-mapping like <code>*.html</code> and therefore can't
 * handle requests like </code>/some/folder</code>. In case a page with the
 * requested path exists and is {@link Page#isFolder() marked as folder}, this
 * plugin will send a redirect to the first <i>requestable</i> child-page.
 * A page is requestable if it is {@link Page#isEnabled() enabled} <em>or</em>
 * a Riot user {@link AccessController#isAuthenticatedUser() is logged in}.  
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class FolderFilterPlugin extends FilterPlugin {

	private static final Log log = LogFactory.getLog(FolderFilterPlugin.class);

	private static final TransactionDefinition TX_DEF = 
			new DefaultTransactionDefinition();
	

	private PageDao pageDao;
	
	private PageResolver pageResolver;
	
	private PathCompleter pathCompleter;
	
	private PlatformTransactionManager tx;
	
	private String siteChooserUrl;
	
	public FolderFilterPlugin(PageDao pageDao,
			PageResolver pageResolver,
			PathCompleter pathCompleter, 
			PlatformTransactionManager tx) {
		
		this.pageDao = pageDao;
		this.pageResolver = pageResolver;
		this.pathCompleter = pathCompleter;
		this.tx = tx;
	}
	
	/**
	 * Sets an URL to which the user will be redirected if no site matches.
	 * Default is <code>null</code>, which means that no redirect is sent and 
	 * the request is handed on to the next plugin in the chain.  
	 * @param  url A context-relative URL
	 */
	public void setSiteChooserUrl(String url) {
		this.siteChooserUrl = url;
	}

	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response, PluginChain pluginChain)
			throws IOException, ServletException {
		
		boolean requestHandled = false;
		String path = ServletUtils.getOriginatingPathWithinApplication(request);
		if (path.lastIndexOf('.') < path.lastIndexOf('/')) {
			TransactionStatus status = tx.getTransaction(TX_DEF);
			try {
				requestHandled = sendRedirect(request, response);
			}
			catch (Exception ex) {
				tx.rollback(status);
			    throw new ServletException(ex);
			}
			tx.commit(status);
		}
		if (!requestHandled) {
			pluginChain.doFilter(request, response);
		}
	}

	private boolean sendRedirect(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		Site site = pageResolver.getSite(request, pathCompleter);
		if (site == null) {
			if (siteChooserUrl != null) {
				response.sendRedirect(response.encodeRedirectURL(
						request.getContextPath() + siteChooserUrl));
				
				return true;
			}
			return false;
		}
		String path = pageResolver.getPathWithinSite(request);
		if (path.length() == 0) {
			Collection topLevelPages = pageDao.getRootNode().getChildPages(site);
			sendRedirect(topLevelPages, request, response);
			return true;
		}
		
		Page page = pageResolver.getPage(request);
		if (page == null || !page.isFolder()) {
			return false;
		}
		
		sendRedirect(page.getChildPages(), request, response);
		
		return true;
	}

	private void sendRedirect(Collection pages, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
		String url = getFirstRequestablePageUrl(pages);
		if (url != null) {
			url = response.encodeRedirectURL(request.getContextPath() + url);
			log.debug("Sending redirect to '" + url + "'");
			response.sendRedirect(url);
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	private boolean isRequestable(Page page) {
		return page.isEnabled() || AccessController.isAuthenticatedUser();
	}
		
	private String getFirstRequestablePageUrl(Collection pages) {
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (isRequestable(page)) {
				return page.getUrl(pathCompleter);
			}
		}
		return null;
	}

}
