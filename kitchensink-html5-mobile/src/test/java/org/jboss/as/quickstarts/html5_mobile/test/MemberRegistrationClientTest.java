/**
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.html5_mobile.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.quickstarts.html5_mobile.data.MemberRepository;
import org.jboss.as.quickstarts.html5_mobile.model.Member;
import org.jboss.as.quickstarts.html5_mobile.rest.JaxRsActivator;
import org.jboss.as.quickstarts.html5_mobile.rest.MemberService;
import org.jboss.as.quickstarts.html5_mobile.service.MemberRegistration;
import org.jboss.as.quickstarts.html5_mobile.util.Resources;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.iphone.IPhoneDriver;
import org.openqa.selenium.support.FindBy;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.element;
import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.junit.Assert.assertEquals;

/**
 * Uses Arquilian Drone to test the JAX-RS processing class for member registration on the client with.
 *
 * @author lholmquist
 */
@RunWith(Arquillian.class)
public class MemberRegistrationClientTest {

    @ArquillianResource
    URL contextUrl;

    @Drone
    WebDriver driver;

    @FindBy(id = "name")
    WebElement nameField;

    @FindBy(id = "email")
    WebElement emailField;

    @FindBy(id = "phoneNumber")
    WebElement phoneNumberField;

    @FindBy(id = "register")
    WebElement registerButton;

    @FindBy(id = "addMember")
    WebElement addMemberButton;


    @Deployment(testable = false)
    public static WebArchive createTestDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addClasses(Member.class, MemberService.class, MemberRepository.class,
                        MemberRegistration.class, Resources.class, JaxRsActivator.class)
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp")
                        .as(GenericArchive.class),"/")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return war;
    }

    @Test
    public void addMemberClientTest() {

        driver.get(contextUrl.toString());

        if( GrapheneContext.holdsInstanceOf(AndroidDriver.class) || GrapheneContext.holdsInstanceOf(IPhoneDriver.class) ) {
            waitModel().withMessage("Add button is not present.")
                    .until(element(addMemberButton).isVisible());

            addMemberButton.click();
        }

        waitModel().withMessage("Registration screen is not present.")
                .until(element(nameField).isVisible());

        nameField.sendKeys("Your Name");
        emailField.sendKeys("yname@email.com");
        phoneNumberField.sendKeys("1234567890");
        registerButton.submit();

        waitModel().withMessage("Waiting For Registration Confirm")
                .until(element(By.cssSelector("span.success")).isVisible());

        assertEquals("Member Registered", driver.findElement(By.id("formMsgs")).getText());


    }
}
