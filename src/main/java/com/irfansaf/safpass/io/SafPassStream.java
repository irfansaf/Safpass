package com.irfansaf.safpass.io;

import com.irfansaf.safpass.util.CryptUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface SafPassStream {

    enum FileVersionType {
        VERSION_0(0, 0, (text, salt) -> CryptUtils.getSha256HashWithDefaultIterations(text)),
        VERSION_1(1, 16, (text, salt) -> CryptUtils.getPBKDF2KeyWithDefaultIterations(text, salt));

        private final int version;
        private final int saltLength;
        private final BiFunction<char[], byte[], byte[]> keyGenerator;

        FileVersionType(int version, int saltLength, BiFunction<char[], byte[], byte[]> keyGenerator) {
            this.version = version;
            this.saltLength = saltLength;
            this.keyGenerator = Objects.requireNonNull(keyGenerator, "keyGenerator must be provided");
        }

        public int getVersion() {
            return version;
        }

        public int getSaltLength() {
            return saltLength;
        }

        public BiFunction<char[], byte[], byte[]> getKeyGenerator() {
            return keyGenerator;
        }
    }

    byte[] FILE_FORMAT_IDENTIFIER = "SafPass\ud83d\udd12".getBytes(StandardCharsets.UTF_8);

    SortedMap<Integer, FileVersionType> SUPPORTED_FILE_VERSIONS = Arrays.stream(FileVersionType.values())
            .collect(Collectors.toMap(FileVersionType::getVersion, Function.identity(), (version, duplicate) -> version, TreeMap::new));

    byte[] getKey();
}
