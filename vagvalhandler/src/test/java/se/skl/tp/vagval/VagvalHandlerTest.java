package se.skl.tp.vagval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.skl.tp.vagval.util.RoutingInfoUtil.createRoutingInfo;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV20;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV21;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skl.tp.DefaultRoutingConfiguration;
import se.skl.tp.DefaultRoutingConfigurationImpl;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.hsa.cache.HsaCacheImpl;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.VagvalCache;


class VagvalHandlerTest {

  HsaCache hsaCache;

  @Mock
  VagvalCache vagvalCache;

  VagvalHandlerImpl vagvalHandler;
  DefaultRoutingConfiguration defaultRoutingConfiguration;

  AutoCloseable mocks;

  @BeforeEach
  void beforeTest() {
    mocks = MockitoAnnotations.openMocks(this);
    hsaCache = new HsaCacheImpl();
    defaultRoutingConfiguration = new DefaultRoutingConfigurationImpl();
    defaultRoutingConfiguration.setDelimiter("#");
  }

  @AfterEach
  void afterTest() throws Exception {
    mocks.close();
  }

  @Test
  void testOneRoutingInfoFound() {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.getFirst().getAddress());
    assertEquals(RIV20, routingInfoList.getFirst().getRivProfile());
  }

  @Test
  void testOneRoutingInfoNoHsaCache() {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(vagvalCache);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.getFirst().getAddress());
    assertEquals(RIV20, routingInfoList.getFirst().getRivProfile());
  }

  @Test
  void testTraceLoggingOneRoutingInfoFound() {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(RECEIVER_1,
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  void testTwoRoutingInfoFound() {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    list.add(createRoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(2, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.get(0).getAddress());
    assertEquals(RIV20, routingInfoList.get(0).getRivProfile());
    assertEquals(ADDRESS_2, routingInfoList.get(1).getAddress());
    assertEquals(RIV21, routingInfoList.get(1).getRivProfile());
  }

  @Test
  void testNoRoutingInfoFound() {
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1))
        .thenReturn(Collections.emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertTrue(routingInfoList.isEmpty());
  }

  @Test
  void testTraceLoggingNoRoutingInfoFound() {
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1))
        .thenReturn(Collections.emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertTrue(routingInfoList.isEmpty());
    assertEquals("receiver-1,(parent),SE,(default),*",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  void testTwoParameterConstructor() {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_2))
        .thenReturn(Collections.emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.getFirst().getAddress());
    assertEquals(RIV20, routingInfoList.getFirst().getRivProfile());
    List<RoutingInfo> emptyList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_2);
    assertTrue(emptyList.isEmpty());
  }
}
