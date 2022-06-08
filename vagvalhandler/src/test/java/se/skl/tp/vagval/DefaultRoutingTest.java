package se.skl.tp.vagval;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static se.skl.tp.vagval.util.RoutingInfoUtil.createRoutingInfo;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_3;
import static se.skl.tp.vagval.util.TestTakDataDefines.AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.vagval.util.TestTakDataDefines.CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.vagval.util.TestTakDataDefines.DEFAULT_RECEIVER;
import static se.skl.tp.vagval.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV20;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV21;

import java.net.URL;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
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

public class DefaultRoutingTest {

  HsaCache hsaCache;

  @Mock
  VagvalCache vagvalCache;

  VagvalHandlerImpl vagvalHandler;
  DefaultRoutingConfiguration defaultRoutingConfiguration;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    hsaCache = new HsaCacheImpl();
    URL url = getClass().getClassLoader().getResource("hsacache.xml");
    URL urlHsaRoot = getClass().getClassLoader().getResource("hsacachecomplementary.xml");
    hsaCache.init(url.getFile(), urlHsaRoot.getFile());
    defaultRoutingConfiguration = new DefaultRoutingConfigurationImpl();
  }

  @Test
  public void testOneRoutingInfoFoundByDefaultReceiver() throws Exception {
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, DEFAULT_RECEIVER))
        .thenReturn(asList(createRoutingInfo(ADDRESS_2, RIV20)));

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration );

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_2, routingInfoList.get(0).getAddress());
    assertEquals(RIV20, routingInfoList.get(0).getRivProfile());
  }

  @Test
  public void testTraceLogWhenOneRoutingInfoFoundByDefaultReceiver() throws Exception {
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, DEFAULT_RECEIVER))
        .thenReturn(asList(createRoutingInfo(ADDRESS_2, RIV20)));

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_2);
    assertEquals(1, routingInfoList.size());
    assertEquals("receiver-2,(parent)SE,(default)*",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  public void testExplicitReceiverUsedBeforeDefaultRouting() throws Exception {
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, DEFAULT_RECEIVER))
        .thenReturn(asList(createRoutingInfo(ADDRESS_2, RIV20)));
    Mockito.when(vagvalCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1))
        .thenReturn(asList(createRoutingInfo(ADDRESS_1, RIV20)));

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.get(0).getAddress());
    assertEquals(RIV20, routingInfoList.get(0).getRivProfile());
  }

  @Test
  public void testFoundByHsaClimbingUsedBeforeDefaultRouting() throws Exception {
    Mockito.when(vagvalCache.getRoutingInfo(anyString(), eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))
        .thenReturn(asList(createRoutingInfo(ADDRESS_2, RIV21)));
    Mockito.when(vagvalCache.getRoutingInfo(anyString(), eq(DEFAULT_RECEIVER)))
        .thenReturn(asList(createRoutingInfo(ADDRESS_3, RIV21)));

    vagvalHandler = new VagvalHandlerImpl(hsaCache, vagvalCache, defaultRoutingConfiguration);

    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_2, routingInfoList.get(0).getAddress());
    assertEquals(RIV21, routingInfoList.get(0).getRivProfile());
  }

}
