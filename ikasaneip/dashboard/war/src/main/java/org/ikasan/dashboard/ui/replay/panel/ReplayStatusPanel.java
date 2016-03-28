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
package org.ikasan.dashboard.ui.replay.panel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.ReplayEventViewPopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.dashboard.ui.replay.window.ReplayEventViewWindow;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayListener;
import org.ikasan.spec.replay.ReplayService;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ReplayStatusPanel extends Panel implements ReplayListener<ReplayAuditEvent>
{
	private Logger logger = Logger.getLogger(ReplayStatusPanel.class);
	
	private List<ReplayEvent> replayEvents;
	
	private IndexedContainer tableContainer;
	
	private FilterTable replayEventsTable;
	
	private ReplayService<ReplayEvent, ReplayAuditEvent> replayService;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private ProgressBar bar = new ProgressBar(0.0f);
	
	private TextArea comments;
	private ComboBox targetServerComboBox;
	
	private IkasanAuthentication authentication;
	
	public ReplayStatusPanel(List<ReplayEvent> replayEvents,
			ReplayService<ReplayEvent, ReplayAuditEvent> replayService,
			PlatformConfigurationService platformConfigurationService) 
	{
		super();
		
		this.replayEvents = replayEvents;
		if(this.replayEvents == null)
		{
			throw new IllegalArgumentException("replayEvents cannot be null!");
		}
		this.replayService = replayService;
		if(this.replayService == null)
		{
			throw new IllegalArgumentException("replayService cannot be null!");
		}
		this.replayService.addReplayListener(this);
		this.platformConfigurationService = platformConfigurationService;
		if(this.platformConfigurationService == null)
		{
			throw new IllegalArgumentException("platformConfigurationService cannot be null!");
		}
		
		init();
	}
	
	protected IndexedContainer buildContainer() 
	{			
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty(" ", Label.class,  null);
		cont.addContainerProperty("Module Name", String.class,  null);
		cont.addContainerProperty("Flow Name", String.class,  null);
		cont.addContainerProperty("Event Id / Payload Id", String.class,  null);
		cont.addContainerProperty("Message", String.class,  null);
		cont.addContainerProperty("Timestamp", String.class,  null);
		cont.addContainerProperty("", Button.class,  null);
		
        return cont;
    }

	public void init()
	{
		this.setSizeFull();
		
		authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
		GridLayout formLayout = new GridLayout(2, 6);
		formLayout.setSizeFull();
		formLayout.setSpacing(true);
		formLayout.setColumnExpandRatio(0, 0.2f);
		formLayout.setColumnExpandRatio(1, 0.8f);
		
		Label wiretapDetailsLabel = new Label("Replay");
		wiretapDetailsLabel.setStyleName(ValoTheme.LABEL_HUGE);
		formLayout.addComponent(wiretapDetailsLabel);
		
		
		Label moduleCountLabel = new Label("Number of events to replay:");
		moduleCountLabel.setSizeUndefined();
		
		formLayout.addComponent(moduleCountLabel, 0, 1);
		formLayout.setComponentAlignment(moduleCountLabel, Alignment.MIDDLE_RIGHT);
		
		TextField moduleCount = new TextField();
		
		if(this.replayEvents != null)
		{
			moduleCount.setValue(Integer.toString(this.replayEvents.size()));
		}
		else
		{
			moduleCount.setValue("0");
		}
		
		moduleCount.setReadOnly(true);
		moduleCount.setWidth("80%");
		formLayout.addComponent(moduleCount, 1, 1);
		
		
		Label targetServerLabel = new Label("Target server:");
		targetServerLabel.setSizeUndefined();
		
		formLayout.addComponent(targetServerLabel, 0, 2);
		formLayout.setComponentAlignment(targetServerLabel, Alignment.MIDDLE_RIGHT);
		
		this.initialiseTargetServerCombo();
		
		this.targetServerComboBox.setWidth("80%");
		formLayout.addComponent(this.targetServerComboBox, 1, 2);
		
		Label commentLabel = new Label("Comment:");
		commentLabel.setSizeUndefined();
		
		formLayout.addComponent(commentLabel, 0, 3);
		formLayout.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);
		
		comments = new TextArea();
		comments.setWidth("80%");
		comments.setRows(4);
		comments.setRequired(true);
		comments.addValidator(new StringLengthValidator(
	            "You must supply a comment!", 1, 2048, false));
		comments.setValidationVisible(false);         
		comments.setRequiredError("A comment is required!");
		comments.setNullSettingAllowed(false);
		
		formLayout.addComponent(comments, 1, 3);
		
		Button replayButton = new Button("Replay");
		replayButton.addStyleName(ValoTheme.BUTTON_SMALL);
		replayButton.setImmediate(true);
		replayButton.setDescription("Replay events.");
		
		replayButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	
            	try 
            	{
            		comments.validate();
                } 
                catch (Exception e) 
                {
                	comments.setValidationVisible(true);                	
                	comments.markAsDirty();
                    return;
                }
                
            	bar.setVisible(true);
            	
            	ExecutorService executorService = Executors
            			.newSingleThreadExecutor();
            	
            	try
            	{
	            	executorService.execute(new Runnable()
	    			{
	    				@Override
	    				public void run() 
	    				{	    					
	    					replayService.replay((String)targetServerComboBox.getValue(), replayEvents, authentication.getName(), 
	    							(String)authentication.getCredentials(), authentication.getName(), comments.getValue());
	    					
	    					logger.info("Finished replaying events!");
	    					
	    					VaadinSession.getCurrent().getLockInstance().lock();
		            		try 
		            		{
		            			bar.setVisible(false);
		    					
		    					Notification.show("Event replay complete.");
		        				
		            		} 
		            		finally 
		            		{
		            			VaadinSession.getCurrent().getLockInstance().unlock();
		            		}
		                	
		                	UI.getCurrent().push();	
	    				}
	    			});
            	}
            	finally
            	{
            		executorService.shutdown();
            	}
            }
        });
		
		formLayout.addComponent(replayButton, 0, 4, 1, 4);
		formLayout.setComponentAlignment(replayButton, Alignment.MIDDLE_CENTER);
		
		this.bar.setWidth("40%");	
		this.bar.setImmediate(true);
		this.bar.setIndeterminate(true);
		this.bar.setVisible(false);
		
		formLayout.addComponent(bar, 0, 5, 1, 5);
		formLayout.setComponentAlignment(bar, Alignment.MIDDLE_CENTER);
		
		this.replayEventsTable = new FilterTable();
		this.replayEventsTable.setFilterBarVisible(true);
		this.replayEventsTable.setSizeFull();
		this.replayEventsTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.replayEventsTable.addStyleName("ikasan");
		
		this.replayEventsTable.setColumnExpandRatio("Module Name", .14f);
		this.replayEventsTable.setColumnExpandRatio("Flow Name", .18f);
		this.replayEventsTable.setColumnExpandRatio("Event Id / Payload Id", .15f);
		this.replayEventsTable.setColumnExpandRatio("Message", .33f);
		this.replayEventsTable.setColumnExpandRatio("Timestamp", .1f);
		this.replayEventsTable.setColumnExpandRatio("", .05f);
		this.replayEventsTable.setColumnExpandRatio(" ", .05f);
		
		this.replayEventsTable.addStyleName("wordwrap-table");
		this.replayEventsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		
		tableContainer = this.buildContainer();
		this.replayEventsTable.setContainerDataSource(tableContainer);
		
		this.replayEventsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
		    	if(itemClickEvent.isDoubleClick())
		    	{
		    		ReplayEvent replayEvent = (ReplayEvent)itemClickEvent.getItemId();
			    	ReplayEventViewWindow replayEventViewWindow = new ReplayEventViewWindow(replayEvent
			    			, replayService, platformConfigurationService);
			    
			    	UI.getCurrent().addWindow(replayEventViewWindow);
		    	}
		    }
		});
		
		for(final ReplayEvent replayEvent: replayEvents)
    	{
    		Date date = new Date(replayEvent.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String timestamp = format.format(date);
    	    
    	    Item item = tableContainer.addItem(replayEvent);			            	    
    	    
    	    item.getItemProperty("Module Name").setValue(replayEvent.getModuleName());
			item.getItemProperty("Flow Name").setValue(replayEvent.getFlowName());
			item.getItemProperty("Event Id / Payload Id").setValue(replayEvent.getEventId());
			item.getItemProperty("Timestamp").setValue(timestamp);
			
			Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new window");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);
			
			BrowserWindowOpener popupOpener = new BrowserWindowOpener(ReplayEventViewPopup.class);
			popupOpener.setFeatures("height=600,width=900,resizable");
	        popupOpener.extend(popupButton);
	        
	        popupButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
	            	 VaadinService.getCurrentRequest().getWrappedSession().setAttribute("replayEvent", (ReplayEvent)replayEvent);
	            }
	        });
	        
	        item.getItemProperty("").setValue(popupButton);
    	}
		
		GridLayout layout = new GridLayout(1, 2);
		layout.setWidth("100%");
		layout.setMargin(true);
		
		layout.addComponent(formLayout);
		layout.addComponent(this.replayEventsTable);
		
		this.setContent(layout);
	}
	
	private List<String> getValidTargetServers()
	{
		String replayTargetServers = this.platformConfigurationService.getConfigurationValue("replayTargetServers");
		
		if(replayTargetServers != null && replayTargetServers.length() > 0)
		{
			return Arrays.asList(replayTargetServers.split(","));
		}
		else
		{
			return new ArrayList<String>();
		}
	}
	
	private void initialiseTargetServerCombo()
	{
		if(this.targetServerComboBox == null)
		{
			this.targetServerComboBox = new ComboBox();
		}
		
		this.targetServerComboBox.removeAllItems();
		
		List<String> targetServers = this.getValidTargetServers();
		
		for(String targetServer: targetServers)
		{
			this.targetServerComboBox.addItem(targetServer);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayListener#onReplay(java.lang.Object)
	 */
	@Override
	public void onReplay(final ReplayAuditEvent auditEvent) 
	{
		UI.getCurrent().access(new Runnable() 
		{
            @Override
            public void run() 
            {
            	VaadinSession.getCurrent().getLockInstance().lock();
        		try 
        		{
        			Item item = tableContainer.getItem(auditEvent.getReplayEvent());
        			
        			if(auditEvent.isSuccess())
        			{
        				item.getItemProperty(" ").setValue(new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML));
        			}
        			else
        			{
        				item.getItemProperty(" ").setValue(new Label(VaadinIcons.BAN.getHtml(), ContentMode.HTML));
        			}
        			
        			item.getItemProperty("Message").setValue(auditEvent.getResultMessage());     				
        		} 
        		finally 
        		{
        			VaadinSession.getCurrent().getLockInstance().unlock();
        		}
            	
            	UI.getCurrent().push();	
            }
        });
	}
}
