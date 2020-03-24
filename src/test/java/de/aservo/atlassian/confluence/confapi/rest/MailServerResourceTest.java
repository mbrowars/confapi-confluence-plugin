package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.mail.MailException;
import com.atlassian.mail.server.DefaultTestPopMailServerImpl;
import com.atlassian.mail.server.DefaultTestSmtpMailServerImpl;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.OtherTestPopMailServerImpl;
import com.atlassian.mail.server.OtherTestSmtpMailServerImpl;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.PopMailServerBean;
import de.aservo.atlassian.confapi.model.SmtpMailServerBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static de.aservo.atlassian.confapi.junit.ResourceAssert.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServerResourceTest {

    @Mock
    private MailServerManager mailServerManager;

    private MailServerResource mailServerResource;

    @Before
    public void setup() {
        mailServerResource = new MailServerResource(mailServerManager);
    }

    @Test
    public void testResourcePath() {
        assertResourcePath(mailServerResource, ConfAPI.MAIL);
    }

    @Test
    public void testGetSmtpMailServerPath() {
        assertResourceMethodGet(mailServerResource, ConfAPI.MAIL_SMTP, "getSmtpMailServer");
    }

    @Test
    public void testGetSmtpMailServer() {
        final SMTPMailServer smtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(true).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(smtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final Response response = mailServerResource.getSmtpMailServer();
        final SmtpMailServerBean bean = (SmtpMailServerBean) response.getEntity();

        assertEquals(smtpMailServer.getName(), bean.getName());
        assertEquals(smtpMailServer.getDescription(), bean.getDescription());
        assertEquals(smtpMailServer.getHostname(), bean.getHost());
        assertEquals(smtpMailServer.getTimeout(), bean.getTimeout());
        assertEquals(smtpMailServer.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
        assertEquals(smtpMailServer.getDefaultFrom(), bean.getFrom());
        assertEquals(smtpMailServer.getPrefix(), bean.getPrefix());
        assertEquals(smtpMailServer.isTlsRequired(), bean.isTls());
        assertEquals(smtpMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(smtpMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetSmtpMailServerNoContent() {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();

        final Response response = mailServerResource.getSmtpMailServer();
        final ErrorCollection bean = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Status.NO_CONTENT.getStatusCode());
        assertTrue(bean.hasAnyErrors());
    }

    @Test
    public void testPutSmtpMailServerPath() {
        assertResourceMethodPut(mailServerResource, ConfAPI.MAIL_SMTP, "putSmtpMailServer", SmtpMailServerBean.class);
    }

    @Test
    public void testPutSmtpMaiLServerUpdate() throws Exception {
        final SMTPMailServer defaultSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(true).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(defaultSmtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer updateSmtpMailServer = new OtherTestSmtpMailServerImpl();
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(updateSmtpMailServer);
        final Response response = mailServerResource.putSmtpMailServer(requestSmtpMailServerBean);
        final SmtpMailServerBean responseSmtpMailServerBean = (SmtpMailServerBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).update(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(SmtpMailServerBean.from(updateSmtpMailServer), SmtpMailServerBean.from(smtpMailServer));
        assertEquals(requestSmtpMailServerBean, responseSmtpMailServerBean);
    }

    @Test
    public void testPutSmtpMaiLServerCreate() throws Exception {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(createSmtpMailServer);
        final Response response = mailServerResource.putSmtpMailServer(requestSmtpMailServerBean);
        final SmtpMailServerBean responseSmtpMailServerBean = (SmtpMailServerBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(SmtpMailServerBean.from(createSmtpMailServer), SmtpMailServerBean.from(smtpMailServer));
        assertEquals(requestSmtpMailServerBean, responseSmtpMailServerBean);
    }

    @Test
    public void testPutSmtpMaiLServerWithoutPort() throws Exception {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        createSmtpMailServer.setPort(null);

        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(createSmtpMailServer);
        final Response response = mailServerResource.putSmtpMailServer(requestSmtpMailServerBean);
        final SmtpMailServerBean responseSmtpMailServerBean = (SmtpMailServerBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(createSmtpMailServer.getMailProtocol().getDefaultPort(), smtpMailServer.getPort());
        assertEquals(requestSmtpMailServerBean, responseSmtpMailServerBean);
    }

    @Test
    public void testPutSmtpMaiLServerException() throws Exception {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();
        doThrow(new MailException("SMTP test exception")).when(mailServerManager).create(any());

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(createSmtpMailServer);
        final Response response = mailServerResource.putSmtpMailServer(requestSmtpMailServerBean);
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

    @Test
    public void testGetPopMailServerPath() {
        assertResourceMethodGet(mailServerResource, ConfAPI.MAIL_POP, "getPopMailServer");
    }

    @Test
    public void testGetPopMailServer() {
        final PopMailServer popMailServer = new DefaultTestPopMailServerImpl();
        doReturn(popMailServer).when(mailServerManager).getDefaultPopMailServer();

        final Response response = mailServerResource.getPopMailServer();
        final PopMailServerBean bean = (PopMailServerBean) response.getEntity();

        assertEquals(popMailServer.getName(), bean.getName());
        assertEquals(popMailServer.getDescription(), bean.getDescription());
        assertEquals(popMailServer.getHostname(), bean.getHost());
        assertEquals(popMailServer.getTimeout(), bean.getTimeout());
        assertEquals(popMailServer.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
        assertEquals(popMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(popMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetPopMailServerNoContent() {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();

        final Response response = mailServerResource.getPopMailServer();
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Status.NO_CONTENT.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

    @Test
    public void testPutPopMailServerPath() {
        assertResourceMethodPut(mailServerResource, ConfAPI.MAIL_POP, "putPopMailServer", PopMailServerBean.class);
    }

    @Test
    public void testPutPopMaiLServerUpdate() throws Exception {
        final PopMailServer defaultPopMailServer = new DefaultTestPopMailServerImpl();
        doReturn(defaultPopMailServer).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer updatePopMailServer = new OtherTestPopMailServerImpl();
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(updatePopMailServer);
        final Response response = mailServerResource.putPopMailServer(requestPopMailServerBean);
        final PopMailServerBean responsePopMailServerBean = (PopMailServerBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).update(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(PopMailServerBean.from(updatePopMailServer), PopMailServerBean.from(popMailServer));
        assertEquals(requestPopMailServerBean, responsePopMailServerBean);
    }

    @Test
    public void testPutPopMaiLServerCreate() throws Exception {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(createPopMailServer);
        final Response response = mailServerResource.putPopMailServer(requestPopMailServerBean);
        final PopMailServerBean responsePopMailServerBean = (PopMailServerBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        PopMailServerBean from1 = PopMailServerBean.from(createPopMailServer);
        PopMailServerBean from2 = PopMailServerBean.from(popMailServer);

        assertEquals(from1, from2);
        assertEquals(requestPopMailServerBean, responsePopMailServerBean);
    }

    @Test
    public void testPutPopMaiLServerWithoutPort() throws Exception {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        createPopMailServer.setPort(null);

        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(createPopMailServer);
        final Response response = mailServerResource.putPopMailServer(requestPopMailServerBean);
        final PopMailServerBean responsePopMailServerBean = (PopMailServerBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(createPopMailServer.getMailProtocol().getDefaultPort(), popMailServer.getPort());
        assertEquals(requestPopMailServerBean, responsePopMailServerBean);
    }

    @Test
    public void testPutPopMaiLServerException() throws Exception {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();
        doThrow(new MailException("POP test exception")).when(mailServerManager).create(any());

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(createPopMailServer);
        final Response response = mailServerResource.putPopMailServer(requestPopMailServerBean);
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

}