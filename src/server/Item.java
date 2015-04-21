/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.HashMap;

/**
 *
 * @author kevinyu
 */
public class Item {
    public static final int ITEM_KIND_NUMBER = 10;
    public static final int ITEM_HONEY = 0;
    public static final int ITEM_HERB = 1;
    public static final int ITEM_CLAY = 2;
    public static final int ITEM_MINERAL = 3;
    public static final int ITEM_POTION = 4;
    public static final int ITEM_INCENSE = 5;
    public static final int ITEM_GEMS = 6;
    public static final int ITEM_ELIXIR = 7;
    public static final int ITEM_CRYSTAL = 8;
    public static final int ITEM_STONE = 9;
    private static final HashMap<String,Integer> sMap;
    
    private static final int[][] sCombinationMatrix;
    private static final int CANNOT_COMBINE = -1;
    
    static {
        
        sMap = new HashMap<>();
        sMap.put("R11", ITEM_HONEY);
        sMap.put("R12", ITEM_HERB);
        sMap.put("R13", ITEM_CLAY);
        sMap.put("R14", ITEM_MINERAL);
        sMap.put("R21", ITEM_POTION);
        sMap.put("R22", ITEM_INCENSE);
        sMap.put("R23", ITEM_GEMS);
        sMap.put("R31", ITEM_ELIXIR);
        sMap.put("R32", ITEM_CRYSTAL);
        sMap.put("R41", ITEM_STONE);
        
        sCombinationMatrix = new int[10][10];
        for (int i=0;i<10;i++) {
            for (int j=0;j<10;j++) {
                sCombinationMatrix[i][j] = CANNOT_COMBINE;
            }
        }
        
        //level 1 combination
        sCombinationMatrix[ITEM_HONEY][ITEM_HERB] = ITEM_POTION;
        sCombinationMatrix[ITEM_HERB][ITEM_CLAY] = ITEM_INCENSE;
        sCombinationMatrix[ITEM_CLAY][ITEM_MINERAL] = ITEM_GEMS;
        
        //level 2 combination
        sCombinationMatrix[ITEM_POTION][ITEM_INCENSE] = ITEM_ELIXIR;
        sCombinationMatrix[ITEM_INCENSE][ITEM_GEMS] = ITEM_CRYSTAL;
        
        //level 3 combination
        sCombinationMatrix[ITEM_ELIXIR][ITEM_CRYSTAL] = ITEM_STONE;
        
    }
    
    public static int getCombineResult(int itemCode1,int itemCode2) throws Exception {
        if (sCombinationMatrix[itemCode1][itemCode2] == CANNOT_COMBINE)
            throw new Exception("Cannot combine these two materials");
        return sCombinationMatrix[itemCode1][itemCode2];
    }
    
    public static int getItemNumberCode(String stringCode) {
        return sMap.get(stringCode);
    }
}
