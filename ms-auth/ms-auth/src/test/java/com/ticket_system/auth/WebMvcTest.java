package com.ticket_system.auth;

import com.ticket_system.auth.Controller.AuthController;

/**
 * WebMvcTest
 */
public @interface WebMvcTest {

    Class<AuthController> value();

}
