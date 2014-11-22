/*
 * Copyright (c) 2014, Arjuna Technologies Limited, Newcastle-upon-Tyne, England. All rights reserved.
 */

package com.arjuna.dbplugins.twitter.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import static org.junit.Assert.*;

public class AuthenticationProperties
{
    public AuthenticationProperties(String authenticationPropertiesFilename)
    {
        _authenticationProperties = new Properties();

        try
        {
            FileReader authenticationFileReader = new FileReader(authenticationPropertiesFilename);
            _authenticationProperties.load(authenticationFileReader);
            authenticationFileReader.close();
        }
        catch (IOException ioException)
        {
            _authenticationProperties = null;
            fail("Failed to load \"" + authenticationPropertiesFilename + "\"");
        }
    }

    public String getConsumerKey()
    {
        if (_authenticationProperties != null)
        {
            String consumerKey = _authenticationProperties.getProperty("consumer.key");

            if ((consumerKey != null))
                return consumerKey;
            else
            {
                fail("Failed to obtain \"consumer.key\" property");
                return null;
            }
        }
        else
        {
            fail("Failed to obtain \"consumer.key\" property, no property file");
            return null;
        }
    }

    public String getConsumerSecret()
    {
        if (_authenticationProperties != null)
        {
            String consumerSecret = _authenticationProperties.getProperty("consumer.secret");

            if ((consumerSecret != null))
                return consumerSecret;
            else
            {
                fail("Failed to obtain \"consumer.secret\" property");
                return null;
            }
        }
        else
        {
            fail("Failed to obtain \"consumer.secret\" property, no property file");
            return null;
        }
    }

    public String getToken()
    {
        if (_authenticationProperties != null)
        {
            String token = _authenticationProperties.getProperty("token");

            if ((token != null))
                return token;
            else
            {
                fail("Failed to obtain \"token\" property");
                return null;
            }
        }
        else
        {
            fail("Failed to obtain \"token\" property, no property file");
            return null;
        }
    }

    public String getSecret()
    {
        if (_authenticationProperties != null)
        {
            String secret = _authenticationProperties.getProperty("secret");

            if ((secret != null))
                return secret;
            else
            {
                fail("Failed to obtain \"secret\" property");
                return null;
            }
        }
        else
        {
            fail("Failed to obtain \"secret\" property, no property file");
            return null;
        }
    }

    private Properties _authenticationProperties = new Properties();
}
