package com.irfansaf.safpass.util;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link CryptUtils}.
 *
 * @author Irfan
 */
public class CryptUtilsTest {

    @Test
    public void shouldCalculateSha256Hash() throws Exception {
        // given
        byte[] expectedHash = {
                (byte) 0xd0, (byte) 0xc0, (byte) 0x4f, (byte) 0x4b,
                (byte) 0x19, (byte) 0x51, (byte) 0xe4, (byte) 0xae,
                (byte) 0xaa, (byte) 0xec, (byte) 0x82, (byte) 0x23,
                (byte) 0xed, (byte) 0x20, (byte) 0x39, (byte) 0xe5,
                (byte) 0x42, (byte) 0xf3, (byte) 0xaa, (byte) 0xe8,
                (byte) 0x05, (byte) 0xa6, (byte) 0xfa, (byte) 0x7f,
                (byte) 0x6d, (byte) 0x79, (byte) 0x4e, (byte) 0x5a,
                (byte) 0xff, (byte) 0xf5, (byte) 0xd2, (byte) 0x72,
        };

        // when
        byte[] hash = CryptUtils.getSha256Hash("sesame".toCharArray());
    }
}
