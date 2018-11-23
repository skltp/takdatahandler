package se.skl.tp.vagval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.skl.tp.vagval.util.RoutingInfoUtil.createRoutingInfo;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RECEIVER_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV20;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV21;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.hsa.cache.HsaCacheImpl;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.TakCache;


public class VagvalHandlerTest {

  private String defaultRoutingDelimiter = "#";

  HsaCache hsaCache;

  @Mock
  TakCache takCache;

  VagvalHandlerImpl vagvalHandler;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    hsaCache = new HsaCacheImpl();
  }

  @Test
  public void testOneRoutingInfoFound() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    Mockito.when(takCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.get(0).getAddress());
    assertEquals(RIV20, routingInfoList.get(0).getRivProfile());
  }

  @Test
  public void testTraceLoggingOneRoutingInfoFound() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    Mockito.when(takCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(1, routingInfoList.size());
    assertEquals(RECEIVER_1,
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));
  }

  @Test
  public void testTwoRoutingInfoFound() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(createRoutingInfo(ADDRESS_1, RIV20));
    list.add(createRoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(takCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1)).thenReturn(list);

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertEquals(2, routingInfoList.size());
    assertEquals(ADDRESS_1, routingInfoList.get(0).getAddress());
    assertEquals(RIV20, routingInfoList.get(0).getRivProfile());
    assertEquals(ADDRESS_2, routingInfoList.get(1).getAddress());
    assertEquals(RIV21, routingInfoList.get(1).getRivProfile());
  }

  @Test
  public void testNoRoutingInfoFound() throws Exception {
    Mockito.when(takCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertTrue(routingInfoList.isEmpty());
  }

  @Test
  public void testTraceLoggingNoRoutingInfoFound() throws Exception {
    Mockito.when(takCache.getRoutingInfo(NAMNRYMD_1, RECEIVER_1))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler.getRoutingInfo(NAMNRYMD_1, RECEIVER_1);
    assertTrue(routingInfoList.isEmpty());
    assertEquals("receiver-1,(parent)SE,(default)*",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE));
  }


}
