package com.example.backend.numbering;

import java.math.BigInteger;

public class ParagraphNumberingInfo {
    /**
     * 段落编号信息的数据类
     */

        private final BigInteger numId;
        private final int level;

        public ParagraphNumberingInfo(BigInteger numId, int level) {
            this.numId = numId;
            this.level = level;
        }

        public BigInteger getNumId() {
            return numId;
        }

        public int getLevel() {
            return level;
        }

}
