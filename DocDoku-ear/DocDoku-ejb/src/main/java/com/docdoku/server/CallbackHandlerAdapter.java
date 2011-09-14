/*
 * DocDoku, Professional Open Source
 * Copyright 2006, 2007, 2008, 2009, 2010, 2011 DocDoku SARL
 *
 * This file is part of DocDoku.
 *
 * DocDoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DocDoku.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.server;

import javax.security.auth.callback.*;
import java.io.*;

public class CallbackHandlerAdapter implements CallbackHandler {

    private String mLogin;
    private char[] mPassword;

    public CallbackHandlerAdapter(String pLogin, char[] pPassword) {
        mLogin = pLogin;
        mPassword = pPassword;
    }

    @Override
    public void handle(Callback[] pCallbacks)
            throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < pCallbacks.length; i++) {
            if (pCallbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback) pCallbacks[i];
                nc.setName(mLogin);
            } else if (pCallbacks[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) pCallbacks[i];
                pc.setPassword(mPassword);
            } else {
                throw new UnsupportedCallbackException(
                        pCallbacks[i],
                        "Unrecognized callback");
            }
        }
    }
}