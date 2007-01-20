/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.messaging;

import java.io.Serializable;

import javax.jms.MessageListener;

import org.jamocha.rete.Rete;

/**
 * @author Peter Lin
 * 
 * MessageClient is used to 
 */
public interface MessageClient extends Serializable, MessageListener, Runnable {
	/**
	 * Classes implementing the method must provide concrete logic for closing
	 * the connection and cleaning up the references.
	 */
	void close();

	/**
	 * Method should contain the logic to lookup the connection factory and
	 * create the connection.
	 */
	void connect();

	/**
	 * The ConnectionFactory is used to perform the JNDI lookup. Each
	 * manufacturer provides their own name for the connection factory.
	 * 
	 * @return
	 */
	String getConnectionFactory();

	/**
	 * The initial context factory is used to lookup the JMS server using JNDI.
	 * This is the first step in the process of creating a JMS connection. The
	 * class is responsible for making the connection to the remote server.
	 * 
	 * @return
	 */
	String getInitialContextFactory();

	/**
	 * The name of the message client instance. The name will be used to lookup
	 * the client instance from a hastable or hashmap.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * the provider url defines the host and port for looking up the Initial
	 * context factory.
	 * 
	 * @return
	 */
	String getProviderURL();

	/**
	 * The credentials is the password for a given account.
	 * 
	 * @return
	 */
	String getSecurityCredentials();

	/**
	 * The principles is the username in most cases.
	 * 
	 * @return
	 */
	String getSecurityPrinciple();

	/**
	 * Returns the Topic for the client
	 * 
	 * @return
	 */
	String getTopic();

	/**
	 * Classes implementing the method should provide concrete logic for
	 * publishing a message. The class should format the message correctly and
	 * set JMSType correctly.
	 * 
	 * @param message
	 */
	void publish(Serializable message);

	/**
	 * Classes implementing the method need to create a new ObjectMessage and
	 * publish it.
	 * 
	 * @param body
	 */
	void publishObject(Object body);

	/**
	 * The connection factory is used to create a new connection to the JMS
	 * server. In the case of Orion, the name is "jms/TopicConnectionFactory".
	 * Other servers use different names.
	 * 
	 * @param name
	 */
	void setConnectionFactory(String name);

	/**
	 * The initial context factory is used to create a new InitialContext. The
	 * context is then used to lookup the TopicConnectionFactory.
	 * 
	 * @param ctxname
	 */
	void setInitialContextFactory(String ctxname);

	/**
	 * The name for the message client instance
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * The url is the remote server's RMI location.
	 * 
	 * @param url
	 */
	void setProviderURL(String url);

	/**
	 * A password should be provided in case authentication is needed.
	 * 
	 * @param pwd
	 */
	void setSecurityCredentials(String pwd);

	/**
	 * A username should be provided in case authentication is needed.
	 * 
	 * @param username
	 */
	void setSecurityPrinciple(String username);

	/**
	 * Set the topic for the client
	 * 
	 * @param topic
	 */
	void setTopic(String topic);

	/**
	 * All clients should have a handle to the rule engine. It is necessary,
	 * because the client will need to assert objects and facts to the rule
	 * engine at some point.
	 * 
	 * @param engine
	 */
	void setRete(Rete engine);

	/**
	 * Classes implementing the method should create a new thread to run the
	 * client.
	 */
	void start();

}
