package se.skl.tp.behorighet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static se.skl.tp.behorighet.util.TestTakDataDefines.AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.behorighet.util.TestTakDataDefines.CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.behorighet.util.TestTakDataDefines.NAMNRYMD_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.NAMNRYMD_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.PARENT_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_1_DEFAULT_RECEIVER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_1_DEFAULT_RECEIVER_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_2_DEFAULT_RECEIVER_3;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_2_RECEIVER_3_RECIEVER_4;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_3_DEFAULT_RECEIVER_4;
import static se.skl.tp.behorighet.util.TestTakDataDefines.RECEIVER_4_RECEIVER_3_RECIEVER_2;
import static se.skl.tp.behorighet.util.TestTakDataDefines.SENDER_1;
import static se.skl.tp.behorighet.util.TestTakDataDefines.SENDER_2;

import java.net.URL;
import java.util.Arrays;
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
import se.skltp.takcache.BehorigheterCache;


public class BehorighetHandlerTest {

  private static final String DEFAULT_ROUTING_DELIMITER = "#";


  HsaCache hsaCache;

  @Mock
  BehorigheterCache behorigheterCache;

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
    defaultRoutingConfiguration.setDelimiter(DEFAULT_ROUTING_DELIMITER);
  }

  @Test
  public void testSimpleAuthorizon() throws Exception {

    Mockito.when(behorigheterCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1)).thenReturn(true);
    Mockito.when(behorigheterCache.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1)).thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1));
    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1));
  }

  @Test
  public void testSimpleAuthorizonNoHsaCache() throws Exception {

    Mockito.when(behorigheterCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1)).thenReturn(true);
    Mockito.when(behorigheterCache.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1)).thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(behorigheterCache);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1));
    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1));
  }

  @Test
  public void testTraceLogForSimpleAuthorizon() throws Exception {

    Mockito.when(behorigheterCache.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1)).thenReturn(true);
    Mockito.when(behorigheterCache.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1)).thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1));
    assertEquals(RECEIVER_1,
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }

  @Test
  public void testTraceLogNotAuthorized() throws Exception {

    Mockito.when(behorigheterCache.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1)).thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1));
    assertEquals("receiver-1,(parent)SE,(default)*",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }

  @Test
  public void testIsAuthorizedByHsaTreeClimbing() throws Exception {
    Mockito
        .when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))
        .thenReturn(true);
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(),
        AdditionalMatchers.not(eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))).thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(
        behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, AUTHORIZED_RECEIVER_IN_HSA_TREE));
    assertTrue(behorighetHandler
        .isAuthorized(SENDER_1, NAMNRYMD_1, CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE));
    assertFalse(behorighetHandler
        .isAuthorized(SENDER_1, NAMNRYMD_1, PARENT_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE));
  }

  @Test
  public void testTraceLogWhenAuthorizedByHsaTreeClimbing() throws Exception {
    Mockito
        .when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))
        .thenReturn(true);
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(),
        AdditionalMatchers.not(eq(AUTHORIZED_RECEIVER_IN_HSA_TREE)))).thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler
        .isAuthorized(SENDER_1, NAMNRYMD_1, CHILD_OF_AUTHORIZED_RECEIVER_IN_HSA_TREE));
    assertEquals("SE0000000001-1234,(parent)SE0000000002-1234,SE0000000003-1234",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }

  @Test
  public void testIsAuthorizedByOldStyleDefaultRouting() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2));
    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2_DEFAULT_RECEIVER_3));
    assertFalse(
        behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_3_DEFAULT_RECEIVER_4));
  }

  @Test
  public void testIsAuthorizedByOldStyleDefaultRoutingWhenVEEqualsVG() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_1))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_1))))
        .thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);
    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_1));
  }


  @Test
  public void testIsAuthorizedByOldStyleDefaultRoutingAndForSenderAndContract() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    defaultRoutingConfiguration.setAllowedContracts(Arrays.asList(NAMNRYMD_1));
    defaultRoutingConfiguration.setAllowedSenderIds(Arrays.asList(SENDER_1));
    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2));
    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2_DEFAULT_RECEIVER_3));
    assertFalse(
        behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_3_DEFAULT_RECEIVER_4));
  }


  @Test
  public void testOldStyleDefaultRoutingNotAllowedContract() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    defaultRoutingConfiguration.setAllowedContracts(Arrays.asList(NAMNRYMD_1));
    defaultRoutingConfiguration.setAllowedSenderIds(Arrays.asList(SENDER_1));
    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertFalse(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_2, RECEIVER_1_DEFAULT_RECEIVER_2));
    assertFalse(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_2, RECEIVER_2_DEFAULT_RECEIVER_3));
  }

  @Test
  public void testOldStyleDefaultRoutingNotAllowedSenders() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    defaultRoutingConfiguration.setAllowedContracts(Arrays.asList(NAMNRYMD_1));
    defaultRoutingConfiguration.setAllowedSenderIds(Arrays.asList(SENDER_1));
    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2));
    assertFalse(behorighetHandler.isAuthorized(SENDER_2, NAMNRYMD_1, RECEIVER_2_DEFAULT_RECEIVER_3));
  }

  @Test
  public void testOldStyleDefaulRoutingtMultipleSeperatorsNotAllowed() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertFalse(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2_RECEIVER_3_RECIEVER_4));
    assertFalse(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_4_RECEIVER_3_RECIEVER_2));
  }

  @Test
  public void testTraceLogWhenAuthorizedByOldStyleDefaultRouting() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2));
    assertEquals("(leaf)receiver-2",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));

    assertTrue(behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_2_DEFAULT_RECEIVER_3));
    assertEquals("(leaf)receiver-3,receiver-2",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));

    assertFalse(
        behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_3_DEFAULT_RECEIVER_4));
    assertEquals("(leaf)receiver-4,receiver-3",
        ThreadContextLogTrace.get(ThreadContextLogTrace.ROUTER_RESOLVE_ANROPSBEHORIGHET_TRACE));
  }


  @Test
  public void testIsNotAuthorizedByOldStyleDefaultRoutingWhenItsDisabled() throws Exception {
    Mockito.when(behorigheterCache.isAuthorized(anyString(), anyString(), eq(RECEIVER_2))).thenReturn(true);
    Mockito.when(
        behorigheterCache.isAuthorized(anyString(), anyString(), AdditionalMatchers.not(eq(RECEIVER_2))))
        .thenReturn(false);

    defaultRoutingConfiguration.setDelimiter("");
    behorighetHandler = new BehorighetHandlerImpl(hsaCache, behorigheterCache, defaultRoutingConfiguration);

    assertFalse(
        behorighetHandler.isAuthorized(SENDER_1, NAMNRYMD_1, RECEIVER_1_DEFAULT_RECEIVER_2));
  }


}