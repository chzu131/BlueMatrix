package com.BlueMatrix.tools;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/10/11 0011.
 */
public class DataMode {

    static int row = 7;
    static int col = 4;

    public static int[] modeNull = {
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    };

    public static int[] modeA = {
            0x6,
            0x9,
            0x9,
            0xf,
            0x9,
            0x9,
            0x9
    };

    public static int[] modeB = {
            0xe,
            0x9,
            0x9,
            0xf,
            0x9,
            0x9,
            0xe
    };

    public static int[] modeC = {
            0x6,
            0x9,
            0x8,
            0x8,
            0x8,
            0x9,
            0x6
    };

    public static int[] modeD = {
            0xE,
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0xE
    };

    public static int[] modeE = {
            0xF,
            0x8,
            0x8,
            0xE,
            0x8,
            0x8,
            0xF
    };

    public static int[] modeF = {
            0xF,
            0x8,
            0x8,
            0xE,
            0x8,
            0x8,
            0x8
    };

    public static int[] modeG = {
            0x6,
            0x9,
            0x8,
            0x8,
            0xB,
            0x9,
            0x7
    };

    public static int[] modeH = {
            0x9,
            0x9,
            0x9,
            0xF,
            0x9,
            0x9,
            0x9
    };

    public static int[] modeI = {
            0xE,
            0x4,
            0x4,
            0x4,
            0x4,
            0x4,
            0xE
    };

    public static int[] modeJ = {
            0x7,
            0x2,
            0x2,
            0x2,
            0xA,
            0xA,
            0x6
    };

    public static int[] modeK = {
            0x9,
            0xA,
            0xC,
            0x8,
            0xC,
            0xA,
            0x9
    };

    public static int[] modeL = {
            0x8,
            0x8,
            0x8,
            0x8,
            0x8,
            0x8,
            0xF
    };

    public static int[] modeM = {
            0x9,
            0xF,
            0x9,
            0x9,
            0x9,
            0x9,
            0x9
    };

    public static int[] modeN = {
            0x9,
            0x9,
            0xD,
            0xB,
            0x9,
            0x9,
            0x9
    };

    public static int[] modeO = {
            0x6,
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0x6
    };

    public static int[] modeP = {
            0xE,
            0x9,
            0x9,
            0xE,
            0x8,
            0x8,
            0x8
    };

    public static int[] modeQ = {
            0x6,
            0x9,
            0x9,
            0x9,
            0x9,
            0x6,
            0x1
    };

    public static int[] modeR = {
            0xE,
            0x9,
            0x9,
            0xE,
            0xC,
            0xA,
            0x9
    };

    public static int[] modeS = {
            0x6,
            0x9,
            0x8,
            0x6,
            0x1,
            0x9,
            0x6
    };

    public static int[] modeT = {
            0xF,
            0x4,
            0x4,
            0x4,
            0x4,
            0x4,
            0x4
    };

    public static int[] modeU = {
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0x6
    };

    public static int[] modeV = {
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0x6,
            0x4
    };

    public static int[] modeW = {
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0xF,
            0x9
    };

    public static int[] modeX = {
            0x9,
            0x9,
            0x9,
            0x6,
            0x9,
            0x9,
            0x9
    };

    public static int[] modeY = {
            0x9,
            0x9,
            0x9,
            0x6,
            0x4,
            0x4,
            0x4
    };

    public static int[] modeZ = {
            0xF,
            0x1,
            0x2,
            0x4,
            0x8,
            0x8,
            0xF
    };

    public static int[] mode0 = {
            0xf,
            0x9,
            0x9,
            0x9,
            0x9,
            0x9,
            0xf
    };

    public static int[] mode1 = {
            0x2,
            0x2,
            0x2,
            0x2,
            0x2,
            0x2,
            0x2
    };

    public static int[] mode2 = {
            0xf,
            0x1,
            0x1,
            0xf,
            0x8,
            0x8,
            0xf
    };

    public static int[] mode3 = {
            0xe,
            0x1,
            0x1,
            0xf,
            0x1,
            0x1,
            0xe
    };

    public static int[] mode4 = {
            0x9,
            0x9,
            0x9,
            0xf,
            0x1,
            0x1,
            0x1
    };

    public static int[] mode5 = {
            0xf,
            0x8,
            0x8,
            0xf,
            0x1,
            0x1,
            0xf
    };

    public static int[] mode6 = {
            0xf,
            0x8,
            0x8,
            0xf,
            0x9,
            0x9,
            0xf
    };

    public static int[] mode7 = {
            0xf,
            0x1,
            0x1,
            0x1,
            0x1,
            0x1,
            0x1
    };

    public static int[] mode8 = {
            0xf,
            0x9,
            0x9,
            0xf,
            0x9,
            0x9,
            0xf
    };

    public static int[] mode9 = {
            0xf,
            0x9,
            0x9,
            0xf,
            0x1,
            0x1,
            0x1
    };

    public static int[] modeAdd = {
            0x0,
            0x2,
            0x2,
            0x7,
            0x2,
            0x2,
            0x0
    };

    public static int[] modeLess = {
            0x0,
            0x0,
            0x0,
            0xf,
            0x0,
            0x0,
            0x0
    };

    public static int[] modeMultiply = {
            0x0,
            0x2,
            0x2,
            0x7,
            0x2,
            0x5,
            0x0
    };

    public static int[] modeExcept = {
            0x0,
            0x1,
            0x2,
            0x4,
            0x8,
            0x0,
            0x0
    };

    public static int[] modeEqual = {
            0x0,
            0x0,
            0xf,
            0x0,
            0xf,
            0x0,
            0x0
    };

    public static int[] modeSigh = {
            0x2,
            0x2,
            0x2,
            0x2,
            0x2,
            0x0,
            0x2
    };


    public static HashMap<String, boolean[][]> sModeMap = new HashMap<String, boolean[][]>();

    static DataMode mInstance = null;

    private DataMode() {
        initModeMap();
    }

    static public DataMode getInstance() {
        if (mInstance == null) {
            mInstance = new DataMode();
        }
        return mInstance;
    }

    public HashMap<String, boolean[][]> getModeMap() {
        return sModeMap;
    }

    public boolean[][] mergerBooleans(List<boolean[][]> booleansList) {
        if (booleansList == null) {
            return null;
        }
        boolean[][] booleanData = new boolean[12][booleansList.size() * (col + 2)];
        for (int l = 0; l < booleansList.size(); l++) {
            boolean[][] booleans = booleansList.get(l);
            for (int i = 0; i < col; i++) {
                for (int j = 0; j < row; j++) {
                    booleanData[j + 2][1 + i + l * (col + 1)] = booleans[j][i];
                }
            }
        }
        return booleanData;
    }

    private void initModeMap() {
        sModeMap.put(" ", intToBooleans(modeNull));
        sModeMap.put("A", intToBooleans(modeA));
        sModeMap.put("B", intToBooleans(modeB));
        sModeMap.put("C", intToBooleans(modeC));
        sModeMap.put("D", intToBooleans(modeD));
        sModeMap.put("E", intToBooleans(modeE));
        sModeMap.put("F", intToBooleans(modeF));
        sModeMap.put("G", intToBooleans(modeG));
        sModeMap.put("H", intToBooleans(modeH));
        sModeMap.put("I", intToBooleans(modeI));
        sModeMap.put("J", intToBooleans(modeJ));
        sModeMap.put("K", intToBooleans(modeK));
        sModeMap.put("L", intToBooleans(modeL));
        sModeMap.put("M", intToBooleans(modeM));
        sModeMap.put("N", intToBooleans(modeN));
        sModeMap.put("O", intToBooleans(modeO));
        sModeMap.put("P", intToBooleans(modeP));
        sModeMap.put("Q", intToBooleans(modeQ));
        sModeMap.put("R", intToBooleans(modeR));
        sModeMap.put("S", intToBooleans(modeS));
        sModeMap.put("T", intToBooleans(modeT));
        sModeMap.put("U", intToBooleans(modeU));
        sModeMap.put("V", intToBooleans(modeV));
        sModeMap.put("W", intToBooleans(modeW));
        sModeMap.put("X", intToBooleans(modeX));
        sModeMap.put("Y", intToBooleans(modeY));
        sModeMap.put("Z", intToBooleans(modeZ));

        sModeMap.put("a", intToBooleans(modeA));
        sModeMap.put("b", intToBooleans(modeB));
        sModeMap.put("c", intToBooleans(modeC));
        sModeMap.put("d", intToBooleans(modeD));
        sModeMap.put("e", intToBooleans(modeE));
        sModeMap.put("f", intToBooleans(modeF));
        sModeMap.put("g", intToBooleans(modeG));
        sModeMap.put("h", intToBooleans(modeH));
        sModeMap.put("i", intToBooleans(modeI));
        sModeMap.put("j", intToBooleans(modeJ));
        sModeMap.put("k", intToBooleans(modeK));
        sModeMap.put("l", intToBooleans(modeL));
        sModeMap.put("m", intToBooleans(modeM));
        sModeMap.put("n", intToBooleans(modeN));
        sModeMap.put("o", intToBooleans(modeO));
        sModeMap.put("p", intToBooleans(modeP));
        sModeMap.put("q", intToBooleans(modeQ));
        sModeMap.put("r", intToBooleans(modeR));
        sModeMap.put("s", intToBooleans(modeS));
        sModeMap.put("t", intToBooleans(modeT));
        sModeMap.put("u", intToBooleans(modeU));
        sModeMap.put("v", intToBooleans(modeV));
        sModeMap.put("w", intToBooleans(modeW));
        sModeMap.put("x", intToBooleans(modeX));
        sModeMap.put("y", intToBooleans(modeY));
        sModeMap.put("z", intToBooleans(modeZ));

        sModeMap.put("1", intToBooleans(mode1));
        sModeMap.put("2", intToBooleans(mode2));
        sModeMap.put("3", intToBooleans(mode3));
        sModeMap.put("4", intToBooleans(mode4));
        sModeMap.put("5", intToBooleans(mode5));
        sModeMap.put("6", intToBooleans(mode6));
        sModeMap.put("7", intToBooleans(mode7));
        sModeMap.put("8", intToBooleans(mode8));
        sModeMap.put("9", intToBooleans(mode9));
        sModeMap.put("0", intToBooleans(mode0));

        sModeMap.put("+", intToBooleans(modeAdd));
        sModeMap.put("-", intToBooleans(modeLess));
        sModeMap.put("*", intToBooleans(modeMultiply));
        sModeMap.put("/", intToBooleans(modeExcept));
        sModeMap.put("=", intToBooleans(modeEqual));
        sModeMap.put("!", intToBooleans(modeSigh));
    }

    static public boolean[][] intToBooleans(int[] n) {
        boolean [][] booleanData = new boolean[row][col];
        for (int i = 0; i < row; i++) {
            booleanData[i] = intToBoolean(n[i]);
        }
        return booleanData;
    }
    
    static public boolean [] intToBoolean(int n) {
        boolean [] booleanData = new boolean[col];
        for (int i = col - 1; i >= 0; i--) {
            int a = n >> i;
            booleanData[col - 1 - i] = a % 2 != 0;
        }
        return booleanData;
    }
}
