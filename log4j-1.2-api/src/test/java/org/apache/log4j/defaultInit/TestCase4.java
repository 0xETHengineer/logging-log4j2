/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.defaultInit;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Ignore;

@Ignore
public class TestCase4 extends TestCase {

  public TestCase4(String name) {
    super(name);
  }

  public void setUp() {
  }

  public void tearDown() {
    LogManager.shutdown();
  }

  public void combinedTest() {
    Logger root = Logger.getRootLogger();
    boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
    assertTrue(rootIsConfigured);
    Enumeration e = root.getAllAppenders();
    Appender appender = (Appender) e.nextElement();
    assertEquals(appender.getName(), "D1");
    assertEquals(e.hasMoreElements(), false);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestCase4("combinedTest"));
    return suite;
  }

}
