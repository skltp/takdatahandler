package se.skl.tp.behorighet;

import static org.junit.jupiter.api.Assertions.*;
import static se.skl.tp.behorighet.util.TestTakDataDefines.DEFAULT_RECEIVER;
import static se.skl.tp.behorighet.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.NAMNRYMD_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.SENDER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.SENDER_2;

import java.net.URL;
import java.util.Objects;

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
import se.skltp.takcache.BehorigheterCache;

class BehorighetDefaultRouteTest {

  private static final String OLD_STYLE_DEFAULT_ROUTING_DELIMITER = "#";

  HsaCache hsaCache;

  @Mock
  BehorigheterCache behorighetCache;

  BehorighetHandlerImpl behorighetHandler;
  DefaultRoutingConfiguration defaultRoutingConfiguration;
  AutoCloseable mocks;


  @BeforeEach
  void beforeTest() {
    mocks = MockitoAnnotations.openMocks(this);
    hsaCache = new HsaCacheImpl();
    URL url = Objects.requireNonNull(getClass().getClassLoader().getResource("hsacache.xml"));
    URL urlHsaRoot = Objects.requireNonNull(getClass().getClassLoader().getResource("hsacachecomplementary.xml"));
    hsaCache.init(url.getFile(), urlHsaRoot.getFile());
    defaultRoutingConfiguration = new DefaultRoutingConfigurationImpl();
    defaultRoutingConfiguration.setDelimiter(OLD_STYLE_DEFAULT_ROUTING_DELIMITER);
  }

  @AfterEach
  void afterTest() throws Exception {
    mocks.close();
  }

  @Test
  void testSimpleAuthorizationByDefaultRoute() {

    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1)).thenReturn(false);
    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, DEFAULT_RECEIVER)).thenReturn(true);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorighetCache, defaultRoutingConfiguration);
    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1));
  }

  @Test
  void testDefaultRouteShouldOnlyAuthorizeOnCorrectSenderAndNamnrymd() {

    Mockito.when(behorighetCache.isAuthorized(SENDER_2, NAMNRYMD_2, RECEIVER_1)).thenReturn(false);
    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, DEFAULT_RECEIVER)).thenReturn(true);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorighetCache, defaultRoutingConfiguration);
    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_2, RECEIVER_1));
  }

  @Test
  void testTraceLogAuthorizedByDefaultRouting() {

    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2)).thenReturn(false);
    Mockito.when(behorighetCache.isAuthorized(SENDER_1, NAMNRYMD_1, DEFAULT_RECEIVER)).thenReturn(true);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorighetCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2));
    assertEquals("receiver-2,(default),*",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }
}
