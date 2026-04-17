package se.skl.tp.vagval.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DefaultRoutingUtilTest {

  @Test
  void delimiterInAddressShouldGiveTrue() {
    assertTrue( DefaultRoutingUtil.useOldStyleDefaultRouting("VG#VE", "#"));
  }

  @Test
  void emptyDelimiterShouldGiveFalse() {
    assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting("VG#VE", ""));
  }

  @Test
  void nullDelimiterShouldGiveFalse() {
    assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting("VG#VE", null));
  }

  @Test
  void nullReceiverShouldGiveFalse() {
    assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting(null, "#"));
  }

  @Test
  void delimiterNotInAddressShouldGiveFalse() {
    assertFalse( DefaultRoutingUtil.useOldStyleDefaultRouting("VG", "#"));
  }

  @Test
  void extractReceiverAddressesShouldSplitInCorrectOrder() {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAddresses("VG#VE", "#");
    assertEquals(2, addresses.size());
    assertEquals("VE", addresses.get(0));
    assertEquals("VG", addresses.get(1));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideEdgeCases")
  void extractReceiverAddressesShouldHandleEdgeCases(String testName, String address, String expectedAddress) {
    List<String> addresses = DefaultRoutingUtil.extractReceiverAddresses(address, "#");
    assertEquals(1, addresses.size());
    assertEquals(expectedAddress, addresses.get(0));
  }

  private static Stream<Arguments> provideEdgeCases() {
    return Stream.of(
      Arguments.of("missingVEShouldNotCauseCrash", "VG#", "VG"),
      Arguments.of("missingVGShouldNotCauseCrash", "#VE", "VE"),
      Arguments.of("sameStringShouldNotBeAddedTwiceToList", "VE#VE", "VE"),
      Arguments.of("noDelimiterInAddressShouldGiveOneInList", "12345", "12345")
    );
  }
}
