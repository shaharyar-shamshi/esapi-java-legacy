/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * http://www.owasp.org/esapi.
 *
 * Copyright (c) 2007 - The OWASP Foundation
 * 
 * The ESAPI is published by OWASP under the LGPL. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created 2007
 */
package org.owasp.esapi;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.owasp.esapi.errors.AuthenticationException;
import org.owasp.esapi.errors.IntegrityException;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.http.TestHttpServletRequest;
import org.owasp.esapi.http.TestHttpServletResponse;

/**
 * The Class IntrusionDetectorTest.
 * 
 * @author Jeff Williams (jeff.williams@aspectsecurity.com)
 */
public class IntrusionDetectorTest extends TestCase {

	/**
	 * Instantiates a new intrusion detector test.
	 * 
	 * @param testName
	 *            the test name
	 */
	public IntrusionDetectorTest(String testName) {
		super(testName);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// none
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// none
	}

	/**
	 * Suite.
	 * 
	 * @return the test
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(IntrusionDetectorTest.class);

		return suite;
	}

	/**
	 * Test of addException method, of class org.owasp.esapi.IntrusionDetector.
	 * 
	 * @throws AuthenticationException
	 *             the authentication exception
	 */
	public void testAddException() throws AuthenticationException {
		System.out.println("addException");
		IntrusionDetector.getInstance().addException( new IntrusionException("user message", "log message") );
		String username = Randomizer.getInstance().getRandomString(8, Encoder.CHAR_ALPHANUMERICS);
		User user = Authenticator.getInstance().createUser(username, "addException", "addException");
		user.enable();
	    TestHttpServletRequest request = new TestHttpServletRequest();
		TestHttpServletResponse response = new TestHttpServletResponse();
        Authenticator.getInstance().setCurrentHTTP(request, response);
		user.loginWithPassword("addException");
		
		// Now generate some exceptions to disable account
		for ( int i = 0; i < SecurityConfiguration.getInstance().getQuota("org.owasp.esapi.errors.IntegrityException").count; i++ ) {
            // EnterpriseSecurityExceptions are added to IntrusionDetector automatically
            new IntegrityException( "IntegrityException " + i, "IntegrityException " + i );
		}
        assertFalse( user.isLoggedIn() );
	}

    
    /**
     * Test of addEvent method, of class org.owasp.esapi.IntrusionDetector.
     * 
     * @throws AuthenticationException
     *             the authentication exception
     */
    public void testAddEvent() throws AuthenticationException {
        System.out.println("addEvent");
		String username = Randomizer.getInstance().getRandomString(8, Encoder.CHAR_ALPHANUMERICS);
		User user = Authenticator.getInstance().createUser(username, "addEvent", "addEvent");
		user.enable();
	    TestHttpServletRequest request = new TestHttpServletRequest();
		TestHttpServletResponse response = new TestHttpServletResponse();
        Authenticator.getInstance().setCurrentHTTP(request, response);
		user.loginWithPassword("addEvent");
        
        // Now generate some events to disable user account
        for ( int i = 0; i < SecurityConfiguration.getInstance().getQuota("event.test").count; i++ ) {
            IntrusionDetector.getInstance().addEvent("test");
        }
        assertFalse( user.isEnabled() );
    }
    
}