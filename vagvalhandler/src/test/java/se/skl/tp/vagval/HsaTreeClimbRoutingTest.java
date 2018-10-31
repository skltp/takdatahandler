package se.skl.tp.vagval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static se.skl.tp.vagval.util.TestTakDataDefines.ADDRESS_2;
import static se.skl.tp.vagval.util.TestTakDataDefines.AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.vagval.util.TestTakDataDefines.CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.vagval.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.vagval.util.TestTakDataDefines.PARENT_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.vagval.util.TestTakDataDefines.RIV21;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skl.tp.hsa.cache.HsaCache;
import se.skl.tp.hsa.cache.HsaCacheImpl;
import se.skl.tp.vagval.logging.ThreadContextLogTrace;
import se.skltp.takcache.RoutingInfo;
import se.skltp.takcache.TakCache;

public class HsaTreeClimbRoutingTest {
  private String defaultRoutingDelimiter = "#";

  HsaCache hsaCache;

  @Mock
  TakCache takCache;

  VagvalHandlerImpl vagvalHandler;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);

    hsaCache = new HsaCacheImpl();
    URL url = getClass().getClassLoader().getResource("hsacache.xml");
    URL urlHsaRoot = getClass().getClassLoader().getResource("hsacachecomplementary.xml");
    hsaCache.init(url.getFile(), urlHsaRoot.getFile());
  }

  @Test
  public void testRoutingShouldBeFoundOnChildInHsaTree() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(new RoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(takCache.getRoutingInfo(anyString(), eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))
        .thenReturn(list);
    Mockito.when(takCache
        .getRoutingInfo(anyString(), AdditionalMatchers.not(eq(AUTHORIZED_RECEIVER_IN_HSA_TREE))))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE);
    assertEquals(1, routingInfoList.size());
    assertEquals(ADDRESS_2, routingInfoList.get(0).getAddress());
    assertEquals(RIV21, routingInfoList.get(0).getRivProfile());
  }

  @Test
  public void testTraceLoggingWhenRoutingFoundOnChildInHsaTree() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(new RoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(takCache.getRoutingInfo(anyString(), eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))
        .thenReturn(list);
    Mockito.when(takCache
        .getRoutingInfo(anyString(), AdditionalMatchers.not(eq(AUTHORIZED_RECEIVER_IN_HSA_TREE))))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE);
    assertEquals(1, routingInfoList.size());
    String logResult = ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_VAGVAL_TRACE);
    assertEquals("SE0000000001-1234,(parent)SE0000000002-1234,SE0000000003-1234", logResult);
  }

  @Test
  public void testRoutingShouldNotBeFoundOnParentInHsaTree() throws Exception {
    List<RoutingInfo> list = new ArrayList<>();
    list.add(new RoutingInfo(ADDRESS_2, RIV21));

    Mockito.when(takCache.getRoutingInfo(anyString(), eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))
        .thenReturn(list);
    Mockito.when(takCache
        .getRoutingInfo(anyString(), AdditionalMatchers.not(eq(AUTHORIZED_RECEIVER_IN_HSA_TREE))))
        .thenReturn(Collections.<RoutingInfo>emptyList());

    vagvalHandler = new VagvalHandlerImpl(hsaCache, takCache, defaultRoutingDelimiter);

    List<RoutingInfo> routingInfoList = vagvalHandler
        .getRoutingInfo(NAMNRYMD_1, PARENT_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE);
    assertTrue(routingInfoList.isEmpty());
  }

}
