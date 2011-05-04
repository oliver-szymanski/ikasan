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
package org.ikasan.tools.messaging.server;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.Message;

import org.ikasan.tools.messaging.destination.DestinationHandle;
import org.ikasan.tools.messaging.destination.discovery.DestinationDiscoverer;

public class DestinationServer {

	
	private List<DestinationHandle> destinations = new ArrayList<DestinationHandle>();
	
	private ConnectionFactory connectionFactory;
	
	
	public DestinationServer(DestinationDiscoverer destinationDiscoverer, ConnectionFactory connectionFactory){
		this.destinations = destinationDiscoverer.findDestinations();
		this.connectionFactory = connectionFactory;
	}
	
	
	
	public List<DestinationHandle> getDestinations() {
		return destinations;
	}

	
	public void publishTextMessage(String destinationPath, String messageText, int priority){
		getDestination(destinationPath).publishTextMessage(connectionFactory,  messageText, priority);
	}
	
	public void createSimpleSubscription(String destinationPath){
		getDestination(destinationPath).startSimpleSubscription(connectionFactory);
	}
	
	public void destroySimpleSubscription(String destinationPath){
		getDestination(destinationPath).stopSimpleSubscription();
	}



	public DestinationHandle getDestination(String destinationPath) {
		for (DestinationHandle destinationHandle : destinations){
			if (destinationHandle.getDestinationPath().equals(destinationPath)){
				return destinationHandle;
			}
		}
		return null;
		
	}



	public Message getMessage(String destinationPath, String messageId) {
		return getDestination(destinationPath).getSimpleSubscriber().getMessage(messageId);
	}
	
	
}