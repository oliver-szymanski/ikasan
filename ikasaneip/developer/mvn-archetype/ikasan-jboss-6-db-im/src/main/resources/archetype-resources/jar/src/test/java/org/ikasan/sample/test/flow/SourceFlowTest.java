package org.ikasan.sample.test.flow;

import example.io.model.Model;
import example.io.service.TargetService;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.platform.IkasanEIPTest;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.testharness.flow.FlowObserver;
import org.ikasan.testharness.flow.FlowSubject;
import org.ikasan.testharness.flow.FlowTestHarness;
import org.ikasan.testharness.flow.FlowTestHarnessImpl;
import org.ikasan.testharness.flow.expectation.model.*;
import org.ikasan.testharness.flow.expectation.service.OrderedExpectation;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample Scheduled Flow test.
 *
 * @author Ikasan Developmnet Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/monitor-service-conf.xml",
        "/monitor-conf.xml",
        "/shared-conf.xml",
        "/source-db-flow-conf.xml",
        "/db-conf.xml",
        "/jms-conf.xml",
        "/scheduled-conf.xml",
        "/exception-conf.xml",
        "/ikasan-transaction-conf.xml",
        "/mock-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
})
public class SourceFlowTest extends IkasanEIPTest
{
    /** mockery instance */
    @Resource
    Mockery mockery;

    /** flow on test */
    @Resource
    VisitingInvokerFlow sourceFlow;

    /**
     * Captures the actual components invoked and events created within the flow
     */
    @Resource
    FlowSubject testHarnessFlowEventListener;

    @Resource
    TargetService targetService;

    /**
     * Setup will clear down any previously defined observers and ignore all exception transformations.
     *
     */
    private void flowTest_setup()
    {
        testHarnessFlowEventListener.removeAllObservers();
    }

    /**
     * Tests flow operation for Sample Flow.
     */
    @SuppressWarnings("unchecked")
    @Test
    @DirtiesContext
    public void test_successful_sampleFlow_invocation() throws IOException
    {
        flowTest_setup();

        Model testDataModel = new Model();
        testDataModel.setId("1");
        testDataModel.setValue("one");
        targetService.save(testDataModel);

        //
        // setup expectations
        FlowTestHarness flowTestHarness = new FlowTestHarnessImpl(new OrderedExpectation()
        {
            {
                // main request flow
                expectation(new ConsumerComponent("Consumer Name"), "Consumer Name");
                expectation(new ProducerComponent("Producer Name"), "Producer Name");
            }
        });

        testHarnessFlowEventListener.addObserver((FlowObserver) flowTestHarness);
        testHarnessFlowEventListener.setIgnoreEventCapture(true);

        // configure AMQ to provide in-memory destinations for the test
        Map<String,String> jndiProperties = new HashMap<String,String>();
        jndiProperties.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jndiProperties.put("java.naming.provider.url", "vm://localhost?broker.persistent=false");

        // configure the JMS producer for the test
        FlowElement<?> producerFlowElement = this.sourceFlow.getFlowElement("Producer Name");
        ConfiguredResource<SpringMessageProducerConfiguration> configuredProducer = (ConfiguredResource)producerFlowElement.getFlowComponent();
        SpringMessageProducerConfiguration producerConfiguration = configuredProducer.getConfiguration();
        producerConfiguration.setConnectionFactoryName("ConnectionFactory");
        producerConfiguration.setConnectionFactoryJndiProperties(jndiProperties);
        producerConfiguration.setDestinationJndiName("dynamicTopics/queue");
        producerConfiguration.setDestinationJndiProperties(jndiProperties);

        // start the flow
        this.sourceFlow.start();
        Assert.assertEquals("flow should be running", "running", this.sourceFlow.getState());

        // wait for the flow to fully execute
        try
        {
            Thread.sleep(4000);
        }
        catch(InterruptedException e)
        {
            Assert.fail(e.getMessage());
        }

        // stop the flow
        this.sourceFlow.stop();

        Assert.assertEquals("flow should be stopped", "stopped", this.sourceFlow.getState());

        // run flow assertions
        flowTestHarness.assertIsSatisfied();

        // mocked assertions
        mockery.assertIsSatisfied();
    }

}