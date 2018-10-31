package se.skl.tp.vagval.util;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class DefaultRoutingUtilTest {

  @Test
  public void delimiterInAddressShouldGiveTrue() {
    Assert.assertTrue( DefaultRoutingUtil.useOldStyleDefaultRouting("VG#VE", "#"));
  }

  @Test
  public void emptyDelimiterShouldGiveFalse() {
    Assert.assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting("VG#VE", ""));
  }

  @Test
  public void nullDelimiterShouldGiveFalse() {
    Assert.assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting("VG#VE", null));
  }

  @Test
  public void nullReceiverShouldGiveFalse() {
    Assert.assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting(null, "#"));
  }

  @Test
  public void delimiterNotInAddressShouldGiveFalse() {
    Assert.assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting("VG", "#"));
  }

  @Test
  public void extractReceiverAdressesShouldSplitInCorrectOrder() {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAdresses("VG#VE", "#");
    Assert.assertEquals(2, addresses.size());
    Assert.assertEquals("VE", addresses.get(0));
    Assert.assertEquals("VG", addresses.get(1));
  }

  @Test
  public void missingVEShouldNotCauseCrash() {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAdresses("VG#", "#");
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals("VG", addresses.get(0));
  }

  @Test
  public void missingVGShouldNotCauseCrash() {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAdresses("#VE", "#");
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals("VE", addresses.get(0));
  }

  @Test
  public void sameStringShouldNotBeAdddedTwiceToList() {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAdresses("VE#VE", "#");
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals("VE", addresses.get(0));
  }

  @Test
  public void noDelimiterInAddresssShouldGiveOneInList() {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAdresses("12345", "#");
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals("12345", addresses.get(0));
  }
}
