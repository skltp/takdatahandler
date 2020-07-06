package se.skl.tp.vagval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static se.skl.tp.vagval.util.RoutingInfoUtil.createRoutingInfo;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_1_DEFAULT_RECEIVER_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_2_DEFAULT_RECEIVER_3;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_3_DEFAULT_RECEIVER_4;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV21;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skl.tp.DefaultRoutingConfiguration;
import se.skl.tp.DefaultRoutingConfigurationImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.hsa.cache.HsaCacheImpl;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.VagvalCache;

public class OldStyleDefaultRoutingTest {
  HsaCache hsaCache;

  @Mock
  VagvalCache vagvalCache;

  VagvalHandlerImpl vagvalHandler;
  DefaultRoutingConfiguration defaultRoutingConfiguration;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    hsaCache = new HsaCacheImpl();
    defaultRoutingConfiguration = new DefaultRoutingConfigurationImpl();
    defaultRoutingConfiguration.setDelimiter("#");
  }

  @Test
  public void testRoutingByOldStyleDefaultRouting() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(vagvalCache.getRoutingInfo(anyString(), eq(RECEIVER_2))).thenReturn(list);
    Mockito.when(vagvalCache.getRoutingInfo(anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration );

    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_2, routingInfoList.get(0).getAddress());
    assertEquals(RIV21, routingInfoList.get(0).getRivProfile());

    routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_2_DEFAULT_RECEIVER_3);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_2, routingInfoList.get(0).getAddress());
    assertEquals(RIV21, routingInfoList.get(0).getRivProfile());

    routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_3_DEFAULT_RECEIVER_4);
    assertTrue(routingInfoList.isEmpty());
  }

  @Test
  public void testTraceLoggingWhenRoutingByOldStyleDefaultRouting() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(vagvalCache.getRoutingInfo(anyString(), eq(RECEIVER_2))).thenReturn(list);
    Mockito.when(vagvalCache.getRoutingInfo(anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration );

    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2);
    assertEquals(1, routingInfoList.size());
    assertEquals("(leaf)receiver-2",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));

    routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_2_DEFAULT_RECEIVER_3);
    assertEquals(1, routingInfoList.size());
    assertEquals("(leaf)receiver-3,receiver-2",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));

    routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_3_DEFAULT_RECEIVER_4);
    assertTrue(routingInfoList.isEmpty());
    assertEquals("(leaf)receiver-4,receiver-3",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  public void testIsNotAuthorizedByOldStyleDefaultRoutingWhenItsDisabled() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(vagvalCache.getRoutingInfo(anyString(), eq(RECEIVER_2))).thenReturn(list);
    Mockito.when(vagvalCache.getRoutingInfo(anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    DefaultRoutingConfiguration defaultRoutingConfiguration = new DefaultRoutingConfigurationImpl();
    defaultRoutingConfiguration.setDelimiter("");
    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);
    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2);
    assertTrue(routingInfoList.isEmpty());
  }

}
