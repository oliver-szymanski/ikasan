/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.monitor.notifier;


import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.monitor.Notifier;


/**
 * Ikasan default email notifier implementation.
 *
 * @author Ikasan Development Team
 */
public class DashboardNotifier implements Notifier<String>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(DashboardNotifier.class);
    
    /** only interested in state changes */
    boolean notifyStateChangesOnly = true;

    /** last update sent time */
    long lastUpdateDateTime;

    /** the base url of the dashboard */
    private String dashboardBaseUrl;
    
    /** the platform configuration service */
    protected ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    @Override
    public void invoke(String environment, String moduleName, String flowName, String state)
    {
    	notify(environment, moduleName, flowName, state);
    }

    @Override
    public void setNotifyStateChangesOnly(boolean notifyStateChangesOnly)
    {
        this.notifyStateChangesOnly = notifyStateChangesOnly;
    }

    @Override
    public boolean isNotifyStateChangesOnly()
    {
        return this.notifyStateChangesOnly;
    }
    
    /**
	 * @return the dashboardBaseUrl
	 */
	public void setDashboardBaseUrl(String dashboardBaseUrl)
	{
		this.dashboardBaseUrl = dashboardBaseUrl;
	}

	

    /**
	 * @param configurationManagement the configurationManagement to set
	 */
	public void setConfigurationService(
			ConfigurationManagement<ConfiguredResource, Configuration> configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
     * Internal notify method
     * 
     * @param environment
     * @param name
     * @param state
     */
    protected void notify(String environment, String moduleName, String flowName, String state)
    {
    	String url = null;
    	
    	try
		{
    		// We are trying to get the database configuration resource first
    		if(this.configurationService != null)
    		{
	    		Configuration configuration = this.configurationService.getConfiguration("platform-configuration");
	            
	            final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();
	            
	            if(parameters != null && parameters.size() > 0 && (parameters.get(0) instanceof ConfigurationParameterMapImpl))
	            {
	            	ConfigurationParameterMapImpl parameter = (ConfigurationParameterMapImpl)parameters.get(0);
	                
	                url = parameter.getValue().get("dashboardBaseUrl");
	            }
    		}
    		
    		// If we do not have a database persisted configuration value we will try to get the one from the file system/
    		if((url == null || url.length() == 0) && this.dashboardBaseUrl != null && this.dashboardBaseUrl.length() > 0)
    		{
    			url = this.dashboardBaseUrl;
    		}
    		
    		// Otherwise we'll throw an exception! 
    		if(url == null || url.length() == 0)
    		{
    			throw new RuntimeException("Cannot notify dashboard. The dashboard URL is null or empty string!");
    		}
    		
			url = url + "/rest/topologyCache/updateCache/" + moduleName + "/" + flowName;
			
			logger.info("Attempting to call URL: " + url);	
		
	    	
	    	ClientConfig clientConfig = new ClientConfig();
	    	
	    	Client client = ClientBuilder.newClient(clientConfig);
	    	
	    	WebTarget webTarget = client.target(url);
		    
	    	Response response = webTarget.request().put(Entity.entity(state, MediaType.APPLICATION_JSON));
		}
		catch(Exception e)
		{
			logger.error("caught exception: " + e.getMessage());
			
			throw new RuntimeException("An exception occurred trying to notify the dashboard!", e);
		}
    }
}
