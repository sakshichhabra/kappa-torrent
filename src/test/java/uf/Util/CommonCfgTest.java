package uf.Util;

import org.junit.Assert;
import org.junit.Test;
import uf.CommonCfg;
import uf.Constants;

public class CommonCfgTest {
    @Test
    public void readCfgFile(){
        CommonCfg commonCfg = CommonCfg.from(Constants.testCommonCfgPath);
        Assert.assertEquals(commonCfg.NumberOfPreferredNeighbors, 2);
        Assert.assertEquals(commonCfg.UnchokingInterval, 5);
        Assert.assertEquals(commonCfg.OptimisticUnchokingInterval, 15);
        Assert.assertEquals(commonCfg.FileName, "TheFile.dat");
        Assert.assertEquals(commonCfg.FileSize, 10000232);
        Assert.assertEquals((commonCfg.PieceSize), 32768);
    }

}