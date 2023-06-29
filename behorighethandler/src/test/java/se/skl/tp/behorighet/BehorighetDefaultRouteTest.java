package se.skl.tp.behorighet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.skl.tp.behorighet.util.TestTakDataDefines.DEFAULT_RECEIVER;
import static se.skl.tp.behorighet.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.NAMNRYMD_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.SENDER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.SENDER_2;

import java.net.URL;
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
import se.skltp.takcache.BehorigheterCache;
import se.skltp.takcache.TakCache;

public class BehorighetDefaultRouteTest {

  private static final String OLD_STYLE_DEFAULT_ROUTING_DELIMITER = "#";


  HsaCache hsaCache;

  @Mock
  BehorigheterCache behorighetCache;

  BehorighetHandlerImpl behorighetHandler;
  DefaultRoutingConfiguration defaultRoutingConfiguration;


  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    hsaCache = new HsaCacheImpl();
    URL url = getClass().getClassLoader().getResource("hsacache.xml");
    URL urlHsaRoot = getClass().getClassLoader().getResource("hsacachecomplementary.xml");
    hsaCache.init(url.getFile(), urlHsaRoot.getFile());
    defaultRoutingConfiguration = new DefaultRoutingConfigurationImpl();
    defaultRoutingConfiguration.setDelimiter(OLD_STYLE_DEFAULT_ROUTING_DELIMITER);
  }

  @Test
  public void testSimpleAuthorizonByDefaultRoute() throws Exception {

    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1)).thenReturn(false);
    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, DEFAULT_RECEIVER)).thenReturn(true);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorighetCache, defaultRoutingConfiguration);
    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1));
  }

  @Test
  public void testDefaultRouteShouldOnlyAuthorizeOnCorrectSenderAndNamnrymd() throws Exception {

    Mockito.when(behorighetCache.isAuthorized(SENDER_2, NAMNRYMD_2, RECEIVER_1)).thenReturn(false);
    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, DEFAULT_RECEIVER)).thenReturn(true);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorighetCache, defaultRoutingConfiguration);
    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_2, RECEIVER_1));
  }

  @Test
  public void testTraceLogAuthorizedByDefaultRouting() throws Exception {

    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2)).thenReturn(false);
    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, DEFAULT_RECEIVER)).thenReturn(true);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorighetCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2));
    assertEquals("receiver-2,(default),*",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }
}
