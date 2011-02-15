package com.yubico.client.v2.impl;

import com.yubico.client.v2.YubicoClient;
import com.yubico.client.v2.YubicoResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;

/* Copyright (c) 2011, Linus Widströmer.  All rights reserved.

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
  
   * Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  
   * Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following
     disclaimer in the documentation and/or other materials provided
     with the distribution.
 
   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
   CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
   TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
   ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
   TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
   THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
   SUCH DAMAGE.
 
   Written by Linus Widströmer <linus.widstromer@it.su.se>, January 2011.
*/

public class YubicoClientImpl extends YubicoClient {
    private static Logger logger = LoggerFactory.getLogger(YubicoClientImpl.class);

    /** {@inheritDoc} */
    public YubicoClientImpl() {}

    /** {@inheritDoc} */
    public YubicoClientImpl(Integer id) {
        this.clientId=id;
    }

    /** {@inheritDoc} */
    public YubicoResponse verify(String otp) {
        try {
            String nonce=java.util.UUID.randomUUID().toString().replaceAll("-","");
            /* XXX we only use the first wsapi URL - not a real validation v2.0 client yet */
            URL srv = new URL(wsapi_urls[0] + "?id=" + clientId +
                    "&otp=" + otp +
                    "&timestamp=1" +
                    "&nonce=" + nonce
            );
            URLConnection conn = srv.openConnection();
            YubicoResponse response = new YubicoResponseImpl(conn.getInputStream());

            // Verify the result
            if(!otp.equals(response.getOtp())) {
                logger.warn("OTP mismatch in response, is there a man-in-the-middle?");
                return null;
            }

            if(!nonce.equals(response.getNonce())) {
                logger.warn("Nonce mismatch in response, is there a man-in-the-middle?");
                return null;
            }

            return response;
        } catch (Exception e) {
            logger.warn("Got exception when parsing response from server.", e);
            return null;
        }
    }
}